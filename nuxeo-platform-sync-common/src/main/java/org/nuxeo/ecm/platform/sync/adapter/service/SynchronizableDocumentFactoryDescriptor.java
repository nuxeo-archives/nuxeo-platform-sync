/*
 * (C) Copyright 2006-2009 Nuxeo SA (http://nuxeo.com/) and others.
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
