/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nuxeo
 */

package org.nuxeo.ecm.platform.sync.jaxrs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.nuxeo.ecm.platform.sync.api.SynchronizeService;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.model.Access;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
@Path("/sync")
@Produces("text/html;charset=UTF-8")
@WebObject(type = "Root", administrator = Access.GRANT)
public class SyncRoot extends ModuleRoot {

    protected SynchronizeService service = Framework.getLocalService(SynchronizeService.class);

    protected SynchronizeDetails defaultDetails = service.getDefaultDetails();

    protected URI defaultLocation() {
        try {
            return new URI(defaultDetails.getProtocol(), defaultDetails.getUsername() + ":"
                    + defaultDetails.getPassword(), defaultDetails.getHost(), defaultDetails.getPort(),
                    defaultDetails.getContextPath(), null, null);
        } catch (Exception e) {
            throw WebException.wrap("Cannot get default server location", e);
        }
    }

    protected URI location = defaultLocation();

    public URI getLocation() {
        return location;
    }

    @Path("{host}")
    public Object navigateHost(@PathParam("host") String host) {
        return newObject("Host", host);
    }

    @POST
    @Path("synchronize")
    @Deprecated
    public String synchronize(@FormParam("query") String query) {
        throw new WebException(Status.BAD_REQUEST);
    }

    @GET
    public Object doGet() {
        return getView("index");
    }

    @POST
    public Object doPost(@FormParam("host") String host, @Context UriInfo info) throws MalformedURLException,
            URISyntaxException {
        this.location = new URI("http", "Administrator:Administrator", host, 8080, "/nuxeo", null, null);
        final UriBuilder builder = info.getRequestUriBuilder().segment(host).matrixParam("location", location);
        final URI hostLocation = builder.build();
        return redirect(hostLocation.toString());
    }

    public SynchronizeService getService() {
        return service;
    }

    @Override
    public Object handleError(WebApplicationException e) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(os));
        Template template = getView("error").arg("error", e).arg("stack", new String(os.toByteArray()));
        return Response.status(500).type(MediaType.TEXT_HTML).entity(template).build();
    }
}
