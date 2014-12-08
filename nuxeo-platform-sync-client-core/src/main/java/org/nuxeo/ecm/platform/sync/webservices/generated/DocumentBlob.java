package org.nuxeo.ecm.platform.sync.webservices.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for documentBlob complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentBlob">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="blob" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="encoding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extensions" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mimeType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mimetype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentBlob", propOrder = { "blob", "encoding", "extensions", "mimeType", "mimetype", "name", "url" })
public class DocumentBlob {

    protected byte[] blob;

    protected String encoding;

    @XmlElement(nillable = true)
    protected List<String> extensions;

    protected String mimeType;

    protected String mimetype;

    protected String name;

    protected String url;

    /**
     * Gets the value of the blob property.
     * 
     * @return possible object is byte[]
     */
    public byte[] getBlob() {
        return blob;
    }

    /**
     * Sets the value of the blob property.
     * 
     * @param value allowed object is byte[]
     */
    public void setBlob(byte[] value) {
        this.blob = ((byte[]) value);
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return possible object is {@link String }
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the extensions property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the extensions property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getExtensions().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     */
    public List<String> getExtensions() {
        if (extensions == null) {
            extensions = new ArrayList<String>();
        }
        return this.extensions;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return possible object is {@link String }
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the mimetype property.
     * 
     * @return possible object is {@link String }
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * Sets the value of the mimetype property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setMimetype(String value) {
        this.mimetype = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return possible object is {@link String }
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setUrl(String value) {
        this.url = value;
    }

}
