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
package org.nuxeo.ecm.platform.sync.webservices.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for nuxeoSynchroTuple complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="nuxeoSynchroTuple">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="adaptedId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="clientId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contextData" type="{http://webservices.server.sync.platform.ecm.nuxeo.org/}contextDataInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lastModification" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proxy" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="serverId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nuxeoSynchroTuple", propOrder = { "adaptedId", "clientId", "contextData", "lastModification", "path",
        "proxy", "serverId", "type", "version" })
public class NuxeoSynchroTuple {

    protected String adaptedId;

    protected String clientId;

    @XmlElement(nillable = true)
    protected List<ContextDataInfo> contextData;

    protected Long lastModification;

    protected String path;

    protected boolean proxy;

    protected String serverId;

    protected String type;

    protected boolean version;

    /**
     * Gets the value of the adaptedId property.
     *
     * @return possible object is {@link String }
     */
    public String getAdaptedId() {
        return adaptedId;
    }

    /**
     * Sets the value of the adaptedId property.
     *
     * @param value allowed object is {@link String }
     */
    public void setAdaptedId(String value) {
        this.adaptedId = value;
    }

    /**
     * Gets the value of the clientId property.
     *
     * @return possible object is {@link String }
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the value of the clientId property.
     *
     * @param value allowed object is {@link String }
     */
    public void setClientId(String value) {
        this.clientId = value;
    }

    /**
     * Gets the value of the contextData property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the contextData property.
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getContextData().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link ContextDataInfo }
     */
    public List<ContextDataInfo> getContextData() {
        if (contextData == null) {
            contextData = new ArrayList<ContextDataInfo>();
        }
        return this.contextData;
    }

    /**
     * Gets the value of the lastModification property.
     *
     * @return possible object is {@link Long }
     */
    public Long getLastModification() {
        return lastModification;
    }

    /**
     * Sets the value of the lastModification property.
     *
     * @param value allowed object is {@link Long }
     */
    public void setLastModification(Long value) {
        this.lastModification = value;
    }

    /**
     * Gets the value of the path property.
     *
     * @return possible object is {@link String }
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     *
     * @param value allowed object is {@link String }
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the proxy property.
     */
    public boolean isProxy() {
        return proxy;
    }

    /**
     * Sets the value of the proxy property.
     */
    public void setProxy(boolean value) {
        this.proxy = value;
    }

    /**
     * Gets the value of the serverId property.
     *
     * @return possible object is {@link String }
     */
    public String getServerId() {
        return serverId;
    }

    /**
     * Sets the value of the serverId property.
     *
     * @param value allowed object is {@link String }
     */
    public void setServerId(String value) {
        this.serverId = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return possible object is {@link String }
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value allowed object is {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the version property.
     */
    public boolean isVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     */
    public void setVersion(boolean value) {
        this.version = value;
    }

}
