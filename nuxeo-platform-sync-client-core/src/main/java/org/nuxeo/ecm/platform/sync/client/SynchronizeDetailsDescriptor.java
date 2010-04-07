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

}
