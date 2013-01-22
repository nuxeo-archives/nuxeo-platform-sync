/*
 * (C) Copyright 2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 */
package org.nuxeo.ecm.platform.sync.manager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.log4j.Logger;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.EventServiceAdmin;
import org.nuxeo.ecm.platform.sync.api.SynchronizeReport;
import org.nuxeo.ecm.platform.sync.api.exception.SynchronizationException;
import org.nuxeo.ecm.platform.sync.api.util.MonitorProvider;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.platform.sync.client.ImportConfiguration;
import org.nuxeo.ecm.platform.sync.processor.TupleProcessor;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoWSMainEntrancePoint;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoWSMainEntrancePointService;
import org.nuxeo.ecm.platform.sync.webservices.generated.WSSynchroServerModule;
import org.nuxeo.ecm.platform.sync.webservices.generated.WSSynchroServerModuleService;
import org.nuxeo.runtime.api.Framework;

/**
 * The manager to take care the documents set synchronization. It compares the
 * tuples received with the local situation and operates the changes.
 *
 * @author rux
 *
 */
public class DocumentsSynchronizeManager {

    private static final Logger log = Logger.getLogger(DocumentsSynchronizeManager.class);

    private static final String DC_LISTENER = "dclistener";

    private CoreSession session;

    private SynchronizeDetails synchronizeDetails;

    // this list will keep the new added documents from the server
    private List<NuxeoSynchroTuple> addedTuples;

    // this list will keep the modified documents from the server
    private List<NuxeoSynchroTuple> modifiedTuples;

    // this list will keep the deleted documents from the server
    private List<String> deletedIds;

    private String queryName;

    private ImportConfiguration importConfiguration;

    private DocumentDifferencesPolicy documentDifferencesPolicy;

    // this list will keep the documents that need to be move
    private List<NuxeoSynchroTuple> movedTuples;

    public DocumentsSynchronizeManager(CoreSession session,
            SynchronizeDetails synchronizeDetails, String queryName,
            ImportConfiguration importConfiguration,
            DocumentDifferencesPolicy documentDifferencesPolicy) {

        this.session = session;
        this.synchronizeDetails = synchronizeDetails;
        this.queryName = queryName;
        this.importConfiguration = importConfiguration;
        this.documentDifferencesPolicy = documentDifferencesPolicy;
        prepareLists();
    }

