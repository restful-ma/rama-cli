package restful.api.metric.analyzer.cli.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import restful.api.metric.analyzer.cli.model.Evaluation;
import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.RestfulService;

/**
 * Creates a JsonObject or Maps that can be used to create JSON, PDF 
 * or console output for a restfulService with its evaluation and measurements.
 * 
 */
public class RestfulServiceMapper {

	/**
	 * creates a JsonObject from a given RestfulService Object.
	 *
	 * @param restfulService
	 * @return
	 */
	public JsonObject createJsonObject(RestfulService restfulService) {
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("apiTitle", restfulService.getServiceTitle());
		resultJson.addProperty("apiFileUrl", restfulService.getApiFileUrl());
		JsonArray measurementArray = new JsonArray();
		for (Evaluation evaluation : restfulService.getEvaluationList()) {

			resultJson.addProperty("apiVersion", evaluation.getVersion());
			resultJson.addProperty("apiFileName", evaluation.getFileName());
			resultJson.addProperty("apiFormat", evaluation.getApiFormat());
			resultJson.addProperty("measurementDate", evaluation.getMeasurementDate());

			for (Measurement measurement : evaluation.getMeasurementList()) {
				JsonObject tempMeasurement = new JsonObject();
				tempMeasurement.addProperty("metricName", measurement.getMetricName());
				tempMeasurement.addProperty("metricValue", measurement.getMetricValue());
				tempMeasurement.addProperty("additionalInformation",
						measurement.getAdditionalInformation());
				measurementArray.add(tempMeasurement);
				resultJson.add("measurement", measurementArray);
			}
		}
		return resultJson;
	}

	/**
	 * creates a Map<String, Object> from a given RestfulService Object for PdfCreator .
	 *
	 * @param restfulService
	 * @return
	 */
	public Map<String, Object> createMapObject(RestfulService restfulService) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		resultMap.put("API title", restfulService.getServiceTitle());
		resultMap.put("URL path", restfulService.getApiFileUrl());
		for (Evaluation evaluation : restfulService.getEvaluationList()) {
			resultMap.put("API version", evaluation.getVersion());
			resultMap.put("API file name", evaluation.getFileName());
			resultMap.put("API format", evaluation.getApiFormat());
			resultMap.put("Date of the measurement", evaluation.getMeasurementDate());

			resultMap.put("measurement", buildMeasurementList(evaluation));
		}
		return resultMap;
	}

	/**
	 * creates a Map<String, Object> from a given RestfulService Object for PdfCreator for specific
	 * evaluation
	 *
	 * @param restfulService
	 * @return
	 */
	public Map<String, Object> createMapObjectForPDF(RestfulService restfulService,
													 Evaluation evaluation) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		resultMap.put("API title", restfulService.getServiceTitle());
		resultMap.put("URL path", restfulService.getApiFileUrl());
		resultMap.put("API version", evaluation.getVersion());
		resultMap.put("API file name", evaluation.getFileName());
		resultMap.put("API format", evaluation.getApiFormat());
		resultMap.put("Date of the measurement", evaluation.getMeasurementDate());

		resultMap.put("measurement", buildMeasurementList(evaluation));
		return resultMap;
	}

	private List<Map<String, Object>> buildMeasurementList(Evaluation evaluation) {
		List<Map<String, Object>> measurementList = new ArrayList<>();
		for (Measurement measurement : evaluation.getMeasurementList()) {
			Map<String, Object> tempMeasurementMap = new LinkedHashMap<>();
			tempMeasurementMap.put("Metric name", measurement.getMetricName());
			tempMeasurementMap.put("Metric value", measurement.getMetricValue());
			tempMeasurementMap.put("Additional information",
					measurement.getAdditionalInformation());
			measurementList.add(tempMeasurementMap);
		}
		return measurementList;
	}
}