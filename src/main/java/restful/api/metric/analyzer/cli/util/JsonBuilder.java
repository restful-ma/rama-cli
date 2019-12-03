package restful.api.metric.analyzer.cli.util;

import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonBuilder.class);
	private String prettyJson;
	private JsonObject jsonObject = new JsonObject();

	public JsonBuilder(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		formatJson();
	}


	/**
	 * formats the json object
	 */
	private void formatJson() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		prettyJson = gson.toJson(jsonObject);
	}


	/**
	 * creates a json file in the given output path
	 *
	 * @param path
	 */
	public void createFile(String path) {
		if(!path.endsWith(".json")) {
			path += ".json";
		}
		try(FileWriter writer = new FileWriter(path)) {
			writer.write(prettyJson);
		} catch (IOException e) {
			logger.error("Failed to create JSON file",e);
		}

	}

	public String getPrettyJson() {
		return this.prettyJson;
	}

	public JsonObject getJsonObject() {
		return this.jsonObject;
	}

}