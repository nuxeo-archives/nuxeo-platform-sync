package org.nuxeo.ecm.platform.sync.manager;

import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

public interface DocumentDifferencesPolicy {

    void process(DocumentModelList availableDocs, List<NuxeoSynchroTuple> tuples, List<NuxeoSynchroTuple> addedTuples,
            List<NuxeoSynchroTuple> modifiedTuples, List<String> deletedIds, List<NuxeoSynchroTuple> movedTuples)
            throws Exception;

}
