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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.sync.api.SynchronizeReport;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.webengine.model.WebObject;

/**
 * @author "Stephane Lacoin (aka matic) slacoin@nuxeo.com"
 */
@WebObject(type = "Docs")
public class SyncDocs extends SyncOp {

    protected String query = "SELECT * FROM Document";

    public String getQuery() {
        return query;
    }

    @GET
    public Object doGet() throws MalformedURLException {
        return getView("index");
    }

    @POST
    public Object doPost(@FormParam("query") @DefaultValue("SELECT * from Document") String query) {
        this.query = query;
        final SyncHost host = host();
        SynchronizeDetails details = host.details();
        CoreSession session = getContext().getCoreSession();
        SynchronizeReport report = root().service.synchronizeDocuments(session, details, query);
        return getView("report").arg("report", report).arg("a", host.uri(false).toASCIIString());
    }
}
