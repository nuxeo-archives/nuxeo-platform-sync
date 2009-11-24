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

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.sync.api.SynchronizeService;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
@Produces("text/html;charset=UTF-8")
public class SynchronizationHandler extends DefaultObject {

    @POST
    @Path("synchronize")
    public String synchronize(@QueryParam("queryName") String queryName) throws Exception {
        WebContext ctx = WebEngine.getActiveContext();
        CoreSession session = ctx.getCoreSession();

        SynchronizeService synchronizeService = Framework.getService(SynchronizeService.class);
        if (queryName != null) {
            synchronizeService.doSynchronizeDocuments(session, queryName);
        } else {
            synchronizeService.doSynchronizeDocuments(session);
        }

        return "";
    }

}
