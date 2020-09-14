package br.com.company.api.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.company.api.contants.Constants;
import br.com.company.api.properties.AccountUpdateProperties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataFormatterUtilsTest {
	
	private static final int agencyLength = 4;
	private static final int accountLength = 6;
	private static final char leftPadValue = '0';
	
	@MockBean(name = "taskExecutor")
	private ExecutorService executor;
	
	@MockBean
	private AccountUpdateProperties propertiesMock;
	
	@Autowired
	private DataFormatterUtils dataFormatterUtils;

	@Test
	public void FormatAgencyDataWithCorrectLengthTest() {
		String originalValue = "1234";
		String expextedValue = "1234";
		
		Mockito.when(propertiesMock.getDefaultAgencyLength())
			.thenReturn(agencyLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAgencyData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatAgencyDataWithoutCorrectLengthTest() {
		String originalValue = "12";
		String expextedValue = StringUtils.leftPad( originalValue, agencyLength, leftPadValue );
		
		Mockito.when(propertiesMock.getDefaultAgencyLength())
			.thenReturn(agencyLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAgencyData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatAgencyDataWithNullValueTest() {
		String originalValue = null;
		String expextedValue = null;
		
		Mockito.when(propertiesMock.getDefaultAgencyLength())
			.thenReturn(agencyLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAgencyData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatAgencyDataWithEmptyValueTest() {
		String originalValue = Constants.EMPTY_STRING;
		String expextedValue = null;
		
		Mockito.when(propertiesMock.getDefaultAgencyLength())
			.thenReturn(agencyLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAgencyData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatAccountDataWithCorrectLengthTest() {
		String originalValue = "123456";
		String expextedValue = "123456";
		
		Mockito.when(propertiesMock.getDefaultAccountLength())
			.thenReturn(accountLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAccountData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatAccountDataWithoutCorrectLengthTest() {
		String originalValue = "12";
		String expextedValue = StringUtils.leftPad( originalValue, accountLength, leftPadValue );
		
		Mockito.when(propertiesMock.getDefaultAccountLength())
			.thenReturn(accountLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAccountData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatAccountDataWithNullValueTest() {
		String originalValue = null;
		String expextedValue = null;
		
		Mockito.when(propertiesMock.getDefaultAccountLength())
			.thenReturn(accountLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAccountData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatAccountDataWithEmptyValueTest() {
		String originalValue = Constants.EMPTY_STRING;
		String expextedValue = null;
		
		Mockito.when(propertiesMock.getDefaultAccountLength())
			.thenReturn(accountLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAccountData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatAccountDataWithDigitSeparatorCharacterAndNormalLengthTest() {
		String originalValue = "12345-6";
		String originalValueReplaced = originalValue.replace("-", "");
		String expextedValue = StringUtils.leftPad( originalValueReplaced, accountLength, leftPadValue );
		
		Mockito.when(propertiesMock.getDefaultAccountLength())
			.thenReturn(accountLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAccountData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatAccountDataWithDigitSeparatorCharacterAndNoNormalLengthTest() {
		String originalValue = "1245-6";
		String originalValueReplaced = originalValue.replace("-", "");
		String expextedValue = StringUtils.leftPad( originalValueReplaced, accountLength, leftPadValue );;
		
		Mockito.when(propertiesMock.getDefaultAccountLength())
			.thenReturn(accountLength);
		
		Mockito.when(propertiesMock.getDefaultLeftPadValue())
			.thenReturn(leftPadValue);
		
		String stringFormated = dataFormatterUtils.formatAccountData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
	
	@Test
	public void FormatBalanceDataNormalTest() {
		String originalValue = "0,0";
		Double expextedValue = Double.valueOf(0.0);
		
		Double stringFormated = dataFormatterUtils.formatBalanceData(originalValue);
		
		assertThat(stringFormated).isEqualTo(expextedValue);
	}
}
