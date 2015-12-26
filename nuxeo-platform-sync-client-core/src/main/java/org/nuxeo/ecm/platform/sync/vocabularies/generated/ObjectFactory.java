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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2009.12.03 at 07:10:29 PM CET
//

package org.nuxeo.ecm.platform.sync.vocabularies.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * org.nuxeo.ecm.platform.sync.vocabularies.generated package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Entries_QNAME = new QName("", "entries");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
     * org.nuxeo.ecm.platform.sync.vocabularies.generated
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Entry }
     */
    public Entry createEntry() {
        return new Entry();
    }

    /**
     * Create an instance of {@link Entries }
     */
    public Entries createEntries() {
        return new Entries();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Entries }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "entries")
    public JAXBElement<Entries> createEntries(Entries value) {
        return new JAXBElement<Entries>(_Entries_QNAME, Entries.class, null, value);
    }

}
