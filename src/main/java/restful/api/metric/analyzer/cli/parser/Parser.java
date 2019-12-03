package restful.api.metric.analyzer.cli.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;

public abstract class Parser {

	/**
	 * required for loadText() that converts text into a temporary file. 
	 * The temporary file can then be analyzed.
	 */
	protected String fileEnding;

	/**
	 * parses a file from a public URL
	 */
	public abstract Model.SpecificationFile loadPublicUrl(String url) throws ParseException;

	/**
	 * parses a file from a local file path
	 */
	public abstract Model.SpecificationFile loadLocalFile(String url) throws ParseException;

	/**
	 * Creates a temporary file from string, file is deleted after JVM is terminated.
	 *
	 * @param formattedString
	 * @return parsed internal model
	 * @throws IOException
	 * @throws ParseException 
	 */
	public String loadText(String formattedString) throws IOException, ParseException {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tempFile","." + fileEnding);
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))){
				bw.write(formattedString);
			}
			tempFile.deleteOnExit();
			return tempFile.getAbsolutePath();
		}catch(IOException e) {
			throw new IOException();
		}
	}
}