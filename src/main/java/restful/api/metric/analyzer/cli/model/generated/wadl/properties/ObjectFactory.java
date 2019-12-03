
package restful.api.metric.analyzer.cli.model.generated.wadl.properties;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the restful.api.metric.analyzer.cli.model.generated.wadl.properties package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Schema_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "schema");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: restful.api.metric.analyzer.cli.model.generated.wadl.properties
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SchemaType }
     * 
     */
    public SchemaType createSchemaType() {
        return new SchemaType();
    }

    /**
     * Create an instance of {@link SequenceType }
     * 
     */
    public SequenceType createSequenceType() {
        return new SequenceType();
    }

    /**
     * Create an instance of {@link ElementType }
     * 
     */
    public ElementType createElementType() {
        return new ElementType();
    }

    /**
     * Create an instance of {@link ComplexTypeType }
     * 
     */
    public ComplexTypeType createComplexTypeType() {
        return new ComplexTypeType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SchemaType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "schema")
    public JAXBElement<SchemaType> createSchema(SchemaType value) {
        return new JAXBElement<SchemaType>(_Schema_QNAME, SchemaType.class, null, value);
    }

}
