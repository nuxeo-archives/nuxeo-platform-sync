/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 */
package org.nuxeo.ecm.platform.sync.server.tuple;

import org.nuxeo.ecm.platform.api.ws.DocumentBlob;
import org.nuxeo.ecm.platform.api.ws.DocumentProperty;
import org.nuxeo.ecm.platform.api.ws.DocumentSnapshot;
import org.nuxeo.ecm.platform.api.ws.WsACE;

/**
 * It is a {@link DocumentSnapshot} extended with hasBlobs property to signal existence of blobs in the document.
 * 
 * @author rux
 */
public class FlagedDocumentSnapshot extends DocumentSnapshot {

    private static final long serialVersionUID = -4033819657709889213L;

    private boolean hasBlobs = false;

    public FlagedDocumentSnapshot() {
        super();
    }

    public FlagedDocumentSnapshot(DocumentProperty[] noBlobProperties, DocumentBlob[] blobProperties,
            String pathAsString, WsACE[] acl, boolean hasBlobs) {
        super(noBlobProperties, blobProperties, pathAsString, acl);
        this.hasBlobs = hasBlobs;
    }

    public boolean isHasBlobs() {
        return hasBlobs;
    }

    public void setHasBlobs(boolean hasBlobs) {
        this.hasBlobs = hasBlobs;
    }

}
