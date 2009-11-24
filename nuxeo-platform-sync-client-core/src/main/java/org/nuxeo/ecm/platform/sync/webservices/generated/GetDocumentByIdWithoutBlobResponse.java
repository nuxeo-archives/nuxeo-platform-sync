
package org.nuxeo.ecm.platform.sync.webservices.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDocumentByIdWithoutBlobResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDocumentByIdWithoutBlobResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://webservices.server.sync.platform.ecm.nuxeo.org/}flagedDocumentSnapshot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDocumentByIdWithoutBlobResponse", propOrder = {
    "_return"
})
public class GetDocumentByIdWithoutBlobResponse {

    @XmlElement(name = "return")
    protected FlagedDocumentSnapshot _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link FlagedDocumentSnapshot }
     *     
     */
    public FlagedDocumentSnapshot getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlagedDocumentSnapshot }
     *     
     */
    public void setReturn(FlagedDocumentSnapshot value) {
        this._return = value;
    }

}
