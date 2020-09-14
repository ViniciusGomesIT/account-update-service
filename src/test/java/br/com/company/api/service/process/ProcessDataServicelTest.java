package br.com.company.api.service.process;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.company.api.contants.Constants;
import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.properties.AccountUpdateProperties;
import br.com.company.api.services.message.MessageService;
import br.com.company.api.services.process.AccountInfoCallableService;
import br.com.company.api.services.process.ProcessDataService;
import br.com.company.api.services.process.impl.AccountInfoCallableServiceImpl;
import br.com.company.api.util.FileUtil;
import br.com.company.api.utils.GenerateDataUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessDataServicelTest {
	
	@MockBean
	private AccountUpdateProperties propertiesMock;
	
	@MockBean
	private MessageService messageServiceMock;
	
	@MockBean
	private FileUtil fileUtilMock;
	
	@MockBean(name = "taskExecutor")
	private ExecutorService executorMock;
	
	@Mock
	private AccountInfoCallableService accountInfoCallableServiceMock;
	
	@Mock
	private AccountInfoCallableServiceImpl accountInfoCallableServiceImplMock;
	
	@Mock
	private Future<AccountInfoDTO> accountInfoDTOFutureMock;
	
	@Autowired
	private ProcessDataService processDataServiceMock;
	
	private static GenerateDataUtils generateDataUtils;
	
	@BeforeClass
	public static void setUp() {
		generateDataUtils = new GenerateDataUtils();
	}
	
	@Test
	public void ProcessAccountRegistersMultiThreadingTest() throws InterruptedException, ExecutionException {
		BDDMockito.when(accountInfoCallableServiceMock.sendCentralBankUpdateIntegrationUpdateIntegration(Mockito.any(AccountInfoDTO.class)))
			.thenReturn(generateDataUtils.generateOneAccountInfoDTOWithProcessedStatus());
		
		BDDMockito.when(executorMock.submit(Mockito.any(AccountInfoCallableServiceImpl.class)))
			.thenReturn(accountInfoDTOFutureMock);
	
		BDDMockito.when(accountInfoDTOFutureMock.get())
			.thenReturn(generateDataUtils.generateOneAccountInfoDTOWithProcessedStatus());
		
		BDDMockito.when(propertiesMock.getThreadPoolLength())
			.thenReturn(2); 
		
		BDDMockito.doNothing().when(fileUtilMock).writeFile(Mockito.anyString(), Mockito.anyString());
		
		Queue<AccountInfoDTO> response = processDataServiceMock.processAccountRegistersMultiThreading(generateDataUtils.generateListOfAccountInfoDTO(), Mockito.anyString());
		
		assertThat(response.isEmpty(), equalTo(Boolean.FALSE));
	}
	
	@Test
	public void ProcessAccountRegistersMultiThreadingEmptyAccountsTest() throws InterruptedException, ExecutionException {
		BDDMockito.when(accountInfoCallableServiceMock.sendCentralBankUpdateIntegrationUpdateIntegration(Mockito.any(AccountInfoDTO.class)))
			.thenReturn(generateDataUtils.generateOneAccountInfoDTOWithProcessedStatus());
	
		BDDMockito.when(executorMock.submit(Mockito.any(AccountInfoCallableServiceImpl.class)))
			.thenReturn(accountInfoDTOFutureMock);
	
		BDDMockito.when(accountInfoDTOFutureMock.get())
			.thenReturn(generateDataUtils.generateOneAccountInfoDTOWithProcessedStatus());
		
		BDDMockito.when(propertiesMock.getThreadPoolLength())
			.thenReturn(2); 
		
		BDDMockito.doNothing().when(fileUtilMock).writeFile(Mockito.anyString(), Mockito.anyString());
		
		Queue<AccountInfoDTO> response = processDataServiceMock.processAccountRegistersMultiThreading(Collections.emptyList(), Constants.EMPTY_STRING);
		
		Mockito.verify(executorMock, Mockito.never()).submit(Mockito.any(AccountInfoCallableServiceImpl.class));
		assertThat(response.isEmpty(), equalTo(Boolean.TRUE));
	}
}
	
