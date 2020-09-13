package br.com.company.api.service.process;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.properties.AccountUpdateProperties;
import br.com.company.api.service.utils.GenerateDataUtils;
import br.com.company.api.services.message.MessageService;
import br.com.company.api.services.process.AccountInfoCallableService;
import br.com.company.api.services.process.ProcessDataService;
import br.com.company.api.services.process.impl.AccountInfoCallableServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessDataServicelTest {
	
	@MockBean
	private AccountUpdateProperties propertiesMock;
	
	@MockBean
	private MessageService messageServiceMock;
	
	@Mock
	private AccountInfoCallableService accountInfoCallableServiceMock;
	
	@Mock
	private AccountInfoCallableServiceImpl accountInfoCallableServiceImplMock;
	
	@Mock
	private Future<AccountInfoDTO> accountInfoDTOFutureMock;
	
	@Spy
	private final ExecutorService executor = Executors.newFixedThreadPool(2);
	
	@Autowired
	ProcessDataService processDataServiceMock;
	
	private static GenerateDataUtils generateDataUtils;
	
	@BeforeClass
	public static void setUp() {
		generateDataUtils = new GenerateDataUtils();
	}
	
	@Test
	public void ProcessAccountRegistersMultiThreadingTest() {
		String filePath = "test.csv";
		
		BDDMockito.when(accountInfoCallableServiceMock.sendCentralBankUpdateIntegrationUpdateIntegration(Mockito.any(AccountInfoDTO.class)))
			.thenReturn(generateDataUtils.generateOneAccountInfoDTOWithProcessedStatus());
		
		ExecutorService executorService = Mockito.mock(ExecutorService.class);
	
		BDDMockito.when(propertiesMock.getThreadPoolLength())
			.thenReturn(2); 
		
		BDDMockito.when(executorService.submit(Mockito.any(AccountInfoCallableServiceImpl.class)))
			.thenReturn(accountInfoDTOFutureMock);
		
		Queue<AccountInfoDTO> response = processDataServiceMock.processAccountRegistersMultiThreading(generateDataUtils.generateListOfAccountInfoDTO(), filePath);
		
		assertThat(response.isEmpty(), equalTo(Boolean.FALSE));
	}
	
	@Test
	public void GetAccountsProcessedFromFuturesTest() {
		
	}
	
}
	
