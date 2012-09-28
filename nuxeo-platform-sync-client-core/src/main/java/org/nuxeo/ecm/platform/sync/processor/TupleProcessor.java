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
package org.nuxeo.ecm.platform.sync.processor;

import java.util.List;

import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.platform.sync.client.ImportConfiguration;
import org.nuxeo.ecm.platform.sync.utils.ImportUtils;
import org.nuxeo.ecm.platform.sync.webservices.generated.ContextDataInfo;
import org.nuxeo.ecm.platform.sync.webservices.generated.DocumentSnapshot;
import org.nuxeo.ecm.platform.sync.webservices.generated.FlagedDocumentSnapshot;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;
import org.nuxeo.ecm.platform.sync.webservices.generated.WSSynchroServerModule;

/**
 * The base class for processing a tuple. It holds the common processing.
 *
 * @author rux
 *
 */
public abstract class TupleProcessor {

    protected NuxeoSynchroTuple tuple;

    protected List<ContextDataInfo> contextData;

    protected String parentPath;

    protected String name;

    protected DocumentSnapshot remoteDocument;

    protected DocumentModel localDocument;

    protected CoreSession session;

    protected FlagedDocumentSnapshot documentSnapshot;

    protected WSSynchroServerModule synchroServerModule;

    protected ImportConfiguration importConfiguration;

    /**
     * This will keep all the information about the synchronization process.
     */
    protected SynchronizeDetails synchronizeDetails;

    protected TupleProcessor(CoreSession session, NuxeoSynchroTuple tuple) {

        this.session = session;
        this.tuple = tuple;
        this.contextData = tuple.getContextData();
        this.parentPath = ImportUtils.getParentPath(tuple.getPath());
        this.name = ImportUtils.getName(tuple.getPath());
    }

    /**
     * Creates the right instance: method factory.
     *
     * @param tuple
     * @param isNew
     * @return
     */
    public static TupleProcessor createProcessor(CoreSession session,
            NuxeoSynchroTuple tuple, WSSynchroServerModule wsSyncro,
            boolean isNew, SynchronizeDetails synchronizeDetails, ImportConfiguration importConfiguration) {
        TupleProcessor ret = null;
        if (isNew) {
            if (tuple.isVersion()) {
                ret = new TupleProcessorAddVersion(session, tuple);
            } else if (tuple.isProxy()) {
                ret = new TupleProcessorAddProxy(session, tuple);
            } else {
                ret = new TupleProcessorAddDocument(session, tuple);
            }
        } else {
            if (tuple.isVersion()) {
                ret = new TupleProcessorUpdateVersion(session, tuple);
            } else if (tuple.isProxy()) {
                ret = new TupleProcessorUpdateProxy(session, tuple);
            } else {
                ret = new TupleProcessorUpdateDocument(session, tuple);
            }
        }
        ret.setSynchroServerModule(wsSyncro);
        ret.setSynchronizeDetails(synchronizeDetails);
        ret.setImportConfiguration(importConfiguration);
        return ret;
    }

    public static TupleProcessor createProcessor(CoreSession session,
            NuxeoSynchroTuple tuple, WSSynchroServerModule wsSyncro,
            boolean isNew, SynchronizeDetails synchronizeDetails) {
        return createProcessor(session, tuple, wsSyncro, isNew, synchronizeDetails, null);
    }

    /**
     * Processes the tuple. Actual implementation in the implementors. Be
     * careful to catch the ClientException to make possible continuing the
     * process.
     *
     * @throws ClientException
     */
    public abstract void process() throws ClientException;

    /**
     * Obtains the WS document representation. It returns it and also sets it as
     * remoteDocument. Never returns null, but throws exceptions.
     */
    protected FlagedDocumentSnapshot getDocumentSnapshot()
            throws ClientException {
        if (documentSnapshot == null) {
            documentSnapshot = synchroServerModule.getDocumentByIdWithoutBlob(tuple.getServerId());
        }
        return documentSnapshot;
    }

    protected String translatePath(String path) {
        if (importConfiguration == null || importConfiguration.getDocumentRootPath() == null) {
            return path;
        }
        Path p = new Path(path).removeFirstSegments(importConfiguration.getPathSegmentsToRemove());
        p = importConfiguration.getDocumentRootPath().append(p).makeAbsolute();
        return p.toString();
    }

    public void setSynchroServerModule(WSSynchroServerModule synchroServerModule) {
        this.synchroServerModule = synchroServerModule;
    }

    public void setSynchronizeDetails(SynchronizeDetails synchronizeDetails) {
        this.synchronizeDetails = synchronizeDetails;
    }

    public void setImportConfiguration(ImportConfiguration importConfiguration) {
        this.importConfiguration = importConfiguration;
        this.parentPath = translatePath(this.parentPath);
    }

}
