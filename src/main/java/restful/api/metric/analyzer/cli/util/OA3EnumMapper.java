package restful.api.metric.analyzer.cli.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;

public class OA3EnumMapper {
	private static final Logger logger = LoggerFactory.getLogger(OA3EnumMapper.class);

	private OA3EnumMapper() {
	    throw new IllegalStateException("Utility class");
	  }
	
	public static Model.DataType getEnumForOA3DataType(String dataType, String format) {

		if (dataType == null) {
			//Often Data which is described via $ref in OA3 don't set the DataType
			return Model.DataType.OBJECT;
		} else if (dataType.equals("integer")) {
			if (format == null) {
				return Model.DataType.INTEGER;
			}
			switch (format) {
				case "int32":
					return Model.DataType.INTEGER;
				case "int64":
					return Model.DataType.LONG;
				default:
					return Model.DataType.INTEGER;
			}
		} else if (dataType.equals("number")) {
			if (format == null) {
				return Model.DataType.INTEGER;
			}
			switch (format) {
				case "double":
					return Model.DataType.DOUBLE;
				case "float":
					return Model.DataType.FLOAT;
				default:
					return Model.DataType.INTEGER;
			}
		}

		switch (dataType) {
			case "string":
				return Model.DataType.STRING;
			case "boolean":
				return Model.DataType.BOOLEAN;
			case "array":
				return Model.DataType.ARRAY;
			case "object":
				return Model.DataType.OBJECT;
			default:
				logger.info("Unrecognized DataType in OpenApi3: {} will be treated as OBJECT DataType", dataType);
				return Model.DataType.OBJECT;
		}
	}

	public static Model.ParameterLocation getEnumForOA3Location(String location) {
		switch (location) {
			case "path":
				return Model.ParameterLocation.PATH;
			case "query":
				return Model.ParameterLocation.QUERY;
			case "header":
				return Model.ParameterLocation.HEADER;
			case "cookie":
				return Model.ParameterLocation.COOKIE;
			default:
				logger.info("Unrecognized Location in OpenApi3 of type: {} .Following \"in\": values are known path,query,header and cookie", location);

//			Normally the KaizenParser consider a file invalid when no parameter location was set
//			https://swagger.io/docs/specification/describing-parameters/
// 			at least the swagger editor tells that there is an structural error 
//			when the location -> "in" property was not set in the file
				return null;
		}


	}
}