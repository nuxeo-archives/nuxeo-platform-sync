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
import java.net.URI;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.sync.api.SynchronizeReport;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

/**
 * @author "Stephane Lacoin (aka matic) slacoin@nuxeo.com"
 *
 */
@WebObject(type="Vocs")
public class SyncVocs extends DefaultObject {
    
    
    public URI location() {
        return syncHost().location;
    }
    
    protected SyncHost syncHost() {
        return (SyncHost)getPrevious();
    }
    
    protected SyncRoot syncRoot() {
        return syncHost().syncRoot();
    }
     
    protected boolean dryrun = true;
    
    public boolean dryrun() {
        return dryrun;
    }
    
    @GET
    public Object doGet() throws MalformedURLException {
        return getView("index");
    }
    
    @POST
    public Object doPost(@FormParam("dryrun") Boolean dryrun) throws MalformedURLException, ClientException {
        this.dryrun = dryrun;
        SynchronizeDetails details = syncHost().details();
        details.setDryRun(dryrun);
        SynchronizeReport report=
                syncRoot().service.synchronizeVocabularies(details);
        return getView("report").arg("report", report);
    }
}
