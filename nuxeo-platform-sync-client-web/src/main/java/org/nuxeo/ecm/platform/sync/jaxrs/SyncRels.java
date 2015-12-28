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
 *     "Stephane Lacoin (aka matic) slacoin@nuxeo.com"
 */
package org.nuxeo.ecm.platform.sync.jaxrs;

import java.net.MalformedURLException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.nuxeo.ecm.platform.sync.api.SynchronizeReport;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.webengine.model.WebObject;

/**
 * @author "Stephane Lacoin (aka matic) slacoin@nuxeo.com"
 */
@WebObject(type = "Rels")
public class SyncRels extends SyncOp {

    @GET
    public Object doGet() throws MalformedURLException {
        return run();
    }

    @POST
    public Object doPost() throws MalformedURLException {
        return run();
    }

    protected Object run() {
        final SyncHost host = host();
        SynchronizeDetails details = host.details();
        SynchronizeReport report = root().service.synchronizeRelations(details);
        return getView("report").arg("report", report).arg("action", host.uri(false).toASCIIString());
    }
}
