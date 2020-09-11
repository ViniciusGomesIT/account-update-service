package br.com.company.api.services.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import br.com.company.api.builders.account.AccountInfoDTOBuilder;
import br.com.company.api.contants.Constants;
import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.enums.AccountInfoIndexConfigurationEnum;
import br.com.company.api.enums.AccountStatusEnum;
import br.com.company.api.properties.AccountUpdateProperties;
import br.com.company.api.services.message.MessageService;
import br.com.company.api.services.process.ProcessDataService;
import br.com.company.api.util.DataFormatterUtils;
import br.com.company.api.util.FileUtil;

@Service
public class AccountUpdateService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountUpdateService.class);
	
	private DataFormatterUtils dataFormatterUtils;
	private ProcessDataService processDataService;
	private FileUtil fileUtil;
	private MessageService messageService;
	private AccountUpdateProperties properties;
	
	private long maxLineNumber;
	
	//TODO Verificar USO dos erros com a linha
	private ConcurrentLinkedQueue<String> erros;

	@Autowired
	public AccountUpdateService(FileUtil fileUtil, 
			DataFormatterUtils dataFormatterUtils,
			ProcessDataService processDataService,
			MessageService messageService,
			AccountUpdateProperties properties) {
		
		this.fileUtil = fileUtil;
		this.dataFormatterUtils = dataFormatterUtils;
		this.processDataService = processDataService;
		this.messageService = messageService;
		this.properties = properties;
	}
	
	@PostConstruct
	public void setup() {
		maxLineNumber = properties.getMaxChunkSizeProcessPerTime();
		erros = new ConcurrentLinkedQueue<>();
	}
	
	//TODO REVER PASSAGEM DE PARÂMETRO
	public void readAndProcessFile(String inputFilePath) {
		LOGGER.info("================== AccountUpdateService.readFile ===> INIT");

		//TODO REMOVER ESTE CÒDIGO
		inputFilePath = "C:\\Users\\Vini\\Documents\\vini_test\\teste.csv";
		CSVParser inputDataCsvParser = fileUtil.getInputCsvFile(inputFilePath);
		
		if ( Objects.nonNull(inputDataCsvParser) ) {
			LOGGER.info("================== AccountUpdateService.readFile ===> FILE: {} FOUND. INITIALIZIN READING", inputFilePath);
			
			String fullOutputFilePath = fileUtil.createOutPutFile(properties.getOutputFileName(), properties.getOutputFileExtension(), Boolean.TRUE);
			
			if ( Strings.isNullOrEmpty(fullOutputFilePath) ) {
				LOGGER.info("================== AccountUpdateService.readFile ===> NO OUTPUT FILE CREATED, ABORTING...");
			} else {
				processCsvLine(inputDataCsvParser, fullOutputFilePath);
			}
			
		} else {
			LOGGER.info("================== AccountUpdateService.readFile ===> File: {} NOT found, aborting...");	
		}
		
		LOGGER.info("================== AccountUpdateService.readFile ===> END");
	}
	
	private void processCsvLine(CSVParser inputDataCsvParser, String fullOutputFilePath) {
		List<AccountInfoDTO> accountsToUpdate = new ArrayList<>();
		
		for (CSVRecord csvRecord : inputDataCsvParser) {
			
			//Skipping header line
			if ( inputDataCsvParser.getCurrentLineNumber() == 1 ) {
				continue;
			}
			
			this.buildAccountInfoDTOAndAddToUpdateList(csvRecord, accountsToUpdate, inputDataCsvParser.getCurrentLineNumber());
			
			if ( inputDataCsvParser.getCurrentLineNumber() == maxLineNumber 
					|| !inputDataCsvParser.iterator().hasNext() ) {
				
				Queue<AccountInfoDTO> processedDataFutures = new ConcurrentLinkedQueue<>();
				
				try {
					processedDataFutures = processDataService.processAccountRegistersMultiThreading(accountsToUpdate);
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.error("=============================================");
					LOGGER.error(messageService.getMessage("error.while.trying.to.execute.integration"));
					LOGGER.error("MESSAGE: {}", e.getMessage());
					LOGGER.error("STACK TRACE: {}", Arrays.toString(e.getStackTrace()));
				}
				
				this.writeOutputFile(fullOutputFilePath, processedDataFutures);
				
				maxLineNumber += maxLineNumber;
				accountsToUpdate.clear();
			}
		}
	}

	private void buildAccountInfoDTOAndAddToUpdateList(CSVRecord csvRecord, List<AccountInfoDTO> accountsToUpdate, long lineNumber) {
		try {
			String agencia 		= dataFormatterUtils.formatAgencyData( csvRecord.get(AccountInfoIndexConfigurationEnum.AGENCY.getIndex()) );
			String conta 		= dataFormatterUtils.formatAccountData( csvRecord.get(AccountInfoIndexConfigurationEnum.ACCOUNT.getIndex()) );
			Double saldo 		= dataFormatterUtils.formatBalanceData( csvRecord.get(AccountInfoIndexConfigurationEnum.BALANCE.getIndex()) );
			
			AccountInfoDTO accountInfoDTO = AccountInfoDTOBuilder.getInstance()
					.withAgencia( agencia )
					.withConta( conta )
					.withSaldo( saldo )
					.withStatus( AccountStatusEnum.valueOf(csvRecord.get(AccountInfoIndexConfigurationEnum.STATUS.getIndex()))	)
					.build();
			
			accountsToUpdate.add(accountInfoDTO);
		} catch (Exception e) {
			String errorMessage = messageService.getMessage("error.while.parsing.input.file.data", String.valueOf(lineNumber));
			LOGGER.error("==========================================================");
			LOGGER.error(errorMessage);
			LOGGER.error("==========================================================");
			
			erros.add(errorMessage);
		}
	}
	
	/*
	 * A escrita do CSV poderia ser feita de forma Assíncrona com @Async 
	 * caso não fosse necessário manter a ordem de execução
	 * */
	private void writeOutputFile(String fullFileNamePath, Queue<AccountInfoDTO> processedDataFutures) {
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> INIT");
		
		StringBuffer outputDataBuffer = new StringBuffer();
		
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> BUILDING INFORMATION ===> INIT");
		processedDataFutures.parallelStream()
			.forEachOrdered(processedAccountInfo -> this.buildOutputData(outputDataBuffer, processedAccountInfo));
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> BUILDING INFORMATION ===> DONE");
		
		
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> WRITING OUTPUTFILE ===> INIT");
		fileUtil.writeInOutputFile(fullFileNamePath, outputDataBuffer.toString());
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> WRITING OUTPUTFILE ===> DONE");

	
		LOGGER.info("================== AccountUpdateService.writeOutFile ===> END");
	}

	private void buildOutputData(StringBuffer outputDataBuffer, AccountInfoDTO processedAccountInfo) {
		AccountInfoDTO accountInfoDTO = processedAccountInfo;
		
		if ( outputDataBuffer.length() > 0 ) {
			outputDataBuffer.append(Constants.NEW_LINE_FUNCTION);
		}
		
		outputDataBuffer.append(accountInfoDTO.getAgencia());
		outputDataBuffer.append(Constants.CSV_SEPARATOR);
		outputDataBuffer.append(accountInfoDTO.getConta());
		outputDataBuffer.append(Constants.CSV_SEPARATOR);
		outputDataBuffer.append(accountInfoDTO.getSaldo());
		outputDataBuffer.append(Constants.CSV_SEPARATOR);
		outputDataBuffer.append(accountInfoDTO.getStatus());
		outputDataBuffer.append(Constants.CSV_SEPARATOR);
		outputDataBuffer.append(accountInfoDTO.getProcessedStatus());
		outputDataBuffer.append(Constants.CSV_SEPARATOR);
		
		String error =  Strings.isNullOrEmpty(accountInfoDTO.getProcessError()) 
				? Constants.DEFAULT_ERROR_MESSAGE 
				: accountInfoDTO.getProcessError();
		
		outputDataBuffer.append(error);
			
	}
}
