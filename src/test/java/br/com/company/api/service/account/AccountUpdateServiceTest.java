package br.com.company.api.service.account;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.checkerframework.common.reflection.qual.NewInstance;
import org.junit.Before;
import org.junit.Ignore;
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
	
	@Autowired
	private AccountUpdateService accountUpdateServiceMock;

	@Test
	@Ignore
	public void ReadAndProcessFileTest() throws FileNotFoundException, IOException, URISyntaxException {
		String inputFilePath = "/test.csv";
		String outputFilePath = "/test_out.csv";
		
		File file = new File(inputFilePath);
		
		BDDMockito.when(fileUtilMock.getInputCsvFile(Mockito.anyString()))
			.thenReturn(CSVFormat.EXCEL.withDelimiter(';').parse(new InputStreamReader(new FileInputStream(file))));
		
		BDDMockito.when(fileUtilMock.createOutPutFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
			.thenReturn(outputFilePath);
		
		accountUpdateServiceMock.readAndProcessFile(inputFilePath);
		
		Mockito.verify(accountUpdateServiceMock, Mockito.atLeastOnce()).readAndProcessFile(inputFilePath);
	}
}
