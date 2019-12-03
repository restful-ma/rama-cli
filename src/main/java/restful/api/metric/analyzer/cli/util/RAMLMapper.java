package restful.api.metric.analyzer.cli.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonObject;

public class RAMLMapper {

	public JsonObject createJsonObject(Object object) {
		JsonObject resultJson = new JsonObject();

		return resultJson;
	}

	public Map<String, Object> createMapObject(Object object) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		return resultMap;
	}

	public Map<String, Object> createMapObjectForPdf(Object object) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		return resultMap;
	}
}