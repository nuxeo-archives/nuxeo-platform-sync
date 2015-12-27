/*
 * (C) Copyright 2006-2008 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: WriteSecurityPolicy.java 58962 2008-11-20 16:37:03Z atchertchian $
 */

package org.nuxeo.ecm.platform.sync.security;

import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.Access;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.BROWSE;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.READ;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.READ_CHILDREN;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.READ_LIFE_CYCLE;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.READ_PROPERTIES;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.READ_SECURITY;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.VERSION;
import org.nuxeo.ecm.core.model.Document;
import org.nuxeo.ecm.core.security.AbstractSecurityPolicy;
import org.nuxeo.ecm.platform.sync.api.SynchronizeService;
import org.nuxeo.runtime.api.Framework;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

/**
 * Read security policy
 *
 * @author <a href="mailto:cbaican@nuxeo.com">Catalin Baican</a>
 */
public class ReadSecurityPolicy extends AbstractSecurityPolicy {

    protected SynchronizeService synchronizeService;

    @Override
    public Access checkPermission(Document doc, ACP mergedAcp, Principal principal, String permission,
            String[] resolvedPermissions, String[] additionalPrincipals) throws SecurityException {
        Access access = Access.UNKNOWN;
        if (doc.getType().getName().equals("UserWorkspacesRoot")) {
            return Access.DENY;
        }

        if (shouldDisableSecurityPolicy(doc, permission)) {
            return access;
        }

        List<String> permissionList = Arrays.asList(READ, BROWSE, "ReadVersion", READ_PROPERTIES, READ_CHILDREN,
                READ_LIFE_CYCLE, READ_SECURITY, "ReviewParticipant", VERSION);
        if (permissionList.contains(permission)) {
            access = Access.GRANT;
        } else {
            access = Access.DENY;
        }

        return access;
    }

    protected boolean shouldDisableSecurityPolicy(Document doc, String permission) {
        SynchronizeService service = getSynchronizeService();
        String docPath = doc.getPath();
        return service.shouldDisableReadSP(docPath, permission);
    }

    protected SynchronizeService getSynchronizeService() {
        if (synchronizeService == null) {
            try {
                synchronizeService = Framework.getService(SynchronizeService.class);
            } catch (Exception e) {
                synchronizeService = null;
            }
        }
        return synchronizeService;
    }

}
