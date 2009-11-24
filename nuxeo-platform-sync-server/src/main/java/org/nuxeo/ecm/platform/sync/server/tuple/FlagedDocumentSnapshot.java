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
package org.nuxeo.ecm.platform.sync.server.tuple;

import org.nuxeo.ecm.platform.api.ws.DocumentBlob;
import org.nuxeo.ecm.platform.api.ws.DocumentProperty;
import org.nuxeo.ecm.platform.api.ws.DocumentSnapshot;
import org.nuxeo.ecm.platform.api.ws.WsACE;

/**
 * It is a {@link DocumentSnapshot} extended with hasBlobs property to signal existence of
 * blobs in the document.
 * @author rux
 *
 */
public class FlagedDocumentSnapshot extends DocumentSnapshot {

    private static final long serialVersionUID = -4033819657709889213L;
    
    private boolean hasBlobs = false;
    
    public FlagedDocumentSnapshot(){
        super();
    }

    public FlagedDocumentSnapshot(DocumentProperty[] noBlobProperties,
            DocumentBlob[] blobProperties, String pathAsString, WsACE[] acl,  boolean hasBlobs) {
        super(noBlobProperties, blobProperties, pathAsString, acl );
        this.hasBlobs = hasBlobs;
    }

    public boolean isHasBlobs() {
        return hasBlobs;
    }

    public void setHasBlobs(boolean hasBlobs) {
        this.hasBlobs = hasBlobs;
    }


}
