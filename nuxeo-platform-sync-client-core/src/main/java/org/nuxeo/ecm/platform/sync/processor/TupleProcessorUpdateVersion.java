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

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

/**
 * Implementing class for processing a tuple. It updates a new version document.
 * 
 * @author rux
 */
public class TupleProcessorUpdateVersion extends TupleProcessorUpdate {

    public TupleProcessorUpdateVersion(CoreSession session, NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    @Override
    public void process() {
        // this case is not encountered yet
    }

}
