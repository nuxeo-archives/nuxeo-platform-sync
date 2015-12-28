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

package org.nuxeo.ecm.platform.sync.adapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;
import org.nuxeo.ecm.platform.sync.adapter.service.SynchronizableDocumentManagerService;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:qlamerand@nuxeo.com">Quentin Lamerand</a>
 */
public class SynchronizableDocumentAdapterFactory implements DocumentAdapterFactory {

    private static final Log log = LogFactory.getLog(SynchronizableDocumentAdapterFactory.class);

    protected static SynchronizableDocumentManagerService syncDocumentService;

    protected SynchronizableDocumentManagerService getService() {
        if (syncDocumentService == null) {
            syncDocumentService = Framework.getLocalService(SynchronizableDocumentManagerService.class);
        }
        return syncDocumentService;
    }

    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        try {
            return getService().getSynchronizableDocument(doc);
        } catch (Exception e) {
            log.error(e, e);
            return null;
        }
    }

}
