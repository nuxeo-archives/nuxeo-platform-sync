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
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;

/**
 * @author rux rdarlea@nuxeo.com
 */
public class BasicSession {

    private CoreSession documentManager;
    //can be null if the JAAS context is suposedly established by the Nuxeo filters
    private LoginContext loginCtx;
    private Repository repository;

    public CoreSession getDocumentManager() {
        return documentManager;
    }

    public LoginContext getLoginCtx() {
        return loginCtx;
    }

    public BasicSession(CoreSession docMgr, LoginContext loginCtx,
            Repository repo) {
        this.documentManager = docMgr;
        this.loginCtx = loginCtx;
        this.repository = repo;
    }

    public Repository getRepository() {
        return repository;
    }

    public void login() throws ClientException {
        try {
            if (loginCtx != null) {
                loginCtx.login();
            } else {
                //TODO I should check if JAAS context established... How?
            }
        } catch (LoginException e) {
            throw new ClientException(e);
        }

    }

    public void logout() {
        try {
            if (loginCtx != null) {
                loginCtx.logout();
            }
        } catch (LoginException e) {
//            throw new ClientAuthenticationException(e);
        }
    }

    public void disconnect() {
        try {
            Repository.close(documentManager);
            logout();
        } catch (Exception e) {
//          throw new ClientAuthenticationException(e);
        }

    }
    
    public static BasicSession getInstanceAsSuperUser(String repository) 
            throws ClientException {
        LoginContext loginContext = null;
        CoreSession session = null;
        RepositoryManager manager = null;
        Repository repo = null;
        try {
            loginContext = Framework.login();
            manager = Framework.getService(RepositoryManager.class);
            repo = manager.getRepository(repository);
            session = repo.open();
        } catch (Exception e) {
            if (session != null) {
                Repository.close(session);
            }
            try {
                if (loginContext != null) {
                    loginContext.logout();
                }
            } catch (LoginException le) {
                //not interested
            }
            throw new ClientException(e);
        }
        return new BasicSession(session, loginContext, repo);
    }

    public static BasicSession getInstanceAsUser(String repository, 
            String userName, String password) throws ClientException {
        LoginContext loginContext = null;
        CoreSession session = null;
        RepositoryManager manager = null;
        Repository repo = null;
        try {
            loginContext = Framework.login(userName, password);
            manager = Framework.getService(RepositoryManager.class);
            repo = manager.getRepository(repository);
            session = repo.open();
        } catch (Exception e) {
            if (session != null) {
                Repository.close(session);
            }
            try {
                if (loginContext != null) {
                    loginContext.logout();
                }
            } catch (LoginException le) {
                //not interested
            }
            throw new ClientException(e);
        }
        return new BasicSession(session, loginContext, repo);
    }
}
