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
package org.nuxeo.ecm.platform.sync.manager;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.relations.api.Graph;
import org.nuxeo.ecm.platform.relations.api.RelationManager;
import org.nuxeo.ecm.platform.sync.api.SynchronizeReport;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.platform.sync.utils.SynchHttpClient;
import org.nuxeo.runtime.api.Framework;

/**
 * The manager to take care the relations set synchronization. It simple replaces the local relations with the ones from
 * server.
 * 
 * @author <a href="mailto:cbaican@nuxeo.com">Catalin Baican</a>
 */
public class RelationsSynchronizeManager {
    SynchHttpClient httpClient = null;

    public RelationsSynchronizeManager(SynchronizeDetails synchronizeDetails) {
        httpClient = new SynchHttpClient(synchronizeDetails);

    }

    public SynchronizeReport performChanges() throws ClientException {
        RelationManager relationManager;
        try {
            relationManager = Framework.getService(RelationManager.class);
            if (relationManager == null) {
                throw new ClientException("Cannot get RelationManager");
            }
        } catch (Exception e) {
            throw new ClientException("Cannot get RelationManager", e);
        }

        Graph graph;
        final List<String> graphNames = relationManager.getGraphNames();
        for (String currentGraphName : graphNames) {
            try {
                List<String> pathParams = Arrays.asList("relation", currentGraphName);
                InputStream inputStream = httpClient.executeGetCall(pathParams, null);
                if (inputStream == null) {
                    throw new IllegalStateException("Cannot fetch graphs input from server");
                }
                try {
                    graph = relationManager.getGraphByName(currentGraphName);
                } catch (ClientException e) {
                    throw new ClientException("Unable to get graph:" + currentGraphName);
                }

                graph.clear();

                try {
                    String inputString = IOUtils.toString(inputStream, "ISO-8859-1");
                    InputStream inStream = new StringInputStream(new String(inputString.getBytes(), "UTF-8"));
                    graph.read(inStream, null, null);
                } catch (Exception e) {
                    throw new ClientException("Can't parse stream in UTF-8", e);
                }
            } finally {
                // close connection
                httpClient.closeConnection();
            }
        }

        return SynchronizeReport.newRelationsReport(graphNames);
    }

}
