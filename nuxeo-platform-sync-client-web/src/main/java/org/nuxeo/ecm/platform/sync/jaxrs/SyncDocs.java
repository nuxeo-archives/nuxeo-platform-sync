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
 *     matic
 */
package org.nuxeo.ecm.platform.sync.jaxrs;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.sync.api.SynchronizeReport;
import org.nuxeo.ecm.platform.sync.jaxrs.SyncRoot.Params;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

/**
 * @author matic
 *
 */
@WebObject(type=SyncDocs.ID)
public class SyncDocs extends DefaultObject{

    public static final String ID = "SyncDocs";
    
    protected SyncRoot root;
    
    protected SynchronizeReport report;
    
    protected Exception error;
    
    @Override
    protected void initialize(Object... args) {
        super.initialize(args);
    }
    
    @POST
    public Object doSynch(@FormParam("queryName") String query) {
        CoreSession session = getContext().getCoreSession();
        try {
            report = root.service.doSynchronizeDocuments(session, Params.toDetails(root.params), query);
        } catch (Exception e) {
            error = e;
        }
        return getView("index");
    }
}
