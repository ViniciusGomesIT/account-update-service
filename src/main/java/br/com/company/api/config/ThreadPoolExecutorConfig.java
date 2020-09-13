package br.com.company.api.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.company.api.properties.AccountUpdateProperties;

@Configuration
public class ThreadPoolExecutorConfig {
	
	@Autowired
	private AccountUpdateProperties properties;
	
	@Bean(name = "taskExecutor")
    public ExecutorService getAsyncExecutor() {
        ExecutorService executor = Executors.newFixedThreadPool(properties.getThreadPoolLength());
       
        return executor;
    }
}
