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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for wsACE complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="wsACE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="granted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="permission" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsACE", propOrder = { "granted", "permission", "username" })
public class WsACE {

    protected boolean granted;

    protected String permission;

    protected String username;

    /**
     * Gets the value of the granted property.
     */
    public boolean isGranted() {
        return granted;
    }

    /**
     * Sets the value of the granted property.
     */
    public void setGranted(boolean value) {
        this.granted = value;
    }

    /**
     * Gets the value of the permission property.
     *
     * @return possible object is {@link String }
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the value of the permission property.
     *
     * @param value allowed object is {@link String }
     */
    public void setPermission(String value) {
        this.permission = value;
    }

    /**
     * Gets the value of the username property.
     *
     * @return possible object is {@link String }
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     *
     * @param value allowed object is {@link String }
     */
    public void setUsername(String value) {
        this.username = value;
    }

}
