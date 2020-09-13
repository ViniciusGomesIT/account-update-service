package br.com.company.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import br.com.company.api.services.account.impl.AccountUpdateServiceImpl;

@SpringBootApplication
public class AccountUpdateServiceApplication {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountUpdateServiceApplication.class);
	
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(AccountUpdateServiceApplication.class);
		ApplicationContext applicationContext = application.run(args);
		
		AccountUpdateServiceImpl accountUpdateService = applicationContext.getBean(AccountUpdateServiceImpl.class);
		
		accountUpdateService.readAndProcessFile(args[0]);
		
		LOGGER.info("====================");
		LOGGER.info("PROCESS DONE... SHUTTING DOWN");
		LOGGER.info("====================");
		
		SpringApplication.exit(applicationContext);
		System.exit(0);
	}
	
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:/i18n/errors");
		messageSource.setDefaultEncoding("UTF-8");
		
		return messageSource;
	}
}
