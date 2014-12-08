package org.nuxeo.ecm.platform.sync.webservices.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for flagedDocumentSnapshot complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="flagedDocumentSnapshot">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.server.sync.platform.ecm.nuxeo.org/}documentSnapshot">
 *       &lt;sequence>
 *         &lt;element name="hasBlobs" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "flagedDocumentSnapshot", propOrder = { "hasBlobs" })
public class FlagedDocumentSnapshot extends DocumentSnapshot {

    protected boolean hasBlobs;

    /**
     * Gets the value of the hasBlobs property.
     */
    public boolean isHasBlobs() {
        return hasBlobs;
    }

    /**
     * Sets the value of the hasBlobs property.
     */
    public void setHasBlobs(boolean value) {
        this.hasBlobs = value;
    }

}
