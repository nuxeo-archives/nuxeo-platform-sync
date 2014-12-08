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
