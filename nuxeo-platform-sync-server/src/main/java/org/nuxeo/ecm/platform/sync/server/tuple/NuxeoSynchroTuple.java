/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.ecm.platform.sync.server.tuple;

import java.io.Serializable;

/**
 * It contains all relevant data to decide if a synchronization on a document is needed.
 *
 * @author mcedica
 */
public class NuxeoSynchroTuple implements Serializable {

    private static final long serialVersionUID = 1L;

    // server document unique ID
    private String serverId;

    // client document unique ID
    private String clientId;

    // server document applicative ID
    private String adaptedId;

    // document type
    private String type;

    // document path
    private String path;

    // document last modification date
    private Long lastModification;

    // is proxy
    private boolean isProxy;

    // is version
    private boolean isVersion;

    // used to pass properties to importDocument
    private ContextDataInfo[] contextData;

    public NuxeoSynchroTuple() {

    }

    public NuxeoSynchroTuple(String serverId, String clientId, String adaptedId, String type, String path,
            Long lastModification, boolean isProxy, boolean isVersion) {
        this.serverId = serverId;
        this.clientId = clientId;
        this.adaptedId = adaptedId;
        this.type = type;
        this.path = path;
        this.lastModification = lastModification;
        this.isProxy = isProxy;
        this.isVersion = isVersion;
    }

    public Long getLastModification() {
        return lastModification;
    }

    public String getPath() {
        return path;
    }

    public boolean isProxy() {
        return isProxy;
    }

    public boolean isVersion() {
        return isVersion;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLastModification(Long lastModification) {
        this.lastModification = lastModification;
    }

    public void setProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    public void setVersion(boolean isVersion) {
        this.isVersion = isVersion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ContextDataInfo[] getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataInfo[] contextData) {
        this.contextData = contextData;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAdaptedId() {
        return adaptedId;
    }

    public void setAdaptedId(String adaptedId) {
        this.adaptedId = adaptedId;
    }

}
