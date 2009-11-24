
package org.nuxeo.ecm.platform.sync.webservices.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentSnapshot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentSnapshot">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="acl" type="{http://webservices.server.sync.platform.ecm.nuxeo.org/}wsACE" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="blobProperties" type="{http://webservices.server.sync.platform.ecm.nuxeo.org/}documentBlob" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="noBlobProperties" type="{http://webservices.server.sync.platform.ecm.nuxeo.org/}documentProperty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="pathAsString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentSnapshot", propOrder = {
    "acl",
    "blobProperties",
    "noBlobProperties",
    "pathAsString"
})
@XmlSeeAlso({
    FlagedDocumentSnapshot.class
})
public class DocumentSnapshot {

    @XmlElement(nillable = true)
    protected List<WsACE> acl;
    @XmlElement(nillable = true)
    protected List<DocumentBlob> blobProperties;
    @XmlElement(nillable = true)
    protected List<DocumentProperty> noBlobProperties;
    protected String pathAsString;

    /**
     * Gets the value of the acl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the acl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAcl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WsACE }
     * 
     * 
     */
    public List<WsACE> getAcl() {
        if (acl == null) {
            acl = new ArrayList<WsACE>();
        }
        return this.acl;
    }

    /**
     * Gets the value of the blobProperties property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the blobProperties property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBlobProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentBlob }
     * 
     * 
     */
    public List<DocumentBlob> getBlobProperties() {
        if (blobProperties == null) {
            blobProperties = new ArrayList<DocumentBlob>();
        }
        return this.blobProperties;
    }

    /**
     * Gets the value of the noBlobProperties property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the noBlobProperties property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNoBlobProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentProperty }
     * 
     * 
     */
    public List<DocumentProperty> getNoBlobProperties() {
        if (noBlobProperties == null) {
            noBlobProperties = new ArrayList<DocumentProperty>();
        }
        return this.noBlobProperties;
    }

    /**
     * Gets the value of the pathAsString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPathAsString() {
        return pathAsString;
    }

    /**
     * Sets the value of the pathAsString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPathAsString(String value) {
        this.pathAsString = value;
    }

}
