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
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

/**
 * Implementing class for processing a tuple. It updates a normal document.
 *
 * @author rux
 *
 */
public class TupleProcessorUpdateDocument extends TupleProcessorUpdate {

    private static final Logger log = Logger.getLogger(TupleProcessorUpdateDocument.class);

    public TupleProcessorUpdateDocument(CoreSession session,
            NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    @Override
    public void process() throws ClientException {
        log.debug("Starting the process of updating document on the client side: " + name);
        localDocument = session.getDocument(new IdRef(tuple.getClientId()));
        setProperties();
        setACE();
        updateDocument();
        log.debug("Finishing the process of updating on the client side: " + name);
    }

}
