package br.com.company.api.services.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import br.com.company.api.contants.Constants;
import br.com.company.api.properties.AccountUpdateProperties;

@Service
public class MessageService {

	private MessageSource messageSource;
	private AccountUpdateProperties properties;
	
	@Autowired
	public MessageService(MessageSource messageSource, AccountUpdateProperties properties) {
		this.messageSource = messageSource;
		this.properties = properties;
	}

	public String getMessage(String key)  {
		return getMessage(key, Constants.EMPTY_STRING);
	}
	
	public String getMessage(String key, String... args) {
		Locale.setDefault(Locale.forLanguageTag(properties.getDefaultErrorLanguage()));
		return messageSource.getMessage(key, args, Locale.getDefault());
	}
}
