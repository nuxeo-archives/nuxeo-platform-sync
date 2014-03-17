/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     mariana
 */
package org.nuxeo.ecm.platform.sync.server.webservices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.Addressing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.VersionModel;
import org.nuxeo.ecm.core.api.facet.VersioningDocument;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.schema.types.primitives.DateType;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.sync.adapter.SynchronizableDocument;
import org.nuxeo.ecm.platform.sync.server.tuple.ContextDataInfo;
import org.nuxeo.ecm.platform.sync.server.tuple.FlagedDocumentSnapshot;
import org.nuxeo.ecm.platform.sync.server.tuple.FlagedDocumentSnapshotFactory;
import org.nuxeo.ecm.platform.sync.server.tuple.NuxeoSynchroTuple;
import org.nuxeo.runtime.api.Framework;

/**
 * @author mcedica
 */

@WebService
@Addressing
public class WSSynchroServerModule implements StatefulWebServiceManagement {

    public static final QName Q_NAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/",
            "WSSynchroServerModuleService");

    private static final Log log = LogFactory.getLog(WSSynchroServerModule.class);

    private static Map<String, BasicSession> sessions = new WeakHashMap<>();

    @Resource
    protected WebServiceContext wsContext;

    public WSSynchroServerModule() {
    }

    public WSSynchroServerModule(BasicSession _session) {
        sessions.put(_session.toString(), _session);
        // session = _session;
    }

    protected BasicSession getSession() {
        MessageContext ctx = wsContext.getMessageContext();
        String refId = EndpointReferenceUtils.getEndpointReferenceId(ctx);
        return sessions.get(refId);
    }

    @WebMethod(operationName = "getAvailableDocumentListWithQuery")
    public NuxeoSynchroTuple[] getAvailableDocumentListWithQuery(
            String queryName) throws ClientException {
        List<NuxeoSynchroTuple> availableTuples = new ArrayList<NuxeoSynchroTuple>();
        NuxeoSynchroTuple tuple = null;
        Calendar modificationDate = null;
        List<String> domainNames = new ArrayList<String>();
        try {
            getSession().login();
            CoreSession documentManager = getSession().getCoreSession();
            // invalidate cache
            documentManager.save();

            String queryString = getInlinedQuery(queryName);
            if (queryString == null) {
                queryString = getQuery(queryName);
            }
            DocumentModelList availableDocs = documentManager.query(queryString);
            DocumentModelList unrestrictedDocs = new DocumentModelListImpl();
            List<String> availableDocIds = new ArrayList<String>();
            for (DocumentModel documentModel : availableDocs) {
                if (documentModel.getType().equals("Domain")) {
                    domainNames.add(documentModel.getName());
                }
                availableDocIds.add(documentModel.getId());
            }
            for (DocumentModel documentModel : availableDocs) {
                if (documentManager.hasPermission(documentModel.getRef(),
                        SecurityConstants.READ)) {
                    modificationDate = (Calendar) documentModel.getPropertyValue("dc:modified");
                    long modificationTime = modificationDate != null ? modificationDate.getTimeInMillis()
                            : 0;
                    SynchronizableDocument syncDoc = documentModel.getAdapter(SynchronizableDocument.class);
                    tuple = new NuxeoSynchroTuple(documentModel.getId(),
                            documentModel.getId(), syncDoc.getId(),
                            documentModel.getType(),
                            documentModel.getPathAsString(), modificationTime,
                            documentModel.isProxy(), documentModel.isVersion());
                    tuple.setContextData(getContextData(documentManager,
                            documentModel, availableDocIds, unrestrictedDocs));
                    availableTuples.add(tuple);
                } else {
                    unrestrictedDocs.add(documentModel);
                }
            }
            // add also the unrestricted documents
            for (DocumentModel documentModel : unrestrictedDocs) {
                long modificationTime = 0;
                SynchronizableDocument syncDoc = documentModel.getAdapter(SynchronizableDocument.class);
                tuple = new NuxeoSynchroTuple(documentModel.getId(),
                        documentModel.getId(), syncDoc.getId(),
                        documentModel.getType(),
                        documentModel.getPathAsString(), modificationTime,
                        documentModel.isProxy(), documentModel.isVersion());
                tuple.setContextData(getContextData(documentManager,
                        documentModel, null, null));
                availableTuples.add(tuple);
            }
            // add also the root for user workspaces in case it exists
            if (!domainNames.isEmpty()) {
                UnrestrictedUserWorkspaceReader workspaceReader = new UnrestrictedUserWorkspaceReader(
                        documentManager, domainNames.get(0));
                workspaceReader.runUnrestricted();
                DocumentModel userWorkspaceRoot = workspaceReader.getUserWorkspaceRoot();
                if (userWorkspaceRoot != null) {

                    modificationDate = workspaceReader.getModificationDate();
                    long modificationTime = modificationDate != null ? modificationDate.getTimeInMillis()
                            : 0;
                    SynchronizableDocument syncDoc = userWorkspaceRoot.getAdapter(SynchronizableDocument.class);
                    tuple = new NuxeoSynchroTuple(userWorkspaceRoot.getId(),
                            userWorkspaceRoot.getId(), syncDoc.getId(),
                            userWorkspaceRoot.getType(),
                            userWorkspaceRoot.getPathAsString(),
                            modificationTime, userWorkspaceRoot.isProxy(),
                            userWorkspaceRoot.isVersion());
                    tuple.setContextData(getContextData(documentManager,
                            userWorkspaceRoot, availableDocIds,
                            unrestrictedDocs));
                    availableTuples.add(1, tuple);
                }
            }

        } catch (Exception ce) {
            log.error(ce);
            throw new ClientException(ce);

        } finally {
            if (getSession() != null) {
                // don't forget to close the session
                getSession().logout();
            }
        }
        return availableTuples.toArray(new NuxeoSynchroTuple[0]);
    }

    /**
     * Lists the Nuxeo domain tree filtering the visible to user branches. It
     * includes Comments and Tags. The representation offers enough data to
     * client to decide what more needed for full synchronization.
     *
     * @return
     * @throws ClientException
     */
    @WebMethod(operationName = "getAvailableDocumentList")
    public NuxeoSynchroTuple[] getAvailableDocumentList()
            throws ClientException {
        return getAvailableDocumentListWithQuery("QUERY_ALL");
    }

    @WebMethod(operationName = "getQueryAvailableDocumentListWithQuery")
    public String getQueryAvailableDocumentListWithQuery(String queryName)
            throws ClientException {
        String query = getInlinedQuery(queryName);
        if (query == null) {
            query = getQuery(queryName + "_CLIENT_SIDE");
        }
        if (query == null) {
            query = getQuery(queryName);
        }
        return query;
    }

    /**
     * Gets the query used to find all available documents for synchronization
     *
     * @return a string query
     * @throws ClientException
     */
    @WebMethod(operationName = "getQueryAvailableDocumentList")
    public String getQueryAvailableDocumentList() throws ClientException {
        return getQueryAvailableDocumentListWithQuery("QUERY_ALL");
    }

    /**
     * Returns a DocumentSnapshot for a given document id ; this describes a
     * documentModel without blob properties
     *
     * @return DocumentSnapshot
     * @throws ClientException
     */

    @WebMethod(operationName = "getDocumentByIdWithoutBlob")
    public FlagedDocumentSnapshot getDocumentByIdWithoutBlob(String uuid) {
        FlagedDocumentSnapshot ds = null;
        try {
            getSession().login();
            CoreSession documentManager = getSession().getCoreSession();
            DocumentModel documentModel = documentManager.getDocument(new IdRef(
                    uuid));
            ds = new FlagedDocumentSnapshotFactory().newDocumentSnapshot(documentModel);
        } catch (ClientException e) {
            log.error(e);
            DocumentSourceUnrestricted usr = new DocumentSourceUnrestricted(
                    getSession().getCoreSession(), new IdRef(uuid));
            try {
                usr.runUnrestricted();
                ds = usr.documentSnapshot;
            } catch (ClientException e1) {
                log.error(e1);
            }

        } finally {
            getSession().logout();
        }
        return ds;
    }

    public void destroySession() {
        BasicSession session = getSession();
        if (session != null) {
            sessions.remove(session);
            session.disconnect();
        }
    }

    public void keepAlive() {
        // TODO Auto-generated method stub
    }

    private ContextDataInfo[] getContextData(CoreSession documentManager,
            DocumentModel document, List<String> availableDocIds,
            DocumentModelList unrestrictedDocs) throws Exception {

        List<ContextDataInfo> listContextData = new ArrayList<ContextDataInfo>();
        DocumentModel sourceDocument = null;

        DocumentRef ref = document.getRef();
        // add needed context data
        if (document.isProxy()) {
            DocumentModel version = null;

            String importProxyTargetId = null;

            String importProxyVersionableId = null;

            // first try to get the version from which the proxy was made
            version = documentManager.getSourceDocument(ref);

            if (version != null) {
                importProxyTargetId = version.getId();
                // second try to get the source of the version from which
                // the proxy was made
                if (documentManager.hasPermission(version.getRef(),
                        SecurityConstants.VERSION)) {
                    if (version.getSourceId() != null) {
                        // TODO: importProxyVersionableId =
                        // version.getSourceId()
                        sourceDocument = documentManager.getSourceDocument(version.getRef());
                        if (sourceDocument != null) {
                            importProxyVersionableId = sourceDocument.getId();
                        }
                    }
                } else {
                    log.debug("Current logged user does not have Version security ...");
                    // an restricted user needs to get information about the
                    // proxy sources
                    DocumentSourceUnrestricted usr = new DocumentSourceUnrestricted(
                            documentManager, ref);
                    usr.runUnrestricted();
                    importProxyVersionableId = usr.sourceId;
                }
            }
            if (!availableDocIds.contains(importProxyTargetId)) {
                unrestrictedDocs.add(version);
                availableDocIds.add(importProxyTargetId);
            }
            // add proxy targetId
            listContextData.add(generateDataContextInfo(
                    CoreSession.IMPORT_PROXY_TARGET_ID, importProxyTargetId));
            // add proxy versionable id(source id)
            listContextData.add(generateDataContextInfo(
                    CoreSession.IMPORT_PROXY_VERSIONABLE_ID,
                    importProxyVersionableId));
        } else if (document.isVersion()) {
            String importVersionVersionableId = null;
            List<VersionModel> versions = null;
            String minorVer = null;
            String majorVer = null;
            if (document.getSourceId() == null) {
                // add version description
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_MAJOR, "1"));
                // add version description
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_MINOR, "0"));
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_VERSIONABLE_ID,
                        document.getId()));
                // add version label
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_LABEL,
                        document.getVersionLabel()));
            } else {
                if (documentManager.hasPermission(
                        new IdRef(document.getSourceId()),
                        SecurityConstants.READ)) {
                    sourceDocument = documentManager.getSourceDocument(ref);
                } else {
                    log.debug("Current logged user does not have Version security ...");
                    DocumentSourceUnrestricted usr = new DocumentSourceUnrestricted(
                            documentManager, ref);
                    usr.runUnrestricted();
                    importVersionVersionableId = usr.sourceId;
                    versions = usr.versionsForSourceDocument;
                    minorVer = usr.minorVer;
                    majorVer = usr.majorVer;
                }

                if (importVersionVersionableId == null) {
                    importVersionVersionableId = sourceDocument.getId();
                }
                // add versionable id(source id)
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_VERSIONABLE_ID,
                        importVersionVersionableId));
                // add version label
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_LABEL,
                        document.getVersionLabel()));

                if (versions == null) {
                    versions = documentManager.getVersionsForDocument(sourceDocument.getRef());
                }
                for (VersionModel versionModel : versions) {
                    if (versionModel.getLabel().equals(
                            document.getVersionLabel())) {
                        // add version description
                        listContextData.add(generateDataContextInfo(
                                CoreSession.IMPORT_VERSION_DESCRIPTION,
                                versionModel.getDescription()));
                        // add version creation date
                        listContextData.add(generateDataContextInfo(
                                CoreSession.IMPORT_VERSION_CREATED,
                                new DateType().encode(versionModel.getCreated())));
                        break;
                    }
                }

                if (minorVer == null || majorVer == null) {
                    VersioningDocument docVer = document.getAdapter(VersioningDocument.class);
                    minorVer = docVer.getMinorVersion().toString();
                    majorVer = docVer.getMajorVersion().toString();
                }
                // add version description
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_MAJOR, majorVer));
                // add version description
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_MINOR, minorVer));
            }
        } else {
            // add lock status
            Lock lock = document.getLockInfo();
            if (lock != null) {
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_LOCK_OWNER, lock.getOwner()));
                String createdString = ISODateTimeFormat.dateTime().print(
                        new DateTime(lock.getCreated()));
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_LOCK_CREATED, createdString));
            }
            if (document.isVersionable()
                    && documentManager.hasPermission(ref,
                            SecurityConstants.READ)) {
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_CHECKED_IN, Boolean.FALSE.toString()));
                // add the id of the last version, which represents the base
                // for
                // the current state of the document
                DocumentModel version = documentManager.getLastDocumentVersion(ref);
                if (version != null && version.getId().equals(document.getId())) {
                    listContextData.add(generateDataContextInfo(
                            CoreSession.IMPORT_BASE_VERSION_ID, version.getId()));
                }
                VersioningDocument docVer = document.getAdapter(VersioningDocument.class);
                String minorVer = docVer.getMinorVersion().toString();
                String majorVer = docVer.getMajorVersion().toString();
                // add major version
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_MAJOR, majorVer));
                // add minor version
                listContextData.add(generateDataContextInfo(
                        CoreSession.IMPORT_VERSION_MINOR, minorVer));
            }
        }

        // add current lifecycle state
        listContextData.add(generateDataContextInfo(
                CoreSession.IMPORT_LIFECYCLE_STATE,
                document.getCurrentLifeCycleState()));
        // add lifecycle policy
        listContextData.add(generateDataContextInfo(
                CoreSession.IMPORT_LIFECYCLE_POLICY,
                document.getLifeCyclePolicy()));

        return listContextData.toArray(new ContextDataInfo[0]);
    }

    private ContextDataInfo generateDataContextInfo(String dataName,
            String dataValue) {
        return new ContextDataInfo(dataName, dataValue);
    }

    private String getInlinedQuery(String query) {
        final String upperCase = query.toUpperCase();
        if (upperCase.startsWith("SELECT *")) {
            if (!upperCase.endsWith(" ORDER BY ECM:PATH")) {
                query = query.concat(" ORDER BY ecm:path");
            }
            return query;
        }
        return null;
    }

    private String getQuery(String queryName) throws ClientException {
        PageProviderService ppService = Framework.getLocalService(PageProviderService.class);
        if (ppService == null) {
            throw new ClientException("Unable to get PageProviderService");
        }
        PageProviderDefinition def = ppService.getPageProviderDefinition(queryName);
        if (def != null) {
            return def.getPattern();
        }
        return null;
    }

    /**
     * Helper class to run code with an unrestricted session.The code that will
     * be run, will provide information about the source of a document,usually
     * a version document.
     *
     * @author rux
     */
    protected static class DocumentSourceUnrestricted extends
            UnrestrictedSessionRunner {

        /**
         * The id of the source document
         */
        public String sourceId;

        /**
         * The ref of the document
         */
        public DocumentRef ref;

        /**
         * The document
         */
        public DocumentModel document;

        /**
         * The source document
         */
        public DocumentModel sourceDocument;

        /**
         * The versions of the source document
         */
        public List<VersionModel> versionsForSourceDocument;

        /**
         * The minor version of the document
         */
        public String minorVer;

        /**
         * The major version of the document
         */
        public String majorVer;

        private FlagedDocumentSnapshot documentSnapshot;

        public DocumentSourceUnrestricted(CoreSession session, DocumentRef ref) {
            super(session);
            this.ref = ref;
        }

        @Override
        public void run() throws ClientException {
            DocumentModel sourceDocument = null;
            try {
                document = session.getDocument(ref);
                // first the source of the version is retrieved
                try {
                    if (document.getSourceId() == null) {
                        throw new ClientException(
                                "Document has null source document");
                    }
                    sourceDocument = session.getSourceDocument(ref);
                    if (sourceDocument != null) {
                        sourceId = sourceDocument.getId();
                        this.sourceDocument = sourceDocument;
                        versionsForSourceDocument = session.getVersionsForDocument(sourceDocument.getRef());
                        VersioningDocument docVer = document.getAdapter(VersioningDocument.class);
                        minorVer = docVer.getMinorVersion().toString();
                        majorVer = docVer.getMajorVersion().toString();
                    }
                } catch (ClientException e) {
                    log.error(e.getMessage(), e);
                }
                documentSnapshot = new FlagedDocumentSnapshotFactory().newDocumentSnapshot(document);
            } catch (Exception e) {
                session.cancel();
                log.warn(e);
            }
        }
    }

    protected class UnrestrictedUserWorkspaceReader extends
            UnrestrictedSessionRunner {

        private DocumentModel userWorkspaceRoot;

        private Calendar modificationDate;

        private final String domainName;

        public UnrestrictedUserWorkspaceReader(CoreSession userCoreSession,
                String domainName) {
            super(userCoreSession);
            this.domainName = domainName;
        }

        @Override
        public void run() throws ClientException {

            StringBuilder uwsPath = new StringBuilder("/").append(domainName);
            uwsPath.append("/").append("UserWorkspaces");
            PathRef uwsDocRef = new PathRef(uwsPath.toString());
            if (session.exists(uwsDocRef)) {
                userWorkspaceRoot = session.getDocument(uwsDocRef);
                modificationDate = (Calendar) userWorkspaceRoot.getPropertyValue("dc:modified");
            }
        }

        public DocumentModel getUserWorkspaceRoot() {
            return userWorkspaceRoot;
        }

        public Calendar getModificationDate() {
            return modificationDate;
        }
    }

}
