/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and others.
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
