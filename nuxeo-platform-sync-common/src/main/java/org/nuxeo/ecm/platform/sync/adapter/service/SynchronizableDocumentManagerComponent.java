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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.sync.adapter.DefaultSynchronizableDocumentImpl;
import org.nuxeo.ecm.platform.sync.adapter.SynchronizableDocument;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class SynchronizableDocumentManagerComponent extends DefaultComponent implements
        SynchronizableDocumentManagerService {

    private static final Log log = LogFactory.getLog(SynchronizableDocumentManagerComponent.class);

    private List<SynchronizableDocumentFactoryDescriptor> syncDocAdapters = new ArrayList<SynchronizableDocumentFactoryDescriptor>();

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if ("synchronizableDocument".equals(extensionPoint)) {
            SynchronizableDocumentFactoryDescriptor desc = (SynchronizableDocumentFactoryDescriptor) contribution;
            if (desc.isEnabled()) {
                syncDocAdapters.add(0, desc);
            } else {
                for (SynchronizableDocumentFactoryDescriptor syncDocAdapter : syncDocAdapters) {
                    if (syncDocAdapter.getSchema().equals(desc.getSchema())) {
                        syncDocAdapters.remove(syncDocAdapter);
                        break;
                    }
                }
            }
        } else {
            log.error("Unknown extension point : " + extensionPoint);
        }
    }

    public SynchronizableDocument getSynchronizableDocument(DocumentModel doc) throws InstantiationException,
            IllegalAccessException {
        SynchronizableDocument syncDoc = null;
        for (SynchronizableDocumentFactoryDescriptor desc : syncDocAdapters) {
            if (doc.hasSchema(desc.getSchema())) {
                syncDoc = desc.getAdapterClass().newInstance();
                break;
            }
        }
        if (syncDoc == null) {
            syncDoc = new DefaultSynchronizableDocumentImpl();
        }
        syncDoc.setDocumentModel(doc);
        return syncDoc;
    }

}
