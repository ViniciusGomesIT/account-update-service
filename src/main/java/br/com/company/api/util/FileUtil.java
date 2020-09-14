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
import java.util.Objects;
import java.util.Queue;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import br.com.company.api.contants.Constants;
import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.enums.OutputFileHeadersEnum;
import br.com.company.api.services.message.MessageService;

@Component
public class FileUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	
	@Autowired
	private MessageService messageService;

	public CSVParser getInputCsvFile(String csfFileName) {
		LOGGER.info("===================== FileUtil.getInputCsvFile ===> INIT with csvFileName: {}", csfFileName);
		
		CSVParser csvParser = null;
		
		if ( !Strings.isNullOrEmpty(csfFileName) ) {
			File csvFile = new File(csfFileName);
			
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
			
			LOGGER.info("===================== FileUtil.getInputCsvFile ===> returnin csvFileName: {}", csfFileName);
		} else {
			LOGGER.info("===================== FileUtil.getInputCsvFile ===> INIT NULL or EMPTY INPUT CSV FILE PATH, RETURNING NULL");
		}

		return csvParser;
	}

	public String createOutPutFile(String outputFileName, String outputFileExtension, boolean withTimeStamp) {
		File outputFile = null;
		
		LOGGER.info("===================== FileUtil.createOutPutFile ===> INIT");
		
		try {
			String filePath = System.getProperty("user.dir")
					.concat(Constants.PATH_SEPARATOR)
					.concat(outputFileName);
			
			if ( withTimeStamp ) {
				filePath = filePath.concat("_")
					.concat( new SimpleDateFormat(Constants.DEFAULT_TIME_STAMP_PATTERN).format(new Date()) );
			}
			
			filePath = filePath.concat(outputFileExtension);
			
			outputFile = new File(filePath);
			if ( !outputFile.exists() ) {
				outputFile.createNewFile();
				
				this.writeCsvHeader(outputFile.getAbsolutePath());
			}
			
			LOGGER.info("===================== FileUtil.createOutPutFile ===> END ===> OUTPUT FILE CREATED AT: {}", outputFile.getAbsolutePath());
			
		} catch (Exception e) {
			LOGGER.error("=============================================================");
			LOGGER.error(messageService.getMessage("error.creating.output.csv.file"));
			LOGGER.error("MESSAGE: {}", e.getMessage());
			LOGGER.error("STACK TRACE: {}", Arrays.toString(e.getStackTrace()));
			LOGGER.error("=============================================================");
		}
		
		
		return Objects.nonNull(outputFile)
				? outputFile.getAbsolutePath()
				: Constants.EMPTY_STRING;
	}

	private void writeCsvHeader(String absolutePath) {
		StringBuilder stringBuffer = new StringBuilder();
		
		Arrays.stream(OutputFileHeadersEnum.values())
			.sorted(Comparator.comparingInt(OutputFileHeadersEnum::getHeaderOrder))
			.forEach(value -> stringBuffer.append(value.getHeaderName().concat(Constants.CSV_SEPARATOR)));
		
		this.writeFile(absolutePath, stringBuffer.toString());
	}

	/*
	 * A escrita do CSV poderia ser feita de forma Assíncrona com @Async 
	 * caso não seja necessário manter a ordem de execução
	 * */
	public void writeOutputFile(String fullFileNamePath, Queue<AccountInfoDTO> accountsProcessed) {
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> INIT");
		
		StringBuilder outputDataBuilder = new StringBuilder();
		
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> BUILDING INFORMATION ===> INIT");
		accountsProcessed.parallelStream()
			.forEachOrdered(processedAccountInfo -> this.buildOutputData(outputDataBuilder, processedAccountInfo));
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> BUILDING INFORMATION ===> DONE");
		
		
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> WRITING OUTPUTFILE ===> INIT");
		this.writeFile(fullFileNamePath, outputDataBuilder.toString());
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> WRITING OUTPUTFILE ===> DONE");

		
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> END");
	}
	
	private void buildOutputData(StringBuilder outputDataBuilder, AccountInfoDTO processedAccountInfo) {
		AccountInfoDTO accountInfoDTO = processedAccountInfo;
		
		if ( outputDataBuilder.length() > 0 ) {
			outputDataBuilder.append(Constants.NEW_LINE_FUNCTION);
		}
		
		outputDataBuilder.append(accountInfoDTO.getAgencia());
		outputDataBuilder.append(Constants.CSV_SEPARATOR);
		outputDataBuilder.append(accountInfoDTO.getConta());
		outputDataBuilder.append(Constants.CSV_SEPARATOR);
		outputDataBuilder.append(accountInfoDTO.getSaldo());
		outputDataBuilder.append(Constants.CSV_SEPARATOR);
		outputDataBuilder.append(accountInfoDTO.getStatus());
		outputDataBuilder.append(Constants.CSV_SEPARATOR);
		outputDataBuilder.append(accountInfoDTO.getProcessedStatus());
		outputDataBuilder.append(Constants.CSV_SEPARATOR);
		
		String error =  Strings.isNullOrEmpty(accountInfoDTO.getProcessError()) 
				? Constants.DEFAULT_ERROR_MESSAGE 
				: accountInfoDTO.getProcessError();
		
		outputDataBuilder.append(error);
	}
	
	public void writeFile(String outputFilePath, String message) {
		try ( BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, Boolean.TRUE)) ) {
			
			writer.append(message);
			writer.append(Constants.NEW_LINE_FUNCTION);
			
	    } catch (Exception e) {
	    	LOGGER.error("==============================================");
	    	LOGGER.error(messageService.getMessage("error.write.output.file", outputFilePath));
	    	LOGGER.error("MESSAGE: {}", e.getMessage());
	    	LOGGER.error("STACK TRACE: {}", Arrays.toString(e.getStackTrace()));
	    	LOGGER.error("==============================================");
		}
	}
}

