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
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

/**
 * Implementing class for processing a tuple. It updates a normal document.
 *
 * @author rux
 */
public class TupleProcessorUpdateDocument extends TupleProcessorUpdate {

    private static final Logger log = Logger.getLogger(TupleProcessorUpdateDocument.class);

    public TupleProcessorUpdateDocument(CoreSession session, NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    @Override
    public void process() {
        log.debug("Starting the process of updating live document " + tuple.getClientId() + " on the client side: "
                + name);
        localDocument = session.getDocument(new IdRef(tuple.getClientId()));
        setProperties();
        setACE();
        updateDocument();
        log.debug("Finishing the process of updating live document " + tuple.getClientId() + " on the client side: "
                + name);
    }

}
