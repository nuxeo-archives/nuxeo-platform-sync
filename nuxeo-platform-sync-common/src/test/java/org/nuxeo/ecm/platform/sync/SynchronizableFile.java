package org.nuxeo.ecm.platform.sync;

import org.nuxeo.ecm.platform.sync.adapter.DefaultSynchronizableDocumentImpl;

public class SynchronizableFile extends DefaultSynchronizableDocumentImpl {

    @Override
    public String getId() {
        try {
            return (String) documentModel.getPropertyValue("uid:uid");
        } catch (Exception e) {
            return null;
        }
    }

}
