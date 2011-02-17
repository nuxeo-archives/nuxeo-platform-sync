package org.nuxeo.ecm.platform.sync.manager;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.sync.adapter.SynchronizableDocument;
import org.nuxeo.ecm.platform.sync.api.util.MonitorProvider;
import org.nuxeo.ecm.platform.sync.utils.ImportUtils;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

public class DefaultDocumentDifferencesPolicy implements
        DocumentDifferencesPolicy {

    private static final Logger log = Logger.getLogger(DefaultDocumentDifferencesPolicy.class);

    @Override
    public void process(DocumentModelList availableDocs,
            List<NuxeoSynchroTuple> tuples,
            List<NuxeoSynchroTuple> addedTuples,
            List<NuxeoSynchroTuple> modifiedTuples, List<String> deletedIds,
            List<NuxeoSynchroTuple> movedTuples) throws Exception {

        MonitorProvider.getMonitor().beginTask("Computing the differences",
                availableDocs.size());
        addedTuples.addAll(tuples);

        boolean remove;
        for (DocumentModel doc : availableDocs) {
            remove = true;
            SynchronizableDocument syncDoc = doc.getAdapter(SynchronizableDocument.class);
            for (NuxeoSynchroTuple tuple : tuples) {
                String lifecycleState = ImportUtils.getContextDataInfo(
                        tuple.getContextData(),
                        CoreSession.IMPORT_LIFECYCLE_STATE);
                if (syncDoc.getId().equals(tuple.getAdaptedId())) {
                    addedTuples.remove(tuple);
                    remove = false;
                    tuples.remove(tuple);

                    // document was modified
                    Calendar modificationDate = (Calendar) doc.getPropertyValue("dc:modified");
                    // MC : test made using seconds instead of millis due to
                    // parseUsingMask ( in DataParser.java)
                    // yyyy-MM-dd'T'HH:mm:ssz used when dates properties are set
                    // on a document when listeners are disabled
                    if (modificationDate == null) {
                        log.debug(doc.getPathAsString()
                                + " is skipped because it has no modification date - problem at first import");
                    } else if (tuple.getLastModification() == 0) {
                        log.debug(tuple.getClientId()
                                + " is skipped because it is a version without read access - got from a proxy");
                    } else if (modificationDate.getTimeInMillis() / 1000 != (long) tuple.getLastModification() / 1000
                            || !doc.getCurrentLifeCycleState().equals(
                                    lifecycleState)) {
                        tuple.setClientId(doc.getId());
                        modifiedTuples.add(tuple);
                    }
                    if (tuple.getPath() != null
                            && !tuple.getPath().equals(doc.getPathAsString())) {
                        movedTuples.add(tuple);
                    }
                    break;
                }
            }
            if (remove) {
                deletedIds.add(doc.getId());
            }
            MonitorProvider.getMonitor().worked(1);
        }
    }

}
