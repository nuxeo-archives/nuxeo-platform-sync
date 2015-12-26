/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Quentin Lamerand
 */
package org.nuxeo.ecm.platform.sync.client;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("synchronizeDetails")
public class SynchronizeDetailsDescriptor {

    @XNode("@username")
    private String username;

    @XNode("@password")
    private String password;

    @XNode("@protocol")
    private String protocol = "http";

    @XNode("@host")
    private String host;

    @XNode("@port")
    private int port = 8080;

    @XNode("@contextPath")
    private String contextPath;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getContextPath() {
        return contextPath;
    }

}
