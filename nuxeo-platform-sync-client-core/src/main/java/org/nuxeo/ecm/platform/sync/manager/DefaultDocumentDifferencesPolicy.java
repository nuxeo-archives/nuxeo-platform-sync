package org.nuxeo.ecm.platform.sync.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
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

    public void process(DocumentModelList availableDocs,
            List<NuxeoSynchroTuple> tuples,
            List<NuxeoSynchroTuple> addedTuples,
            List<NuxeoSynchroTuple> modifiedTuples, List<String> deletedIds,
            List<NuxeoSynchroTuple> movedTuples) throws Exception {

        MonitorProvider.getMonitor().beginTask("Computing the differences",
                availableDocs.size());
        addedTuples.addAll(tuples);
        List<NuxeoSynchroTuple> versionTuplesToAdd = new ArrayList<NuxeoSynchroTuple>();

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
                        // Doc has a null modification date (and probably no
                        // line at all in the dublincore table), this is
                        // inconsistent => need to fix it.
                        // https://jira.nuxeo.com/browse/NXP-10828
                        // In the case of a live document, update it.
                        // In the case of a version, since update is not
                        // implemented (should not be), delete/add it.
                        // Do the same for a proxy, since a version cannot be
                        // deleted if it has a proxy.
                        log.debug("Doc "
                                + doc.getId()
                                + " ("
                                + (doc.isProxy() ? "proxy"
                                        : (doc.isVersion() ? "version" : "live"))
                                + ") has no modification date (problem at last import) => delete then add it back");
                        if (doc.isVersion() || doc.isProxy()) {
                            remove = true;
                            addedTuples.add(tuple);
                        } else {
                            tuple.setClientId(doc.getId());
                            modifiedTuples.add(tuple);
                        }
                    } else if (tuple.getLastModification() == 0) {
                        log.debug("Doc "
                                + tuple.getClientId()
                                + " is skipped because it is a version without read access - got from a proxy");
                    } else if (doc.getCurrentLifeCycleState() == null) {
                        // Doc has a null life cycle state, this is inconsistent
                        // => need to fix it.
                        // See https://jira.nuxeo.com/browse/NXP-10982
                        // In the case of a live document, update it.
                        // In the case of a version, since update is not
                        // implemented (should not be), delete/add it.
                        // Do the same for a proxy, since a version cannot be
                        // deleted if it has a proxy.
                        log.debug("Doc "
                                + doc.getId()
                                + " ("
                                + (doc.isProxy() ? "proxy"
                                        : (doc.isVersion() ? "version" : "live"))
                                + ") has no life cycle state (problem at last import) => delete then add it back");
                        if (doc.isVersion() || doc.isProxy()) {
                            remove = true;
                            addedTuples.add(tuple);
                            if (doc.isProxy()) {
                                DocumentModel version = doc.getCoreSession().getSourceDocument(
                                        doc.getRef());
                                log.debug("Doc "
                                        + doc.getId()
                                        + " is a proxy => delete then add back its target version "
                                        + version.getId());
                                if (!deletedIds.contains(version.getId())) {
                                    deletedIds.add(version.getId());
                                }
                                NuxeoSynchroTuple versionTuple = getTuple(
                                        tuples, version.getId());
                                if (!versionTuplesToAdd.contains(versionTuple)) {
                                    versionTuplesToAdd.add(versionTuple);
                                }
                            }
                        } else {
                            tuple.setClientId(doc.getId());
                            modifiedTuples.add(tuple);
                        }
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
        addedTuples.addAll(versionTuplesToAdd);
    }

    protected NuxeoSynchroTuple getTuple(List<NuxeoSynchroTuple> tuples,
            String versionId) throws ClientException {
        for (NuxeoSynchroTuple tuple : tuples) {
            if (versionId.equals(tuple.getClientId())) {
                return tuple;
            }
        }
        throw new ClientException("Cannot find tuple matching version "
                + versionId);
    }

}