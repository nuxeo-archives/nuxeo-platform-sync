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
package org.nuxeo.ecm.platform.sync.processor;

import org.apache.log4j.Logger;
import org.nuxeo.common.utils.Path;
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
 */
public class TupleProcessorUpdateProxy extends TupleProcessorUpdate {

    private static final Logger log = Logger.getLogger(TupleProcessorUpdateProxy.class);

    public TupleProcessorUpdateProxy(CoreSession session, NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    @Override
    public void process() {
        // this case is encountered when republishing
        // and overwriting existing proxy

        log.debug("Starting the process of updating proxy " + tuple.getAdaptedId() + " on the client side: " + name);

        log.debug("Removing old proxy " + tuple.getAdaptedId() + " on the client side: " + name);
        try {
            session.removeDocument((new IdRef(tuple.getAdaptedId())));
            session.save();
        } catch (Exception e) {
            log.warn(e);
        }

        log.debug("Adding updated proxy " + tuple.getAdaptedId() + " on the client side: " + name);
        localDocument = new DocumentModelImpl((String) null, CoreSession.IMPORT_PROXY_TYPE, tuple.getAdaptedId(),
                new Path(name), null, null, new PathRef(parentPath), null, null, null, null);
        localDocument.putContextData(CoreSession.IMPORT_PROXY_TARGET_ID,
                ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_PROXY_TARGET_ID));
        localDocument.putContextData(CoreSession.IMPORT_PROXY_VERSIONABLE_ID,
                ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_PROXY_VERSIONABLE_ID));

        runUnrestrictedImport();
        // no need to add properties too
        log.debug("Finishing the process of updating proxy " + tuple.getAdaptedId() + " on the client side: " + name);
    }

    private void runUnrestrictedImport() {
        new UnrestrictedImport(session, localDocument).runUnrestricted();
    }
}
