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
 * $Id$
 */

package org.nuxeo.ecm.platform.sync.server.webservices;


import com.sun.xml.ws.developer.StatefulWebServiceManager;
import com.sun.xml.ws.developer.StatefulWebServiceManager.Callback;

/**
 * The callback for timeout on Stateful Web Service. It is armed by access point and it
 * requires {@link StatefulWebServiceManagement} object.
 *
 * @author rux rdarlea@nuxeo.com
 */
public class StatefulWebServiceCallbackImpl<T> implements Callback<T> {

    public void onTimeout(T arg0, StatefulWebServiceManager<T> arg1) {
        if (arg0 instanceof StatefulWebServiceManagement) {
            ((StatefulWebServiceManagement) arg0).destroySession();
        } else {
            arg1.touch(arg0);
        }
    }

}
