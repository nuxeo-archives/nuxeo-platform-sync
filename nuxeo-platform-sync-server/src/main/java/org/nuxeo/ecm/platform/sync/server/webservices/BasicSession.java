/*
 * (C) Copyright 2006-2007 Nuxeo SA (http://nuxeo.com/) and others.
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
 * $Id$
 */

package org.nuxeo.ecm.platform.sync.server.webservices;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
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
            throw new NuxeoException(e);
        }

    }

    public void logout() {
        try {
            loginCtx.logout();
        } catch (LoginException e) {
            throw new NuxeoException(e);
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
            throw new NuxeoException(e);
        }
        try {
            session = CoreInstance.openCoreSession(repositoryName);
        } catch (NuxeoException e) {
            try {
                loginContext.logout();
            } catch (LoginException le) {
                // not interested
            }
            throw e;
        }
        return new BasicSession(session, loginContext);
    }
}
