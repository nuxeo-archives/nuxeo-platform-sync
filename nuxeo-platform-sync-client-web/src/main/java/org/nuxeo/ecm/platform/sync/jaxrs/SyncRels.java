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

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.nuxeo.ecm.core.api.ClientException;
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
