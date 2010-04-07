package org.nuxeo.ecm.platform.sync.api.util;

import java.io.Serializable;

/**
 * Utility class that will keep all the details needed in the process of
 * synchronization
 *
 * @author rux
 *
 */
public class SynchronizeDetails implements Serializable {

    private static final long serialVersionUID = -3876136428566855181L;

    private String username;

    private String password;

    private String host;

    private int port = 8080;

    private String protocol = "http";

    public SynchronizeDetails() {}

    public SynchronizeDetails(String username, String password, String protocol, String host,
            int port) {
        this.username = username;
        this.password = password;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
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

}
