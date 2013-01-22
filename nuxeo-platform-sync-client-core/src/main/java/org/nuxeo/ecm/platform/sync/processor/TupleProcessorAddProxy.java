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
package org.nuxeo.ecm.platform.sync.processor;

import org.apache.log4j.Logger;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.platform.sync.utils.ImportUtils;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

/**
 * Implementing class for processing a tuple. It adds a new proxy document. The
 * documents are imported by the CoreSession using the contextual data provided
 * by the server. They need to be added after versions import.
 *
 * @author rux
 *
 */
public class TupleProcessorAddProxy extends TupleProcessorAdd {

    private static final Logger log = Logger.getLogger(TupleProcessorAddProxy.class);

    public TupleProcessorAddProxy(CoreSession session, NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    @Override
    public void process() throws ClientException {
        log.debug("Starting the process of adding proxy " + tuple.getClientId()
                + " on the client side: " + name);
        // in case there is a proxy, the document model will have the type
        // ecm:proxy
        localDocument = new DocumentModelImpl((String) null,
                CoreSession.IMPORT_PROXY_TYPE, tuple.getClientId(), new Path(
                        name), null, null, new PathRef(parentPath), null, null,
                null, null);
        localDocument.putContextData(CoreSession.IMPORT_PROXY_TARGET_ID,
                ImportUtils.getContextDataInfo(contextData,
                        CoreSession.IMPORT_PROXY_TARGET_ID));
        localDocument.putContextData(CoreSession.IMPORT_PROXY_VERSIONABLE_ID,
                ImportUtils.getContextDataInfo(contextData,
                        CoreSession.IMPORT_PROXY_VERSIONABLE_ID));

        runUnrestrictedImport();
        // no need to add properties too
        log.debug("Finishing the process of adding proxy "
                + tuple.getClientId() + " on the client side: " + name);
    }
}