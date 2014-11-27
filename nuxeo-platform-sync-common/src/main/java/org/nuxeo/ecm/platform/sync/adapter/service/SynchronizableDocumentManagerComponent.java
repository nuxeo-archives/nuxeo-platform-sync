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
    public void registerContribution(Object contribution, String extensionPoint,
            ComponentInstance contributor) {
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

    public SynchronizableDocument getSynchronizableDocument(DocumentModel doc) throws InstantiationException, IllegalAccessException {
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
