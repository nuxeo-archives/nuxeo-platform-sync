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
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
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
    
    protected boolean dryrun = true;
    
    protected UriInfo info;
    
    protected URI location;
    
    protected String name;
    
    public boolean getDryrun() {
        return dryrun;
    }
    
    public UriInfo getInfo() {
        return info;
    }

    public URI getLocation() {
        return location;
    }
    
    public String getName() {
        return name;
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
        details.setDryRun(dryrun);
        return details;
    }
    

    
    @Override
    protected void initialize(Object... args) {
        super.initialize(args);
        if (args.length == 1) {
            this.name = (String)args[0];
        }
    }
    
    protected SyncRoot syncRoot() {
        return (SyncRoot)getPrevious();
    }
    
    @GET
    public Object doGet(@MatrixParam("location") URI location, @MatrixParam("dryrun") @DefaultValue("true") boolean dryrun, @Context UriInfo info) {
        this.info = info;
        this.dryrun = dryrun;
        this.location = location;
        return getView("index");
    }
    
    
    @POST
    public Object doPost(@FormParam("location") URI location, @Context UriInfo info) throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
        this.info = info;
        return redirect(uri(location).toASCIIString());
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
        final MultivaluedMap<String, String> matrixParameters = parameters(info);
        this.info = info;
        this.location = URI.create(matrixParameters.get("location").get(0));
        final List<String> dryrunParams = matrixParameters.get("dryrun");
        if (dryrunParams != null) {
            this.dryrun = Boolean.parseBoolean(dryrunParams.get(0));
        }
    }

    protected MultivaluedMap<String, String> parameters(UriInfo info) {
        final List<PathSegment> segments = info.getPathSegments();
        final PathSegment hostSegment = segments.get(1);
        final MultivaluedMap<String, String> matrixParameters = hostSegment.getMatrixParameters();
        return matrixParameters;
    }

    public URI uri() {
        return uriBuilder(dryrun, location).build();
    }
    
    public URI uri(boolean dryrun) {
        return uriBuilder(dryrun, location).build();
    }
    
    public URI uri(URI location) {
        return uriBuilder(dryrun, location).build();
    }
        
    public UriBuilder uriBuilder(boolean dryrun, URI location) {
        final LinkedList<PathSegment> pathSegments = new LinkedList<PathSegment>(info.getPathSegments());
        UriBuilder builder = info.getBaseUriBuilder();
        builder = builder.path(pathSegments.remove().getPath()); // root
        pathSegments.remove(); // host
        builder = builder.path(location.getHost()).matrixParam("location", location).matrixParam("dryrun", dryrun);
        while(!pathSegments.isEmpty()) { // subs
            final PathSegment lastSegment = pathSegments.remove();
            builder = builder.path(lastSegment.getPath());
        }
        return builder;
    }


}
