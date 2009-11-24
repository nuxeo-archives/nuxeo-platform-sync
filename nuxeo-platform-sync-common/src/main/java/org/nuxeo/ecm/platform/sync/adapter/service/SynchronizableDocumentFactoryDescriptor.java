/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 * $Id$
 */

package org.nuxeo.ecm.platform.sync.adapter.service;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.platform.sync.adapter.SynchronizableDocument;

@XObject(value = "syncDocument")
public class SynchronizableDocumentFactoryDescriptor {

    @XNode("@schema")
    private String schema;

    @XNode("@enabled")
    private boolean enabled = true;

    @XNode("@class")
    private Class<SynchronizableDocument> adapterClass;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Class<SynchronizableDocument> getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(Class<SynchronizableDocument> adapterClass) {
        this.adapterClass = adapterClass;
    }

}
