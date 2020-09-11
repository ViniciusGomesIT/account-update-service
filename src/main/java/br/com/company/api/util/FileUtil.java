package br.com.company.api.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.company.api.enums.OutputFileHeadersEnum;
import br.com.company.api.services.message.MessageService;

@Component
public class FileUtil {

	private static final String PATH_SEPARATOR = "/";
	private static final String NEW_LINE_FUNCTION = "\n";
	private static final String DEFAULT_TIME_STAMP_PATTERN = "dd_MM_yyyy_HH_mm_ss";
	private static final String CSV_SEPARATOR = ";";

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	
	@Autowired
	private MessageService messageService;

	public CSVParser getInputCsvFile(String csfFileName) {
		LOGGER.info("===================== FileUtil.getInputCsvFile ===> INIT with csvFileName: {}", csfFileName);

		File csvFile = new File(csfFileName);
		CSVParser csvParser = null;

		try {
			
			FileInputStream fileInputStream = new FileInputStream(csvFile);
			csvParser = CSVFormat.EXCEL.withDelimiter(';').parse(new InputStreamReader(fileInputStream));

		} catch (IOException e) {
			LOGGER.error("=============================================================");
			LOGGER.error(messageService.getMessage("error.getting.csv.file", csfFileName));
			LOGGER.error("MESSAGE: {}", e.getMessage());
			LOGGER.error("STACK TRACE: {}", Arrays.toString(e.getStackTrace()));
			LOGGER.error("=============================================================");
		}

		LOGGER.info("===================== FileUtil.getInputCsvFile ===> END with csvFileName: {}", csfFileName);

		return csvParser;
	}

	public String createOutPutFile(String outputFileName, String outputFileExtension, boolean withTimeStamp) {
		File outputFile = null;
		
		try {
			String filePath = System.getProperty("user.dir")
					.concat(PATH_SEPARATOR)
					.concat(outputFileName);
			
			if ( withTimeStamp ) {
				filePath = filePath.concat("_")
					.concat( new SimpleDateFormat(DEFAULT_TIME_STAMP_PATTERN).format(new Date()) );
			}
			
			filePath = filePath.concat(outputFileExtension);
			
			outputFile = new File(filePath);
			if ( !outputFile.exists() ) {
				outputFile.createNewFile();
				
				this.writeCsvHeader(outputFile.getAbsolutePath());
			}
			
		} catch (Exception e) {
			LOGGER.error("=============================================================");
			LOGGER.error(messageService.getMessage("error.creating.output.csv.file"));
			LOGGER.error("MESSAGE: {}", e.getMessage());
			LOGGER.error("STACK TRACE: {}", Arrays.toString(e.getStackTrace()));
			LOGGER.error("=============================================================");
		}
		
		return outputFile.getAbsolutePath();
	}

	private void writeCsvHeader(String absolutePath) {
		StringBuilder stringBuffer = new StringBuilder();
		
		Arrays.stream(OutputFileHeadersEnum.values())
			.sorted(Comparator.comparingInt(OutputFileHeadersEnum::getHeaderOrder))
			.forEach(value -> stringBuffer.append(value.getHeaderName().concat(CSV_SEPARATOR)));
		
		this.writeInOutputFile(absolutePath, stringBuffer.toString());
	}

	public void writeInOutputFile(String outputFilePath, String message) {
		try ( BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, Boolean.TRUE)) ) {
			
			writer.append(message);
			writer.append(NEW_LINE_FUNCTION);
			
	    } catch (Exception e) {
	    	LOGGER.error("==============================================");
	    	LOGGER.error(messageService.getMessage("error.write.output.file", outputFilePath));
	    	LOGGER.error("MESSAGE: {}", e.getMessage());
	    	LOGGER.error("STACK TRACE: {}", Arrays.toString(e.getStackTrace()));
	    	LOGGER.error("==============================================");
		}
	}
}

