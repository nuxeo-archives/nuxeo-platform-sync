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

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.platform.sync.manager.DocumentDifferencesPolicy;

/**
 * @author <a href="mailto:qlamerand@nuxeo.com">Quentin Lamerand</a>
 */
@XObject("documentDifferencesPolicy")
public class DocumentDifferencesPolicyDescriptor {

    @XNode("@policyClass")
    private Class<DocumentDifferencesPolicy> policyClass;

    @XNode("@name")
    private String name = "default";
    
    public Class<DocumentDifferencesPolicy> getPolicyClass() {
        return policyClass;
    }

    public String getName() {
        return name;
    }

}
