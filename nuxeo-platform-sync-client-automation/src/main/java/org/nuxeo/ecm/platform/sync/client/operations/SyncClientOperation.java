/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Sun Seng David TAN <stan@nuxeo.com>
 */
package org.nuxeo.ecm.platform.sync.client.operations;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.sync.api.SynchronizeService;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.runtime.api.Framework;

/**
 * Perform the synchronization through an operation
 *
 * @author Sun Seng David TAN <stan@nuxeo.com>
 *
 */
@Operation(id = SyncClientOperation.ID, category = Constants.CAT_SERVICES, label = "Synchronize with another nuxeo server", description = "Synchronize with another nuxeo server")
public class SyncClientOperation {

    public final static String ID = "Synchronization.ClientSync";

    @Context
    protected CoreSession session;

    @Param(name = "host", required = false)
    protected String host;

    @Param(name = "username", required = false)
    protected String username;

    @Param(name = "password", required = false)
    protected String password;

    @Param(name = "port", required = false)
    protected int port;

    @OperationMethod
    public void test() throws Exception {
        SynchronizeDetails syncDetails = new SynchronizeDetails();
        syncDetails.setHost(host);
        syncDetails.setUsername(username);
        syncDetails.setPassword(password);
        syncDetails.setPort(port);

        SynchronizeService frameworkService = Framework.getLocalService(SynchronizeService.class);

        frameworkService.doSynchronize(session, syncDetails);

    }

}
