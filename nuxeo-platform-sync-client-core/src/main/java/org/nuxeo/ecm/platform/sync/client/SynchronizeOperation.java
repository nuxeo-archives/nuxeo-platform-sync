/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Sun Seng David TAN <stan@nuxeo.com>
 */
package org.nuxeo.ecm.platform.sync.client;

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
 */
@Operation(id = SynchronizeOperation.ID, category = Constants.CAT_SERVICES, label = "Synchronize with another nuxeo server", description = "Synchronize with another nuxeo server")
public class SynchronizeOperation {

    public final static String ID = "Synchronization.ClientSync";

    @Context
    protected CoreSession session;

    @Param(name = "host", required = false)
    protected String host;

    @Param(name = "username", required = false)
    protected String username;

    @Param(name = "password", required = false)
    protected String password;

    @Param(name = "diffPolicy", required = false)
    protected String diffPolicy;

    @Param(name = "port", required = false)
    protected int port = -1;

    @OperationMethod
    public void test() {
        SynchronizeDetails syncDetails = new SynchronizeDetails();
        if (host != null)
            syncDetails.setHost(host);
        if (username != null)
            syncDetails.setUsername(username);
        if (password != null)
            syncDetails.setPassword(password);
        if (port != 01)
            syncDetails.setPort(port);
        if (diffPolicy != null)
            syncDetails.setDiffPolicy(diffPolicy);

        SynchronizeService frameworkService = Framework.getLocalService(SynchronizeService.class);

        frameworkService.synchronize(session, syncDetails, "QUERY_ALL");

    }

}
