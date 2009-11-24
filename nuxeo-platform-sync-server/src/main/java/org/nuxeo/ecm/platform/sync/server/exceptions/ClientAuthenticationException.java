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

package org.nuxeo.ecm.platform.sync.server.exceptions;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * @author rux rdarlea@nuxeo.com
 */
public class ClientAuthenticationException extends ClientException{

    private static final long serialVersionUID = -3443936569208582480L;

    public ClientAuthenticationException() {
    }

    public ClientAuthenticationException(String message) {
        super(message);
    }

    public ClientAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientAuthenticationException(Throwable cause) {
        super(cause);
    }

}
