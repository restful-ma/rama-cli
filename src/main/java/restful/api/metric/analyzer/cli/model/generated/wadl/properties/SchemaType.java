
package restful.api.metric.analyzer.cli.model.generated.wadl.properties;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für schemaType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="schemaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="element" type="{http://www.w3.org/2001/XMLSchema}elementType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="complexType" type="{http://www.w3.org/2001/XMLSchema}complexTypeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "schemaType", namespace = "http://www.w3.org/2001/XMLSchema", propOrder = {
    "element",
    "complexType"
})
public class SchemaType {

    @XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    protected List<ElementType> element;
    @XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    protected List<ComplexTypeType> complexType;

    /**
     * Gets the value of the element property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the element property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElementType }
     * 
     * 
     */
    public List<ElementType> getElement() {
        if (element == null) {
            element = new ArrayList<ElementType>();
        }
        return this.element;
    }

    /**
     * Gets the value of the complexType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the complexType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComplexType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ComplexTypeType }
     * 
     * 
     */
    public List<ComplexTypeType> getComplexType() {
        if (complexType == null) {
            complexType = new ArrayList<ComplexTypeType>();
        }
        return this.complexType;
    }

}
