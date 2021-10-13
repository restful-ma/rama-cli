
package restful.api.metric.analyzer.cli.model.generated.wadl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "doc",
    "grammars",
    "resources",
    "resourceTypeOrMethodOrRepresentation",
    "any"
})
@XmlRootElement(name = "application", namespace = "http://wadl.dev.java.net/2009/02")
public class Application {

    @XmlElement(namespace = "http://wadl.dev.java.net/2009/02")
    protected List<Doc> doc;
    @XmlElement(namespace = "http://wadl.dev.java.net/2009/02")
    protected Grammars grammars;
    @XmlElement(namespace = "http://wadl.dev.java.net/2009/02")
    protected List<Resources> resources;
    @XmlElements({
        @XmlElement(name = "resource_type", namespace = "http://wadl.dev.java.net/2009/02", type = ResourceType.class),
        @XmlElement(name = "method", namespace = "http://wadl.dev.java.net/2009/02", type = Method.class),
        @XmlElement(name = "representation", namespace = "http://wadl.dev.java.net/2009/02", type = Representation.class),
        @XmlElement(name = "param", namespace = "http://wadl.dev.java.net/2009/02", type = Param.class)
    })
    protected List<Object> resourceTypeOrMethodOrRepresentation;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the doc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the doc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDoc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Doc }
     * 
     * 
     */
    public List<Doc> getDoc() {
        if (doc == null) {
            doc = new ArrayList<Doc>();
        }
        return this.doc;
    }

    /**
     * Ruft den Wert der grammars-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Grammars }
     *     
     */
    public Grammars getGrammars() {
        return grammars;
    }

    /**
     * Legt den Wert der grammars-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Grammars }
     *     
     */
    public void setGrammars(Grammars value) {
        this.grammars = value;
    }

    /**
     * Gets the value of the resources property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resources property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResources().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Resources }
     * 
     * 
     */
    public List<Resources> getResources() {
        if (resources == null) {
            resources = new ArrayList<Resources>();
        }
        return this.resources;
    }

    /**
     * Gets the value of the resourceTypeOrMethodOrRepresentation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceTypeOrMethodOrRepresentation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceTypeOrMethodOrRepresentation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceType }
     * {@link Method }
     * {@link Representation }
     * {@link Param }
     * 
     * 
     */
    public List<Object> getResourceTypeOrMethodOrRepresentation() {
        if (resourceTypeOrMethodOrRepresentation == null) {
            resourceTypeOrMethodOrRepresentation = new ArrayList<Object>();
        }
        return this.resourceTypeOrMethodOrRepresentation;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}
