package br.com.company.api.service.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.company.api.contants.Constants;
import br.com.company.api.properties.AccountUpdateProperties;
import br.com.company.api.services.message.MessageService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageServiceTest {
	
	@MockBean(name = "taskExecutor")
	private ExecutorService executor;
	
	@MockBean
	private MessageSource messageSourceMock;
	
	@MockBean
	private AccountUpdateProperties propertiesMock;
	
	@Autowired
	private MessageService messageService;
	
	@Test
	public void GetMessageWithoutArgsTest() {
		String keyMessage = "error.test.key";
		String stringResultTest = "String Test 123";
		
		Object[] args = new Object[1];
		args[0] = Constants.EMPTY_STRING; 
		
		BDDMockito.when(propertiesMock.getDefaultErrorLanguage()).thenReturn("en-US");
		
		Locale.setDefault(Locale.forLanguageTag(propertiesMock.getDefaultErrorLanguage()));
		
		BDDMockito.when(messageSourceMock.getMessage(keyMessage, args, Locale.getDefault()))
			.thenReturn(stringResultTest);
		
		String responseMessage = messageService.getMessage(keyMessage);
		
		assertThat(responseMessage).isEqualTo(stringResultTest);
	}
	
	@Test
	public void GetMessageWithArgsTest() {
		String keyMessage = "error.test.key.with.arg";
		String arg1 = "xpto";
		String stringResultTest = "String Test ".concat(arg1);
		
		Object[] args = new Object[1];
		args[0] = arg1; 
		
		BDDMockito.when(propertiesMock.getDefaultErrorLanguage()).thenReturn("en-US");
		
		Locale.setDefault(Locale.forLanguageTag(propertiesMock.getDefaultErrorLanguage()));
		
		BDDMockito.when(messageSourceMock.getMessage(keyMessage, args, Locale.getDefault()))
			.thenReturn(stringResultTest);
		
		String responseMessage = messageService.getMessage(keyMessage, arg1);
		
		assertThat(responseMessage).isEqualTo(stringResultTest);
	}
}
