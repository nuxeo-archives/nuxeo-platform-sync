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

package org.nuxeo.ecm.platform.sync.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.utils.Path;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
@XObject("importConfiguration")
public class ImportConfiguration {

    @XNode("@documentRootPath")
    private String documentRootPath;

    @XNode("@pathSegmentsToRemove")
    private int pathSegmentsToRemove;

    @XNode("@generateNewId")
    private boolean generateNewId;

    @XNodeMap(value = "lifeCycleStateMapping", key = "@serverState", type = HashMap.class, componentType = String.class)
    Map<String, String> lifeCycleStateMapping = new HashMap<String, String>();

    @XNodeList(value = "exclude/field", type = HashSet.class, componentType = String.class)
    private Set<String> excludedFields = new HashSet<String>();

    public Path getDocumentRootPath() {
        if (documentRootPath == null) {
            return null;
        }
        return new Path(documentRootPath);
    }

    public int getPathSegmentsToRemove() {
        return pathSegmentsToRemove;
    }

    public String getClientLifeCycleStateFor(String serverLifeCycleState) {
        return lifeCycleStateMapping.get(serverLifeCycleState);
    }

    public boolean getGenerateNewId() {
        return generateNewId;
    }

    public Set<String> getExcludedFields() {
        return excludedFields;
    }

}
