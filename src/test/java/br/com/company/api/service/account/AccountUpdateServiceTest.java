package br.com.company.api.service.account;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.company.api.properties.AccountUpdateProperties;
import br.com.company.api.services.account.AccountUpdateService;
import br.com.company.api.services.account.impl.AccountUpdateServiceImpl;
import br.com.company.api.services.message.MessageService;
import br.com.company.api.services.process.impl.ProcessDataServiceImpl;
import br.com.company.api.util.DataFormatterUtils;
import br.com.company.api.util.FileUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountUpdateServiceTest {
	
	@MockBean
	private DataFormatterUtils dataFormatterUtilsMock;
	
	@MockBean
	private ProcessDataServiceImpl processDataServiceMock;
	
	@MockBean
	private FileUtil fileUtilMock;
	
	@MockBean
	private MessageService messageServiceMock;
	
	@MockBean
	private AccountUpdateProperties propertiesMock;
	
	@MockBean(name = "taskExecutor")
	private ExecutorService executor;
	
	@MockBean
	private AccountUpdateServiceImpl accountUpdateServiceImplMock;
	
	@Autowired
	private AccountUpdateService accountUpdateServiceMock;

	@Test
	public void ReadAndProcessFileTest() throws FileNotFoundException, IOException, URISyntaxException {
		String inputFilePath = "/test.csv";
		String outputFilePath = "/test_out.csv";
		
		InputStream stream = this.getClass().getResourceAsStream(inputFilePath);
		CSVParser csvParser = CSVFormat.EXCEL.withDelimiter(';').parse(new InputStreamReader(stream));
		
		BDDMockito.when(fileUtilMock.getInputCsvFile(Mockito.anyString(),  Mockito.any(FileInputStream.class)))
			.thenReturn(csvParser);
		
		BDDMockito.when(fileUtilMock.createOutPutFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
			.thenReturn(outputFilePath);
		
		accountUpdateServiceMock.readAndProcessFile(inputFilePath);
		
		Mockito.verify(accountUpdateServiceImplMock, Mockito.atLeastOnce()).readAndProcessFile(inputFilePath);
	}
	
	@Test
	public void ReadAndProcessFileWithNullCsvParserTest() throws FileNotFoundException, IOException, URISyntaxException {
		String inputFilePath = "/test.csv";
		String outputFilePath = "/test_out.csv";
		
		BDDMockito.when(fileUtilMock.getInputCsvFile(Mockito.anyString(),  Mockito.any(FileInputStream.class)))
			.thenReturn(null);
		
		BDDMockito.when(fileUtilMock.createOutPutFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
			.thenReturn(outputFilePath);
		
		accountUpdateServiceMock.readAndProcessFile(inputFilePath);
		
		Mockito.verify(accountUpdateServiceImplMock, Mockito.atLeastOnce()).readAndProcessFile(inputFilePath);
	}
	
	@Test
	public void ReadAndProcessFileWithNullInputFileTest() throws FileNotFoundException, IOException, URISyntaxException {
		String outputFilePath = "/test_out.csv";
		
		BDDMockito.when(fileUtilMock.getInputCsvFile(Mockito.anyString(),  Mockito.any(FileInputStream.class)))
			.thenReturn(null);
		
		BDDMockito.when(fileUtilMock.createOutPutFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
			.thenReturn(outputFilePath);
		
		accountUpdateServiceMock.readAndProcessFile(null);
		
		Mockito.verify(accountUpdateServiceImplMock, Mockito.atLeastOnce()).readAndProcessFile(null);
	}
	
	@Test
	public void ReadAndProcessFileWithNullOutputFileNameTest() throws FileNotFoundException, IOException, URISyntaxException {
		String inputFilePath = "/test.csv";
		
		InputStream stream = this.getClass().getResourceAsStream("/test.csv");
		CSVParser csvParser = CSVFormat.EXCEL.withDelimiter(';').parse(new InputStreamReader(stream));
		
		BDDMockito.when(fileUtilMock.getInputCsvFile(Mockito.anyString(),  Mockito.any(FileInputStream.class)))
			.thenReturn(csvParser);
		
		BDDMockito.when(fileUtilMock.createOutPutFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
			.thenReturn(null);
		
		accountUpdateServiceMock.readAndProcessFile(inputFilePath);
		
		Mockito.verify(accountUpdateServiceImplMock, Mockito.atLeastOnce()).readAndProcessFile(inputFilePath);
	}
}
