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

package org.nuxeo.ecm.platform.sync.client;

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

    @XNode("@defaultImportLifeCycle")
    private String defaultImportLifeCycle;

    @XNode("@generateNewId")
    private boolean generateNewId;

    public Path getDocumentRootPath() {
        return new Path(documentRootPath);
    }

    public int getPathSegmentsToRemove() {
        return pathSegmentsToRemove;
    }

    public String getDefaultImportLifeCycle() {
        return defaultImportLifeCycle;
    }

    public boolean getGenerateNewId() {
        return generateNewId;
    }

}
