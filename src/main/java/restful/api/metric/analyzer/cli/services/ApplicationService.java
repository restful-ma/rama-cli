package restful.api.metric.analyzer.cli.services;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import restful.api.metric.analyzer.cli.metrics.IMetric;
import restful.api.metric.analyzer.cli.model.Evaluation;
import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.RestfulService;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.Parser;
import restful.api.metric.analyzer.cli.parser.ParserFactory;
import restful.api.metric.analyzer.cli.util.JsonBuilder;
import restful.api.metric.analyzer.cli.util.RestfulServiceMapper;
import restful.api.metric.analyzer.cli.util.PdfCreator;

public class ApplicationService {

	private Model.SpecificationFile model;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	private String urlPath;
	private JsonObject fullApiAnalysisJsonObject;
	private RestfulService restfulService;
	private Evaluation evaluation;
	private List<Measurement> measurementList;
	private RestfulServiceMapper mapper = new RestfulServiceMapper();
	private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

	/**
	 * parses a file from a public URL
	 *
	 * @param path
	 */
	public void loadURIPath(String path, String format) throws ParseException {
		this.urlPath = path;

		this.model = getParser(format).loadPublicUrl(urlPath);
	}

	/**
	 * parses a file from a local file path
	 *
	 * @param path
	 */
	public void loadLocalFilePath(String path, String format) throws ParseException {
		this.urlPath = path;

		this.model = getParser(format).loadLocalFile(path);
	}

	/**
	 * creates and parses temporary file from string
	 *
	 * @param filename
	 * @throws IOException 
	 */
	public String createTemporaryFile(String yamlString, String format) throws ParseException, IOException {
		String file = getParser(format).loadText(yamlString);
		this.model = getParser(format).loadLocalFile(file);
		return file;
	}

	/**
	 * creates a RestfulService Object with all metrics which inherits from the IMetric Interface
	 * from the loaded model. If uses for single Evaluation, the file name is added to the object as
	 * the file is not stored in the DB.
	 *
	 * @param singleEval
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 */
	public void createRestfulService(boolean singleEval)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, InstantiationException {
		Date date = new Date();
		File url = null;
		if (!singleEval) {
			url = new File(urlPath);
		}

		measurementList = new ArrayList<>();

		// loads all classes which inherits from the IMetric Interface.
		Reflections reflections = new Reflections("restful.api.metric.analyzer.cli.metrics");
		Set<Class<? extends IMetric>> classes = reflections.getSubTypesOf(IMetric.class);

		for (Class<?> metricClass : classes) {

			Object metricObject = metricClass.newInstance();

			Method setModelMethod = metricObject.getClass().getMethod("setModel", Model.SpecificationFile.class);
			setModelMethod.invoke(metricObject, model);

			Method calculateMetricMethod = metricObject.getClass().getMethod("calculateMetric");
			calculateMetricMethod.invoke(metricObject);

			Method getMeasurementMethod = metricObject.getClass().getMethod("getMeasurement");

			measurementList.add((Measurement) getMeasurementMethod.invoke(metricObject));
		}

		if (singleEval) {
			evaluation = new Evaluation("evaluationName", model.getApiVersion(),
					"single evaluation", model.getSpecificationDescriptor().getSpecificationFormat().toString(), dateFormat.format(date), measurementList);
		} else {
 			evaluation = new Evaluation("evaluationName", model.getApiVersion(),
					url.getName(), model.getSpecificationDescriptor().getSpecificationFormat().toString(), dateFormat.format(date), measurementList);
		}

		restfulService = new RestfulService();
		restfulService.setServiceTitle(model.getTitle());
		if (singleEval) {
			restfulService.setApiFileUrl(urlPath);
		}
		restfulService.addEvaluation(evaluation);
	}

	/**
	 * export a JSON file with the values of the JsonObject in the specified output path.
	 *
	 * @param path
	 */
	public void exportAsJson(String path) {
		JsonObject json = mapper.createJsonObject(restfulService);

		JsonBuilder jsonBuilder = new JsonBuilder(json);
		jsonBuilder.createFile(path);
		fullApiAnalysisJsonObject = json;
	}

	/**
	 * logs the created JsonObject in the command line.
	 */
	public void commandLineLogger() {
		JsonBuilder jsonBuilder = new JsonBuilder(mapper.createJsonObject(restfulService));
		logger.info(jsonBuilder.getPrettyJson());
	}

	/**
	 * export a PDF file with the values of the hashmap in the specified output path.
	 *
	 * @param path
	 */

	public void exportAsPDF(String path) {
		PdfCreator pdfCreator = new PdfCreator(mapper.createMapObject(restfulService));
		pdfCreator.createPdf(path);
	}

	public Model.SpecificationFile getModel() {
		return this.model;
	}

	public JsonObject getJsonObject() {
		return fullApiAnalysisJsonObject;
	}

	public RestfulService getRestfulService() {
		return restfulService;
	}

	public List<Measurement> getMeasurementList() {
		return measurementList;
	}

	public void setMeasurementObjectList(List<Measurement> measurementList) {
		this.measurementList = measurementList;
	}

	private Parser getParser(String format) {
		return ParserFactory.getParser(format);
		
	}
}