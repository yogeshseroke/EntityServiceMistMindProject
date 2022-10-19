package com.scai.entities.utilities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVUtilities {

	public static String CSV_TYPE = "text/csv";

	public static ByteArrayInputStream objectsToCSVCovertoer(List<String> listOfHeaders, List<Object[]> rowData) {
		log.debug("--- List Of Headers : " + listOfHeaders + "---");
		final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
			csvPrinter.printRecord(listOfHeaders);
			for (Object[] data : rowData) {
				ArrayList<Object> rowParams = new ArrayList<>();
				for (Object obj : data) {
					rowParams.add(obj);
				}
				csvPrinter.printRecord(rowParams);
			}
			csvPrinter.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			log.error("Exception in importing data to CSV file : " + e.getMessage());
			throw new RuntimeException("Fail to import data to CSV file: " + e.getMessage());
		}
	}

	public static boolean isCSVFile(MultipartFile csvFile) {
		if (CSV_TYPE.equals(csvFile.getContentType()) || csvFile.getContentType().equals("application/vnd.ms-excel")) {
			return true;
		}
		return false;
	}

	public static CSVParser getCSVParserfromFileObj(InputStream is) throws IOException {
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		return new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
	}

}
