package eu.europa.ec.eci.oct.export.model;

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.03 at 01:56:07 PM CET 
//



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="forCountry" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="initiativeData" type="{}InitiativeDataType" minOccurs="0"/>
 *         &lt;element name="signatures" type="{}SignatureListType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "forCountry",
    "initiativeData",
    "signatures"
})
@XmlRootElement(name = "supportForm")
public class SupportForm {

    @XmlElement(required = true)
    protected String forCountry;
    protected InitiativeDataType initiativeData;
    @XmlElement(required = true)
    protected SignatureListType signatures;

    /**
     * Gets the value of the forCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForCountry() {
        return forCountry;
    }

    /**
     * Sets the value of the forCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForCountry(String value) {
        this.forCountry = value;
    }

    /**
     * Gets the value of the initiativeData property.
     * 
     * @return
     *     possible object is
     *     {@link InitiativeDataType }
     *     
     */
    public InitiativeDataType getInitiativeData() {
        return initiativeData;
    }

    /**
     * Sets the value of the initiativeData property.
     * 
     * @param value
     *     allowed object is
     *     {@link InitiativeDataType }
     *     
     */
    public void setInitiativeData(InitiativeDataType value) {
        this.initiativeData = value;
    }

    /**
     * Gets the value of the signatures property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureListType }
     *     
     */
    public SignatureListType getSignatures() {
        return signatures;
    }

    /**
     * Sets the value of the signatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureListType }
     *     
     */
    public void setSignatures(SignatureListType value) {
        this.signatures = value;
    }

}
