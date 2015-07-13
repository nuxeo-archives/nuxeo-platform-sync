/*
 * (C) Copyright 2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 */
package org.nuxeo.ecm.platform.sync.server.restAPI;

import java.io.ByteArrayOutputStream;

import org.nuxeo.ecm.platform.relations.api.Graph;
import org.nuxeo.ecm.platform.relations.api.RelationManager;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseStatelessNuxeoRestlet;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Simple restlet to export relation graphs.
 *
 * @author <a href="mailto:cbaican@nuxeo.com">Catalin Baican</a>
 */
public class RelationRestlet extends BaseStatelessNuxeoRestlet {

    public static final String XML_START = "<?";

    public static final String XML_HEADER = "<?xml version=\"1.0\"?>\n";

    @Override
    public void handle(Request req, Response res) {

        RelationManager relationManager;
        try {
            relationManager = Framework.getService(RelationManager.class);
            if (relationManager == null) {
                handleError(res, "Unable to get Relation Service");
                return;
            }
        } catch (Exception e) {
            handleError(res, e);
            return;
        }

        String graphName = (String) req.getAttributes().get("graphName");
        if ("".equals(graphName)) {
            handleError(res, "You must specify a graph name");
            return;
        }

        Graph graph = relationManager.getGraphByName(graphName);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        graph.write(out, null, null);
        String out_xml = out.toString();
        if (out_xml.startsWith(XML_START)) {
            res.setEntity(out_xml, MediaType.TEXT_XML);
        } else {
            StringBuilder sb = new StringBuilder(out.size() + 30);
            sb.append(XML_HEADER);
            sb.append(out_xml);

            res.setEntity(sb.toString(), MediaType.TEXT_XML);
        }
    }

}
