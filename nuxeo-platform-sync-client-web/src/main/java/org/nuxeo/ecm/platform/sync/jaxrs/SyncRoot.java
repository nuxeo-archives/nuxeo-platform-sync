/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Nuxeo
 */

package org.nuxeo.ecm.platform.sync.jaxrs;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.nuxeo.ecm.platform.sync.api.SynchronizeService;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

import com.sun.jersey.api.core.InjectParam;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
@Path("/sync")
@Produces("text/html;charset=UTF-8")
@WebObject(type="SyncRoot")
public class SyncRoot extends ModuleRoot {


    protected final SynchronizeService service =
            Framework.getLocalService(SynchronizeService.class);
    
    protected final SynchronizeDetails defaultDetails = 
            service.getDefaultSynchronizeDetails();
    
    protected static class Params {
        
        protected @DefaultValue("http://Administrator:Administrator@localhost:8080/nuxeo") @QueryParam("url") @FormParam("url") String url;
        protected @DefaultValue("default") @QueryParam("diffPolicy") @FormParam("diffPolicy") String diffPolicy;
        
        public static Params fromDetails(SynchronizeDetails details) {
            Params params = new Params();
            params.url = String.format("%s://%s:%s@%s/%s", details.getProtocol(), details.getUrl(), details.getPassword(), details.getHost(), details.getContextPath());
            params.diffPolicy = details.getDiffPolicy();
            return params;
        }
        
        public static SynchronizeDetails toDetails(Params params) throws MalformedURLException {
            SynchronizeDetails details = new SynchronizeDetails();
            URL url = new URL(params.url);
            details.setProtocol(url.getProtocol());
            details.setHost(url.getHost());
            int port = url.getPort();
            if (port == -1) port = url.getDefaultPort();
            details.setPort(port);
            details.setContextPath(url.getPath());
            String[] credentials = url.getUserInfo().split(":");
            if (credentials.length > 0) {
                details.setUsername(credentials[0]);
            }
            if (credentials.length > 1) {
                details.setPassword(credentials[1]);
            }
            return details;
        }
    }
    
    protected Params params;
    
    @Path("documents")
    public Object navigateDocuments(@InjectParam Params params) {
        this.params = params;
        return newObject("SyncDocs");
    }
    
    @Path("vocabularies")
    public Object navigateVocabularies(@InjectParam Params params) {
        this.params = params;
        return newObject("SyncVocs");
    }
    
    @Path("relations")
    public Object navigateRelations(@InjectParam Params params) {
        this.params = params;
        return newObject("SyncRels");
    }
    
    @POST
    @Path("synchronize")
    @Deprecated
    public String synchronize(@FormParam("queryName") String query) throws Exception {
        ((SyncDocs)newObject("SyncDocs")).doSynch(query);
        return "";
    }

    @GET
    public Object doGet(@InjectParam Params params) {
        this.params = params;
        return getView("index");
    }

}
