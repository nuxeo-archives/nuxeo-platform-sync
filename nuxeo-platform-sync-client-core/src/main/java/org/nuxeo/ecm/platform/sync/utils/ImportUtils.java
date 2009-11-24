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
package org.nuxeo.ecm.platform.sync.utils;

import java.util.List;

import org.nuxeo.ecm.platform.sync.webservices.generated.ContextDataInfo;

/**
 * Utility class used to provide functionality in the process of import.
 *
 * @author rux
 *
 */
public class ImportUtils {

    public static final String DELETED = "DELETED";

    public static final String MODIFIED = "MODIFIED";

    public static final String ADDED = "ADDED";

    public static final String RESTORED = "RESTORED";

    public static final String DELETED_LIFECYCLE_STATE = "deleted";

    /**
     * Utility method used to return a context data information.Usually this
     * context data information is used in the process of import.
     *
     * @param contextDatas - the list with all the context data information that
     *            a document has
     * @param dataName - the name of the data context information that needs to
     *            be retrieved
     * @return the value of the context data information that needs to be
     *         retrieved
     */
    public static String getContextDataInfo(List<ContextDataInfo> contextDatas,
            String dataName) {
        String dataValue = null;
        for (ContextDataInfo contextDataInfo : contextDatas) {
            if (contextDataInfo.getDataName().equals(dataName)) {
                dataValue = contextDataInfo.getDataValue();
                break;
            }
        }
        return dataValue;
    }

    /**
     * Returns the parent path from the path of a document.
     *
     * @param path - the path of a document
     * @return the parent path
     */
    public static String getParentPath(String path) {
        int index = path.lastIndexOf('/');
        String parent = path.substring(0, index);
        return parent.equals("") ? "/" : parent;
    }

    /**
     * Returns the name of a document given its path.
     *
     * @param path - the path of a document
     * @return the name of the document
     */
    public static String getName(String path) {
        int index = path.lastIndexOf('/');
        return path.substring(index + 1, path.length());
    }

}
