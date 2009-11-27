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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.EventServiceAdmin;
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
        WSSynchroServerModule wsas = null;
        List<NuxeoSynchroTuple> tuples = null;
        String query = null;
        try {
            W3CEndpointReference ref = entrance.accessWSSynchroServerModule(
                    session.getRepositoryName(),
                    synchronizeDetails.getUsername(),
                    synchronizeDetails.getPassword());
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
        // first remove documents no more availanle on online server
        removeDocuments();
        // disable the listener in order to keep the original dc:modified
        EventServiceAdmin eventAdmin = Framework.getLocalService(EventServiceAdmin.class);
        eventAdmin.setListenerEnabledFlag(DC_LISTENER, false);
        try {
            // add the new documents
            addDocuments(wsas);
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
        for (NuxeoSynchroTuple tuple : addedTuples) {
            if (!tuple.isVersion() && !tuple.isProxy()) {
                TupleProcessor tupleProcessor = TupleProcessor.createProcessor(
                        session, tuple, wsas, true, synchronizeDetails, importConfiguration);
                tupleProcessor.process();
            }
        }
        // second add versions
        for (NuxeoSynchroTuple tuple : addedTuples) {
            if (tuple.isVersion()) {
                TupleProcessor tupleProcessor = TupleProcessor.createProcessor(
                        session, tuple, wsas, true, synchronizeDetails, importConfiguration);
                tupleProcessor.process();
            }
        }
        // last add proxies
        for (NuxeoSynchroTuple tuple : addedTuples) {
            if (tuple.isProxy()) {
                TupleProcessor tupleProcessor = TupleProcessor.createProcessor(
                        session, tuple, wsas, true, synchronizeDetails, importConfiguration);
                tupleProcessor.process();
            }
        }
    }

    private void updateDocuments(WSSynchroServerModule wsas)
            throws ClientException {

        // update modified documents
        for (NuxeoSynchroTuple tuple : modifiedTuples) {
            TupleProcessor tupleProcessor = TupleProcessor.createProcessor(
                    session, tuple, wsas, false, synchronizeDetails, importConfiguration);
            tupleProcessor.process();
        }

    }

    private void removeDocuments() throws ClientException {
        new RemoveDocumentsUnrestricted(session, deletedIds).runUnrestricted();
    }

    private void processDifferences(List<NuxeoSynchroTuple> tuples, String queryName)
            throws ClientException {
        log.info("Getting the differences with the server ...");
        try {

            // make the query the client side to get all the documents
            UnrestrictedSessionRunQuery query = new UnrestrictedSessionRunQuery(
                    session, queryName);
            query.runUnrestricted();
            DocumentModelList availableDocs = query.result;

            if (documentDifferencesPolicy != null) {
                documentDifferencesPolicy.process(availableDocs, tuples,
                        addedTuples, modifiedTuples, deletedIds);
            }
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    private void prepareLists() {
        addedTuples = new LinkedList<NuxeoSynchroTuple>();
        modifiedTuples = new LinkedList<NuxeoSynchroTuple>();
        deletedIds = new LinkedList<String>();
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

            List<IdRef> refs = new ArrayList<IdRef>(deletedIds.size());
            for (String id : deletedIds) {
                refs.add(new IdRef(id));
            }

            try {
                session.removeDocuments(refs.toArray(new IdRef[0]));
                session.save();
            } catch (Exception e) {
                log.warn(e);
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

}
