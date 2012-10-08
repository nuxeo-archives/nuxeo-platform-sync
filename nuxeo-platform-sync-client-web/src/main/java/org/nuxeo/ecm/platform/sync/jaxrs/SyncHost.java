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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

/**
 * @author "Stephane Lacoin (aka matic) slacoin@nuxeo.com"
 *
 */
@WebObject(type="Host")
public class SyncHost extends DefaultObject {
    
    protected UriInfo info;
    
    protected URI location;
    
    public URI getLocation() {
        return location;
    }

    public URI segment(String segment) {
        return info.getRequestUriBuilder().segment(segment).build();
    }
    
    protected SynchronizeDetails details(){
        SynchronizeDetails details = new SynchronizeDetails();
        details.setProtocol(location.getScheme());
        details.setHost(location.getHost());
        int port = location.getPort();
        details.setPort(port > 0 ? port : 80);
        details.setContextPath(location.getPath());
        String[] credentials = location.getUserInfo().split(":");
        if (credentials.length > 0) {
            details.setUsername(credentials[0]);
        }
        if (credentials.length > 1) {
            details.setPassword(credentials[1]);
        }
        return details;
    }
    
    protected String host;
    
    public String getHost() {
        return host;
    }
    
    @Override
    protected void initialize(Object... args) {
        super.initialize(args);
        this.host = (String)args[0];
    }
    
    protected SyncRoot syncRoot() {
        return (SyncRoot)getPrevious();
    }
    
    @GET
    public Object doGet(@MatrixParam("location") URI location, @Context UriInfo info) {
        this.info = info;
        this.location = location;
        return getView("index");
    }
    
    
    @POST
    public Object doPost(@FormParam("location") URI location, @Context UriInfo info) throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
        this.location = location;
        this.info = info;
        URI newHost = info.getBaseUriBuilder().path("/sync").path("/"+location.getHost()).matrixParam("location", location).build();
        return redirect(newHost.toASCIIString());
    }
    
    @Path("documents")
    public Object doNavigateDocs(@Context UriInfo info) throws MalformedURLException {
        injectInfo(info);      
        return newObject("Docs");
    }


    @Path("vocabularies")
    public Object doNavigateVocs(@Context UriInfo info) throws MalformedURLException {
        injectInfo(info);      
        return newObject("Vocs");
    }
    
    @Path("relations")
    public Object doNavigateRels(@Context UriInfo info) throws MalformedURLException {
        injectInfo(info);      
        return newObject("Rels");
    }

    protected void injectInfo(UriInfo info) {
        List<PathSegment> segments = info.getPathSegments();
        PathSegment hostSegment = segments.get(segments.size()-2);
        this.info = info;
        this.location = URI.create(hostSegment.getMatrixParameters().get("location").get(0));
    }

}
