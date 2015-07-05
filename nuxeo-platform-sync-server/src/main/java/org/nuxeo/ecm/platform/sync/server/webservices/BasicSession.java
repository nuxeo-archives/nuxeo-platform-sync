/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 * $Id$
 */

package org.nuxeo.ecm.platform.sync.server.webservices;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

/**
 * @author rux rdarlea@nuxeo.com
 */
public class BasicSession {

    private CoreSession coreSession;

    // can be null if the JAAS context is suposedly established by the Nuxeo filters
    private LoginContext loginCtx;

    public CoreSession getCoreSession() {
        return coreSession;
    }

    public BasicSession(CoreSession docMgr, LoginContext loginCtx) {
        this.coreSession = docMgr;
        this.loginCtx = loginCtx;
    }

    public void login() {
        try {
            loginCtx.login();
        } catch (LoginException e) {
            throw new ClientException(e);
        }

    }

    public void logout() {
        try {
            loginCtx.logout();
        } catch (LoginException e) {
            throw new ClientRuntimeException(e);
        }
    }

    public void disconnect() {
        coreSession.close();
        logout();
    }

    public static BasicSession getInstanceAsUser(String repositoryName, String userName, String password)
            {
        LoginContext loginContext = null;
        CoreSession session = null;
        try {
            loginContext = Framework.login(userName, password);
        } catch (LoginException e) {
            throw new ClientException(e);
        }
        try {
            session = CoreInstance.openCoreSession(repositoryName);
        } catch (ClientException e) {
            try {
                loginContext.logout();
            } catch (LoginException le) {
                // not interested
            }
            throw new ClientException(e);
        }
        return new BasicSession(session, loginContext);
    }
}
