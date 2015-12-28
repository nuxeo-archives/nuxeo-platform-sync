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
 *     Nuxeo - initial API and implementation
 *
 */
package org.nuxeo.ecm.platform.sync.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.nuxeo.ecm.core.api.NuxeoException;
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

    public SynchronizeReport performChanges() {
        RelationManager relationManager = Framework.getService(RelationManager.class);
        if (relationManager == null) {
            throw new NuxeoException("Cannot get RelationManager");
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
                graph = relationManager.getGraphByName(currentGraphName);

                graph.clear();

                try {
                    String inputString = IOUtils.toString(inputStream, "ISO-8859-1");
                    InputStream inStream = new StringInputStream(new String(inputString.getBytes(), "UTF-8"));
                    graph.read(inStream, null, null);
                } catch (IOException e) {
                    throw new NuxeoException("Can't parse stream in UTF-8", e);
                }
            } finally {
                // close connection
                httpClient.closeConnection();
            }
        }

        return SynchronizeReport.newRelationsReport(graphNames);
    }

}
