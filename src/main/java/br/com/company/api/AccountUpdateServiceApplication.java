package br.com.company.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import br.com.company.api.services.account.AccountUpdateService;

@SpringBootApplication
public class AccountUpdateServiceApplication {
	
	public static void main(String[] args) {
		//TODO REMOVER ESTE CÒDIGO
		//TODO FAZER O SHUTDOWN DA APLICAÇÂO APÒS A EXECUÇÂO
		//SpringApplication.run(AccountUpdateServiceApplication.class, args);

		SpringApplication application = new SpringApplication(AccountUpdateServiceApplication.class);
		ApplicationContext applicationContext = application.run(args);
		
		AccountUpdateService accountUpdateService = applicationContext.getBean(AccountUpdateService.class);
		
		//TODO REVER A PASSAGEM DE PARÂMETRO
		accountUpdateService.readAndProcessFile(args[0]);
	}
	
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:/i18n/errors");
		messageSource.setDefaultEncoding("UTF-8");
		
		return messageSource;
	}
}
