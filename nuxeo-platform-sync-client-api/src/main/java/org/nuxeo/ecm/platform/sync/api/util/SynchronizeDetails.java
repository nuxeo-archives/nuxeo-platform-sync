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
package org.nuxeo.ecm.platform.sync.api.util;

import java.io.Serializable;

import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;

/**
 * Utility class that will keep all the details needed in the process of synchronization
 *
 * @author rux
 */
public class SynchronizeDetails implements Serializable {

    public static final SynchronizeDetails DEFAULTS = new SynchronizeDetails("Administrator", "Administrator", "http",
            "localhost", 8080, "/nuxeo");

    private static final long serialVersionUID = -3876136428566855181L;

    private String username;

    private String password;

    private String host;

    private int port = 8080;

    private String protocol = "http";

    private String contextPath;

    private String diffPolicy = "default";

    private Boolean dryRun = false;

    public SynchronizeDetails() {
    }

    public SynchronizeDetails(String username, String password, String protocol, String host, int port,
            String contextPath) {
        this.username = username;
        this.password = password;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.contextPath = contextPath;
    }

    public String getUrl() {
        StringBuilder url = new StringBuilder(getProtocol());
        url.append("://");
        url.append(getHost());
        url.append(":");
        url.append(getPort());
        url.append(getContextPath());
        return url.toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // XXX : is this used ?
    public void reset() {
        host = null;
        port = 8080;
        username = null;
        password = null;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getContextPath() {
        if (contextPath == null || "".equals(contextPath)) {
            contextPath = VirtualHostHelper.getContextPathProperty();
        }
        if (!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getDiffPolicy() {
        return diffPolicy;
    }

    public void setDiffPolicy(String diffPolicy) {
        this.diffPolicy = diffPolicy;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

}
