package restful.api.metric.analyzer.cli.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfCreator {
	
	private static final Logger logger = LoggerFactory.getLogger(PdfCreator.class);
	private Map<String, Object> hmap;
	private Document document;

	public PdfCreator(Map<String, Object> hmap) {
		this.hmap = hmap;
	}

	/**
	 * creates a PDF document and adds the keys and values from the metric hashmap into the
	 * document. Saves the file in the given output path.
	 *
	 * @param path
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	public Document createPdf(String path) {
		if(!path.endsWith(".pdf")) {
			path += ".pdf";
		}

		document = new Document();
		try {
			@SuppressWarnings("unused")
			PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path));
		} catch (DocumentException e) {
			logger.error("DocumentException!",e);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException!",e);
		}
		document.open();

		Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLDITALIC);
		Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLDITALIC);
		Font metricNameFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLDITALIC);
		Font blueFont = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLUE);
		Font regular = new Font(FontFamily.HELVETICA, 11);

		Paragraph titleParagraph = new Paragraph();
		titleParagraph.setSpacingAfter(20);
		titleParagraph.setAlignment(1);

		Paragraph generalSectionTitleParagraph = new Paragraph();
		generalSectionTitleParagraph.setSpacingBefore(10);
		generalSectionTitleParagraph.setSpacingAfter(10);

		Paragraph evaluationSectionTitleParagraph = new Paragraph();
		evaluationSectionTitleParagraph.setSpacingBefore(20);
		evaluationSectionTitleParagraph.setSpacingAfter(10);

		Paragraph generalSectionParagraph = new Paragraph();

		Paragraph evaluationSectionParagraph = new Paragraph();

		for (Map.Entry<String, Object> entry : hmap.entrySet()) {

			// set title of the document and title of the general information section
			if ("API title".equals(entry.getKey())) {
				Chunk titleChunk = new Chunk("Evaluation of " + entry.getValue() + "\n", titleFont);
				titleParagraph.add(titleChunk);
				Chunk generalInformationChunk = new Chunk("General information \n", sectionFont);
				generalSectionTitleParagraph.add(generalInformationChunk);

			}

			// underline URL and turn URL blue
			else if ("URL path".equals(entry.getKey())) {
				Chunk urlChunk = new Chunk(entry.getValue() + "\n", blueFont);
				urlChunk.setUnderline(1f, -1);
				generalSectionParagraph.add("URL path: ");
				generalSectionParagraph.add(urlChunk);
			}

			// search for the entry with the key "measurement" in the Map
			else if ("measurement".equals(entry.getKey())) {
				// add title for the evaluation
				Chunk evaluationTitle = new Chunk("Evaluation results \n", sectionFont);
				evaluationSectionTitleParagraph.add(evaluationTitle);

				// the value of the "measurement" key has a list of Map<String, Object>
				List<Map<String, Object>> measurementList =
						(List<Map<String, Object>>) entry.getValue();

				// iterate through measurementList
				for (Map<String, Object> measurement : measurementList) {

					// iterate through measurement
					for (Map.Entry<String, Object> measurementEntry : measurement.entrySet()) {
						// set name of the metric as title for each measurement
						if ("Metric name".equals(measurementEntry.getKey())) {
							evaluationSectionParagraph.add(
									new Chunk(measurementEntry.getValue() + "\n", metricNameFont));
						} else {
							evaluationSectionParagraph.add(new Chunk(measurementEntry.getKey()
									+ ": " + measurementEntry.getValue() + "\n", regular));
						}
					}
				}
			} else {
				generalSectionParagraph
						.add(new Chunk(entry.getKey() + ": " + entry.getValue() + "\n", regular));
			}
		}

		try {
			document.add(titleParagraph);
			document.add(generalSectionTitleParagraph);
			document.add(generalSectionParagraph);
			document.add(evaluationSectionTitleParagraph);
			document.add(evaluationSectionParagraph);
		} catch (DocumentException e) {
			logger.error("DocumentException!",e);
		}

		document.close();
		return document;

	}

	/**
	 * Creates human readable pdf evaluation document. no path needed, document is not saved
	 *
	 * @param myDocument
	 * @return
	 */
	public Document createPdfWithoutPath(Document myDocument) throws DocumentException {

		Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLDITALIC);
		Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLDITALIC);
		Font metricNameFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLDITALIC);
		Font blueFont = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLUE);
		Font regular = new Font(FontFamily.HELVETICA, 11);

		Paragraph titleParagraph = new Paragraph();
		titleParagraph.setSpacingAfter(20);
		titleParagraph.setAlignment(1);

		Paragraph generalSectionTitleParagraph = new Paragraph();
		generalSectionTitleParagraph.setSpacingBefore(10);
		generalSectionTitleParagraph.setSpacingAfter(10);

		Paragraph evaluationSectionTitleParagraph = new Paragraph();
		evaluationSectionTitleParagraph.setSpacingBefore(20);
		evaluationSectionTitleParagraph.setSpacingAfter(10);

		Paragraph generalSectionParagraph = new Paragraph();

		Paragraph evaluationSectionParagraph = new Paragraph();

		for (Map.Entry<String, Object> entry : hmap.entrySet()) {

			// set title of the document and title of the general information section
			if ("API title".equals(entry.getKey())) {
				Chunk titleChunk = new Chunk("Evaluation of " + entry.getValue() + "\n", titleFont);
				titleParagraph.add(titleChunk);
				Chunk generalInformationChunk = new Chunk("General information \n", sectionFont);
				generalSectionTitleParagraph.add(generalInformationChunk);

			}

			// underline URL and turn URL blue
			else if ("URL path".equals(entry.getKey())) {
				Chunk urlChunk = new Chunk(entry.getValue() + "\n", blueFont);
				urlChunk.setUnderline(1f, -1);
				generalSectionParagraph.add("URL path: ");
				generalSectionParagraph.add(urlChunk);
			}

			// search for the entry with the key "measurement" in the Map
			else if ("measurement".equals(entry.getKey())) {
				// add title for the evaluation
				Chunk evaluationTitle = new Chunk("Evaluation results \n", sectionFont);
				evaluationSectionTitleParagraph.add(evaluationTitle);

				// the value of the "measurement" key has a list of Map<String, Object>
				List<Map<String, Object>> measurementList =
						(List<Map<String, Object>>) entry.getValue();

				// iterate through measurementList
				for (Map<String, Object> measurement : measurementList) {

					// iterate through measurement
					for (Map.Entry<String, Object> measurementEntry : measurement.entrySet()) {
						// set name of the metric as title for each measurement
						if ("Metric name".equals(measurementEntry.getKey())) {
							evaluationSectionParagraph.add(
									new Chunk(measurementEntry.getValue() + "\n", metricNameFont));
						} else {
							evaluationSectionParagraph.add(new Chunk(measurementEntry.getKey()
									+ ": " + measurementEntry.getValue() + "\n", regular));
						}
					}
				}
			} else {
				generalSectionParagraph
						.add(new Chunk(entry.getKey() + ": " + entry.getValue() + "\n", regular));
			}
		}

		myDocument.add(titleParagraph);
		myDocument.add(generalSectionTitleParagraph);
		myDocument.add(generalSectionParagraph);
		myDocument.add(evaluationSectionTitleParagraph);
		myDocument.add(evaluationSectionParagraph);

		// document.close();

		return myDocument;

	}

	public Document getDocument() {
		return document;
	}

}