    public void run() throws ClientException {
        // first obtain the list of tuples from server based on the custom query
        NuxeoWSMainEntrancePoint entrance = new NuxeoWSMainEntrancePointService().getNuxeoWSMainEntrancePointPort();

        String wsaddress = NuxeoWSMainEntrancePointService.NUXEOWSMAINENTRANCEPOINTSERVICE_WSDL_LOCATION.toString().replace(
                "wssyncroentry?wsdl", "");

        ((BindingProvider) entrance).getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                wsaddress + "wssyncroentry");
        WSSynchroServerModule wsas = null;
        List<NuxeoSynchroTuple> tuples = null;
        String query = null;
        try {
            W3CEndpointReference ref = entrance.accessWSSynchroServerModule(
                    session.getRepositoryName(),
                    synchronizeDetails.getUsername(),
                    synchronizeDetails.getPassword());

            SetPrivateAdressUriFromEndPointReference(ref, wsaddress
                    + "wssyncroserver");

            wsas = new WSSynchroServerModuleService().getPort(ref,
                    WSSynchroServerModule.class);

            if (queryName == null) {
                tuples = wsas.getAvailableDocumentList();
                query = wsas.getQueryAvailableDocumentList();
            } else {
                // get the information about the documents from the server
                tuples = wsas.getAvailableDocumentListWithQuery(queryName);
                // get the query to run from the server
                query = wsas.getQueryAvailableDocumentListWithQuery(queryName);
            }
        } catch (Exception e) {
            log.debug("Problems retrieving the WSSynchroServerModule ...");
            throw new ClientException(e);
        }
        // computes the diffs
        processDifferences(tuples, query);
        // first remove documents no more available on online server
        if (synchronizeDetails.getDryRun()) {
            return;
        }
        removeDocuments();
        // disable the listener in order to keep the original dc:modified
        EventServiceAdmin eventAdmin = Framework.getLocalService(EventServiceAdmin.class);
        eventAdmin.setListenerEnabledFlag(DC_LISTENER, false);
        try {
            // add the new documents
            addDocuments(wsas);
            // then move documents
            moveDocuments();
            // and update the modified ones
            updateDocuments(wsas);
        } catch (ClientException e) {
            log.error(e);
            throw new ClientException(e);
        } finally {
            // enable the listener
            eventAdmin.setListenerEnabledFlag(DC_LISTENER, true);
        }
    }

    private void addDocuments(WSSynchroServerModule wsas)
            throws ClientException {
        // first add normal documents
        MonitorProvider.getMonitor().beginTask(
                "Adding documents (" + addedTuples.size() + ")",
                addedTuples.size());
        for (NuxeoSynchroTuple tuple : addedTuples) {
            if (!tuple.isVersion() && !tuple.isProxy()) {
                TupleProcessor tupleProcessor = TupleProcessor.createProcessor(
                        session, tuple, wsas, true, synchronizeDetails,
                        importConfiguration);
                tupleProcessor.process();
                MonitorProvider.getMonitor().worked(1);
            }
            checkSynchronizeStatus();
        }
        // second add versions
        for (NuxeoSynchroTuple tuple : addedTuples) {
            if (tuple.isVersion()) {
                TupleProcessor tupleProcessor = TupleProcessor.createProcessor(
                        session, tuple, wsas, true, synchronizeDetails,
                        importConfiguration);
                tupleProcessor.process();
                MonitorProvider.getMonitor().worked(1);
            }
            checkSynchronizeStatus();
        }
        // last add proxies
        for (NuxeoSynchroTuple tuple : addedTuples) {
            if (tuple.isProxy()) {
                TupleProcessor tupleProcessor = TupleProcessor.createProcessor(
                        session, tuple, wsas, true, synchronizeDetails,
                        importConfiguration);
                tupleProcessor.process();
                MonitorProvider.getMonitor().worked(1);
            }
            checkSynchronizeStatus();
        }
    }

    private void moveDocuments() throws ClientException {

        // move documents
        new MoveDocumentsUnrestricted(session, movedTuples).runUnrestricted();

    }

    private void updateDocuments(WSSynchroServerModule wsas)
            throws ClientException {

        // update modified documents
        MonitorProvider.getMonitor().beginTask("Updating documents",
                modifiedTuples.size());
        for (NuxeoSynchroTuple tuple : modifiedTuples) {
            log.debug("Will start updating modified tuple: "
                    + tuple.getClientId());
            TupleProcessor tupleProcessor = TupleProcessor.createProcessor(
                    session, tuple, wsas, false, synchronizeDetails,
                    importConfiguration);
            tupleProcessor.process();
            MonitorProvider.getMonitor().worked(1);
        }

    }

    private void removeDocuments() throws ClientException {
        new RemoveDocumentsUnrestricted(session, deletedIds).runUnrestricted();
    }

    private void processDifferences(List<NuxeoSynchroTuple> tuples,
            String queryName) throws ClientException {
        log.info("Getting the differences with the server ...");
        try {

            // make the query the client side to get all the documents
            UnrestrictedSessionRunQuery query = new UnrestrictedSessionRunQuery(
                    session, queryName);
            query.runUnrestricted();
            DocumentModelList availableDocs = query.result;

            if (documentDifferencesPolicy != null) {
                documentDifferencesPolicy.process(availableDocs, tuples,
                        addedTuples, modifiedTuples, deletedIds, movedTuples);
            }

            if (importConfiguration != null
                    && importConfiguration.getGenerateNewId()) {
                for (NuxeoSynchroTuple tuple : addedTuples) {
                    tuple.setClientId(UUID.randomUUID().toString());
                }
            }
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    private void prepareLists() {
        addedTuples = new LinkedList<NuxeoSynchroTuple>();
        modifiedTuples = new LinkedList<NuxeoSynchroTuple>();
        deletedIds = new LinkedList<String>();
        movedTuples = new LinkedList<NuxeoSynchroTuple>();
    }

    protected static class RemoveDocumentsUnrestricted extends
            UnrestrictedSessionRunner {

        private List<String> deletedIds;

        public RemoveDocumentsUnrestricted(CoreSession session,
                List<String> deletedIds) {
            super(session);
            this.deletedIds = deletedIds;
        }

        @Override
        public void run() throws ClientException {

            MonitorProvider.getMonitor().beginTask(
                    "Removing obsolete documents", 4);
            List<IdRef> refs = new ArrayList<IdRef>();
            List<IdRef> proxyRefs = new ArrayList<IdRef>();
            List<DocumentModel> versionDocs = new ArrayList<DocumentModel>();
            for (String id : deletedIds) {
                IdRef docRef = new IdRef(id);
                DocumentModel model = session.getDocument(docRef);
                if (model.isProxy()) {
                    // versions need to be removed after proxies
                    proxyRefs.add(docRef);
                } else if (model.isVersion()) {
                    versionDocs.add(model);
                } else {
                    refs.add(docRef);
                }
            }
            MonitorProvider.getMonitor().worked(1);

            // Remove proxies
            // Cannot use CoreSession#removeDocuments(DocumentRef[] docRefs)
            // because in case of failure on one proxy removal, all remaining
            // proxies to delete would not be removed. This is a problem for
            // versions to delete that need their proxies to be deleted first
            for (IdRef idRef : proxyRefs) {
                try {
                    log.debug("Removing proxy: " + idRef);
                    session.removeDocument(idRef);
                } catch (ClientException e) {
                    log.error(e);
                }
            }
            session.save();
            MonitorProvider.getMonitor().worked(1);

            // Remove versions
            List<DocumentRef> versionWithParentRefs = new ArrayList<DocumentRef>();
            for (DocumentModel doc : versionDocs) {
                DocumentRef docRef = doc.getRef();
                if (session.getProxies(docRef, null).size() == 0) {
                    if (doc.getPath() != null) {
                        versionWithParentRefs.add(docRef);
                    } else {
                        try {
                            log.debug("Removing version: " + doc.getId());
                            session.removeDocument(docRef);
                        } catch (ClientException e) {
                            log.error(e);
                        }
                    }
                }
            }
            try {
                if (log.isDebugEnabled()) {
                    for (DocumentRef docRef : versionWithParentRefs) {
                        log.debug("Will remove version: " + docRef);
                    }
                }
                session.removeDocuments(versionWithParentRefs.toArray(new IdRef[0]));
                session.save();
                log.debug("Removed all versions with a non null parent path to delete.");
                MonitorProvider.getMonitor().worked(1);
            } catch (ClientException e) {
                log.error(e);
            }

            // Remove live docs
            try {
                if (log.isDebugEnabled()) {
                    for (IdRef idRef : refs) {
                        log.debug("Will remove live document: " + idRef);
                    }
                }
                session.removeDocuments(refs.toArray(new IdRef[0]));
                session.save();
                log.debug("Removed all live documents to delete.");
                MonitorProvider.getMonitor().worked(1);
            } catch (ClientException e) {
                log.error(e);
            }
        }
    }

    protected static class MoveDocumentsUnrestricted extends
            UnrestrictedSessionRunner {

        List<NuxeoSynchroTuple> movedTuples;

        public MoveDocumentsUnrestricted(CoreSession session,
                List<NuxeoSynchroTuple> tuples) {
            super(session);
            movedTuples = tuples;
        }

        @Override
        public void run() throws ClientException {
            MonitorProvider.getMonitor().beginTask("Moving documents",
                    movedTuples.size());
            for (NuxeoSynchroTuple tuple : movedTuples) {
                DocumentModel localDocument = session.getDocument(new IdRef(
                        tuple.getAdaptedId()));
                if (localDocument.isVersion()) {
                    continue;
                }
                Path tuplePath = new Path(tuple.getPath());
                String documentName = tuplePath.lastSegment();
                Path parentPath = tuplePath.removeLastSegments(1);
                session.move(localDocument.getRef(),
                        new PathRef(parentPath.toString()), documentName);

                MonitorProvider.getMonitor().worked(1);
            }

        }
    }

    /**
     * The unrestricted runner for running a query.
     *
     * @author rux
     *
     */
    protected static class UnrestrictedSessionRunQuery extends
            UnrestrictedSessionRunner {

        public UnrestrictedSessionRunQuery(CoreSession session, String query) {
            super(session);
            this.query = query;
            result = null;
        }

        // need to have somehow result
        public DocumentModelList result;

        // need to provide somehow the arguments
        private String query;

        @Override
        public void run() throws ClientException {
            result = session.query(query);
        }

    }

    public void checkSynchronizeStatus() throws SynchronizationException {
        if (MonitorProvider.getMonitor().isCanceled()) {
            throw new SynchronizationException(
                    "Synchronization canceled by user");
        }
    }

    private void SetPrivateAdressUriFromEndPointReference(
            W3CEndpointReference ref, String value) {
        if (ref == null) {
            return;
        }
        try {
            Field addressField = W3CEndpointReference.class.getDeclaredField("address");
            addressField.setAccessible(true);
            Object address = addressField.get(ref);
            Field uriField = addressField.getType().getDeclaredField("uri");
            uriField.setAccessible(true);
            uriField.set(address, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * @return
     */
    public SynchronizeReport getReport() {
        List<DocumentRef> added = doExtractDocRefs(addedTuples);
        List<DocumentRef> updated = doExtractDocRefs(modifiedTuples);
        List<DocumentRef> moved = doExtractDocRefs(movedTuples);
        List<DocumentRef> removed =  doExtractIdRefs(deletedIds);

        return SynchronizeReport.newDocumentsReport(added, removed, updated, moved);
    }

    protected List<DocumentRef> doExtractDocRefs(List<NuxeoSynchroTuple> tuples) {
        List<DocumentRef> refs = new ArrayList<DocumentRef>();
        for (NuxeoSynchroTuple tuple:tuples) {
            refs.add(new PathRef(tuple.getPath()));
        }
        return refs;
    }

    protected List<DocumentRef> doExtractIdRefs(List<String> ids) {
        List<DocumentRef> refs = new ArrayList<DocumentRef>();
        for (String id:deletedIds) {
            DocumentModel doc = null;
            try {
                doc = session.getDocument(new IdRef(id));
            } catch (ClientException e) {
                log.warn("Cannot get access to document " + id, e);
            }
            refs.add(new PathRef(doc.getPathAsString()));
        }
        return refs;
    }

}
