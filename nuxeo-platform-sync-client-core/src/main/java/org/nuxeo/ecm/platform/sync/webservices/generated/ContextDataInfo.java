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
 * Java class for contextDataInfo complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="contextDataInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contextDataInfo", propOrder = { "dataName", "dataValue" })
public class ContextDataInfo {

    protected String dataName;

    protected String dataValue;

    /**
     * Gets the value of the dataName property.
     *
     * @return possible object is {@link String }
     */
    public String getDataName() {
        return dataName;
    }

    /**
     * Sets the value of the dataName property.
     *
     * @param value allowed object is {@link String }
     */
    public void setDataName(String value) {
        this.dataName = value;
    }

    /**
     * Gets the value of the dataValue property.
     *
     * @return possible object is {@link String }
     */
    public String getDataValue() {
        return dataValue;
    }

    /**
     * Sets the value of the dataValue property.
     *
     * @param value allowed object is {@link String }
     */
    public void setDataValue(String value) {
        this.dataValue = value;
    }

}
