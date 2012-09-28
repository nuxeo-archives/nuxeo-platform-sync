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
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.platform.sync.processor.TupleProcessorAdd.UnrestrictedImport;
import org.nuxeo.ecm.platform.sync.utils.ImportUtils;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

/**
 * Implementing class for processing a tuple. It updates a new proxy document.
 * 
 * @author rux
 * 
 */
public class TupleProcessorUpdateProxy extends TupleProcessorUpdate {

    private static final Logger log = Logger.getLogger(TupleProcessorUpdateProxy.class);
    
    public TupleProcessorUpdateProxy(CoreSession session,
            NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    @Override
    public void process() throws ClientException {
        // this case is encountered when republishing 
        // and overwriting existing proxy

        log.debug("Starting the process of updating a proxy on the client side: "
                + name);

        log.debug("Removing old proxy on the client side: " + name);
        try {
            session.removeDocument((new IdRef(tuple.getAdaptedId())));
            session.save();
        } catch (Exception e) {
            log.warn(e);
        }

        log.debug("Adding updated proxy on the client side: " + name);
        localDocument = new DocumentModelImpl((String) null,
                CoreSession.IMPORT_PROXY_TYPE, tuple.getAdaptedId(), new Path(
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
        log.debug("Finishing the process of adding on the client side: " + name);
    }

    private void runUnrestrictedImport() throws ClientException {
        new UnrestrictedImport(session, localDocument).runUnrestricted();
    }
}