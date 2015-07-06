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
