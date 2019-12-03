
package restful.api.metric.analyzer.cli.model.generated.wadl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ParamStyle.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ParamStyle">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="plain"/>
 *     &lt;enumeration value="query"/>
 *     &lt;enumeration value="matrix"/>
 *     &lt;enumeration value="header"/>
 *     &lt;enumeration value="template"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ParamStyle", namespace = "http://wadl.dev.java.net/2009/02")
@XmlEnum
public enum ParamStyle {

    @XmlEnumValue("plain")
    PLAIN("plain"),
    @XmlEnumValue("query")
    QUERY("query"),
    @XmlEnumValue("matrix")
    MATRIX("matrix"),
    @XmlEnumValue("header")
    HEADER("header"),
    @XmlEnumValue("template")
    TEMPLATE("template");
    private final String value;

    ParamStyle(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ParamStyle fromValue(String v) {
        for (ParamStyle c: ParamStyle.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
