package br.com.company.api.util;

import java.util.Queue;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import br.com.company.api.contants.Constants;
import br.com.company.api.properties.AccountUpdateProperties;

@Component
public class DataFormatterUtils {
	
	@Autowired
	AccountUpdateProperties properties;
	
	public String formatAgencyData(String agency) {
		
		if ( !Strings.isNullOrEmpty(agency) ) {
			
			if ( agency.length() == properties.getDefaultAgencyLength() ) {
				return agency;
			} else {
				return StringUtils.leftPad( agency, (properties.getDefaultAgencyLength() - agency.length()), properties.getDefaultLeftPadValue() );
			}
		}
		
		return null;
	}
	
	public String formatAccountData(String account) {
		
		if ( !Strings.isNullOrEmpty(account) ) {
			account = account.replace(Constants.DEFAULT_AGENCY_DIGIT_SEPARATOR, Constants.EMPTY_STRING);
			
			if ( account.length() == properties.getDefaultAccountLength() ) {
				return account;
			} else {
				return StringUtils.leftPad( account, (properties.getDefaultAccountLength() - account.length()), properties.getDefaultLeftPadValue() );
			}
		} 

		return null;
	}

	public Double formatBalanceData(String balance) {
		
		if ( !Strings.isNullOrEmpty(properties.getDefaultLeftPadValue()) ) {
			balance = balance.replace(Constants.FROM_FILE_BALANCE_SEPARATOR, Constants.DEFAULT_BALANCE_SEPARATOR);
			
			return new Double(balance);
		} 
		
		return null;
	}
	
	public String formatErrors(Queue<String> errors) {
		StringBuilder builder = new StringBuilder();
		
		errors.forEach( error -> builder.append(error).append(Constants.NEW_LINE_FUNCTION) );
		
		return builder.toString();
	}
}
