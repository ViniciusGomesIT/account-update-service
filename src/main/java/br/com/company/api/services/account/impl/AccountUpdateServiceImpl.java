package br.com.company.api.services.account.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import br.com.company.api.services.account.AccountUpdateService;
import br.com.company.api.services.message.MessageService;
import br.com.company.api.services.process.impl.ProcessDataServiceImpl;
import br.com.company.api.util.DataFormatterUtils;
import br.com.company.api.util.FileUtil;

@Service
public class AccountUpdateServiceImpl implements AccountUpdateService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountUpdateServiceImpl.class);
	
	private DataFormatterUtils dataFormatterUtils;
	private ProcessDataServiceImpl processDataService;
	private FileUtil fileUtil;
	private MessageService messageService;
	private AccountUpdateProperties properties;
	
	
	private long maxChunkSize;
	
	private Queue<String> errors;
	
	@Autowired
	public AccountUpdateServiceImpl(FileUtil fileUtil, 
			DataFormatterUtils dataFormatterUtils,
			ProcessDataServiceImpl processDataService,
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
		maxChunkSize = properties.getMaxChunkSizeProcessPerTime();
		errors = new ConcurrentLinkedQueue<>();
	}
	
	@Override
	public void readAndProcessFile(String inputFilePath) {
		LOGGER.info("================== AccountUpdateService.readFile ===> INIT");

		//TODO REMOVER ESTE CÒDIGO
		inputFilePath = "C:\\Users\\Vini\\Documents\\vini_test\\teste.csv";
		
		/*
		 * 1 - O LOAD DO CSV PODERIA SER FEITO TAMBÉM COM MULTITHREAD CASO A ORDEM NÃO SEJA NECESSÁRIA OU
		 *     SE HOUVESSE UMA ESTAMPA TEMPORAL; 
		 * 2 - O LOAD DO CSV PODERIA TAMBÉM SER FEITO DE UMA VEZ, TRANSFORMANDO TODAS AS LINHAS DO ARQUIVO 
		 *     EM STRING DE DADOS OU LISTA DE STRINGS OU STRING BUFFER OU LISTA DE OBJETOS PARA EVITAR MITIGAR 
		 *     AS CHANCES DE UMA IO EXCEPTION DURANTE O PROCESSAMENTO;
		 * */
		CSVParser inputDataCsvParser = fileUtil.getInputCsvFile(inputFilePath);
		
		if ( Objects.nonNull(inputDataCsvParser) ) {
			LOGGER.info("================== AccountUpdateService.readFile ===> FILE: {} FOUND. INITIALIZING PROCESSING", inputFilePath);
			
			/*
			 * A CRIACAO DO ARQUIVO DE SAÍDA PODERIA SER FEITA DE FORMA ASSÍNCRONA COM @Async E UMA VALIDAÇÃO DESTA CRIAÇÃO
			 * SERIA FEITA ANTES DE ESCREVER NESTE ARQUIVO, CASO HOUVESSE ALGUM PROBLEMA NA CRIAÇÃO, A EXECUÇÃO SERIA
			 * INTERROMPIDA
			 * */
			String fullOutputFilePath = fileUtil.createOutPutFile(properties.getOutputFileName(), properties.getOutputFileExtension(), Boolean.TRUE);
			
			if ( !Strings.isNullOrEmpty(fullOutputFilePath) ) {
				processCsvLine(inputDataCsvParser, fullOutputFilePath);
			} else {
				LOGGER.info("================== AccountUpdateService.readFile ===> NO OUTPUT FILE CREATED, ABORTING...");
			}
			
		} else {
			LOGGER.info("================== AccountUpdateService.readFile ===> File: {} NOT found, aborting...");	
		}
		
		if ( !errors.isEmpty() ) {
			String errorsParsed = dataFormatterUtils.formatErrors(errors);
			LOGGER.info("================== {}", messageService.getMessage("error.processing.data", errorsParsed));
		} else {
			LOGGER.info("================== AccountUpdateService.readFile ===> END WITH NO ERRORS");
		}
	}
	
	private void processCsvLine(CSVParser inputDataCsvParser, String fullOutputFilePath) {
		List<AccountInfoDTO> accountsToUpdate = new ArrayList<>();
		
		/*
		 * CASO A ORDEM DOS REGISTROS NÃO FOSSEM IMPORTANTES PODERÍAMOS
		 * EXECUTAR A LEITURA DE UMA LISTA "THREAD SAFE EM MULTITHREADING
		 * 
		 * */
		for (CSVRecord csvRecord : inputDataCsvParser) {
			Long currentLineNumber = inputDataCsvParser.getCurrentLineNumber();
			
			//Skipping header line
			if ( currentLineNumber.equals(Long.valueOf(Constants.HEADER_LINE_NUMBER)) ) {
				continue;
			}
			
			boolean validLine = this.validateAndBuildInputdata(csvRecord, accountsToUpdate, inputDataCsvParser.getCurrentLineNumber());
			
			if ( !validLine ) {
				continue;
			}
			
			boolean lastExecution = !inputDataCsvParser.iterator().hasNext();
			if ( accountsToUpdate.size() == maxChunkSize 
					|| lastExecution ) {
				
				processDataService.processAccountRegistersMultiThreading(accountsToUpdate, fullOutputFilePath);
				
				maxChunkSize = currentLineNumber + properties.getMaxChunkSizeProcessPerTime();
				accountsToUpdate.clear();
			
			}
		}
	}

	private boolean validateAndBuildInputdata(
			CSVRecord csvRecord, 
			List<AccountInfoDTO> accountsToUpdate,
			long lineNumber) {
		
		boolean validLine = Boolean.TRUE;
		
		try {
			String agencia 					= dataFormatterUtils.formatAgencyData( csvRecord.get(AccountInfoIndexConfigurationEnum.AGENCY.getIndex()) );
			String conta 					= dataFormatterUtils.formatAccountData( csvRecord.get(AccountInfoIndexConfigurationEnum.ACCOUNT.getIndex()) );
			Double saldo 					= dataFormatterUtils.formatBalanceData( csvRecord.get(AccountInfoIndexConfigurationEnum.BALANCE.getIndex()) );
			AccountStatusEnum statusEnum 	= AccountStatusEnum.getByValue(csvRecord.get(AccountInfoIndexConfigurationEnum.STATUS.getIndex()));
			
			if ( Strings.isNullOrEmpty(agencia) 
					|| Strings.isNullOrEmpty(conta) 
					|| Objects.isNull(statusEnum) ) {
				
				validLine = Boolean.FALSE;
				
				String errorMessage = messageService.getMessage("error.invalid.data.line", String.valueOf(lineNumber));
				this.errors.add(errorMessage);
			}
			
			AccountInfoDTO accountInfoDTO = AccountInfoDTOBuilder.getInstance()
					.withAgencia( agencia )
					.withConta( conta )
					.withSaldo( saldo )
					.withStatus( statusEnum )
					.build();
			
			accountsToUpdate.add(accountInfoDTO);
		} catch (Exception e) {
			validLine = Boolean.FALSE;
			
			String errorMessage = messageService.getMessage("error.while.parsing.input.file.data", String.valueOf(lineNumber));
			LOGGER.error("==========================================================");
			LOGGER.error(errorMessage);
			LOGGER.error("==========================================================");
			
			errors.add(errorMessage);
		}
		
		return validLine;
	}
}
