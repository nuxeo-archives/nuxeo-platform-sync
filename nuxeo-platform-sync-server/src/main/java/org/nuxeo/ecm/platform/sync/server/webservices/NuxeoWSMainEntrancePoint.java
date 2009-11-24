/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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

package org.nuxeo.ecm.platform.sync.server.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.nuxeo.ecm.platform.sync.server.exceptions.ClientAuthenticationException;

/**
 * Provides a clean way to obtain a WS-Addressing flavor web service. 
 * A client is wanting to use a certain Web Service. First method to use is a
 * member of this class, one of access[ServiceName]. Through it the client get
 * control over a reference to the wanted web service. If the credentials (or
 * anything else) fails, an exception is thrown and the service remains
 * unavailable. If the EPR is returned, the service is available as usual.
 * Moreover, a Stateful Web Service can be accessed only properly decorated,
 * so the client has to use the instance returned. Just for the record, if the
 * client tries to get directly the Stateful Web Service, an exception regarding
 * SOAP envelope is thrown.
 * After a specified amount of inactivity (1000 seconds currently) the instance is
 * destroyed. In order to prevent that, the Client has to call periodically the 
 * service.
 *
 * @author rux rdarlea@nuxeo.com.
 */

@WebService
public class NuxeoWSMainEntrancePoint {

    @WebMethod(operationName="accessWSSynchroServerModule")
    public synchronized W3CEndpointReference accessWSSynchroServerModule(
            @WebParam(name="repository") String repo,
            @WebParam(name="user") String userName,
            @WebParam(name="password") String password)
            throws ClientAuthenticationException {

        BasicSession session = null;
        try {
            session = BasicSession.getInstanceAsUser(repo, userName, password);
        } catch (Exception e) {
            throw new ClientAuthenticationException(e);
        }
        session.logout();
        WSSynchroServerModule wssyncro = new WSSynchroServerModule(session);
        //allows the timeout to be armed
        WSSynchroServerModule.manager.setTimeout(1000000,
                new StatefulWebServiceCallbackImpl<WSSynchroServerModule>());
        return WSSynchroServerModule.manager.export(wssyncro);
    }

}
