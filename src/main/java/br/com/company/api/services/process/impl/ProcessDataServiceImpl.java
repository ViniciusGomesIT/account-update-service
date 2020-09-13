package br.com.company.api.services.process.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import br.com.company.api.config.AccountLock;
import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.properties.AccountUpdateProperties;
import br.com.company.api.services.message.MessageService;
import br.com.company.api.services.process.ProcessDataService;
import br.com.company.api.util.FileUtil;

@Service
public class ProcessDataServiceImpl implements ProcessDataService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDataServiceImpl.class);
	
	private MessageService messageService;
	private ConcurrentHashMap<String, AccountLock> accountsInProcessAccountLocks;
	private FileUtil fileUtil;
	
	@Qualifier(value = "taskExecutor")
	private ExecutorService executor;
	
	@Autowired
	public ProcessDataServiceImpl(
			 MessageService messageService,
			 FileUtil fileUtil,
			 AccountUpdateProperties properties,
			 ExecutorService executor) {
		
		this.messageService = messageService;
		this.fileUtil = fileUtil;
		this.executor = executor;
	}

	@Override
	public Queue<AccountInfoDTO> processAccountRegistersMultiThreading(List<AccountInfoDTO> accountsToUpdate, String fullOutputFilePath) {
		Queue<AccountInfoDTO> accountsProcessed = new ConcurrentLinkedQueue<>();
		
		
	 	//CHAMADA PARA MÉTODO COM CONTROLE DE CONCORRÊNCIA TENTATIVA DE CRIAÇÃO
	 	//DE CONTROLE DE CONCORRÊNCIA PARA NÃO EXECUTAR UM REGISTROO<AGENCIA+CONTA>
	 	//ANTES DO POSTEIOR NA ORDEN DE LEITURA(NÃO PROCESSAR O 5 AO INVÉS DO 1)
		/*
		Queue<Future<AccountInfoDTO>> futures = executeThreadPool(accountsToUpdate);
		accountsProcessed = this.getAccountsProcessedFromFutures(futures);
		*/
		
		//CÓDIGO RODANDO MULTI-THREADING SEM LOCK
		List<Future<AccountInfoDTO>> futures = accountsToUpdate.parallelStream()
				.map(account -> executor.submit(new AccountInfoCallableServiceImpl(account)))
				.collect(Collectors.toList());
				
		for (Future<AccountInfoDTO> future : futures) {
			try {
				
				accountsProcessed.add(future.get());
				
			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error("=============================================");
				LOGGER.error(messageService.getMessage("error.while.trying.to.execute.integration"));
				LOGGER.error("MESSAGE: {}", e.getMessage());
				LOGGER.error("STACK TRACE: {}", Arrays.toString(e.getStackTrace()));
			}
		}
		
		if ( !accountsProcessed.isEmpty() ) {
			fileUtil.writeOutputFile(fullOutputFilePath, accountsProcessed);
		}
		
		return accountsProcessed;
	}
	
	private Queue<AccountInfoDTO> getAccountsProcessedFromFutures(Queue<Future<AccountInfoDTO>> futures) {
		Queue<AccountInfoDTO> accountsProcessed = new ConcurrentLinkedQueue<>();
		
		try {
			for (Future<AccountInfoDTO> accountFuture : futures ) {
				if ( !accountFuture.isDone() ) {
					accountsProcessed.add( accountFuture.get(5, TimeUnit.SECONDS) );
				} else {
					accountsProcessed.add( accountFuture.get() );
				}
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			LOGGER.error("MESSAGE ==> {}", e.getMessage());
			LOGGER.error("STACK-TRACE ==> {}", Arrays.toString(e.getStackTrace()));
		}
		
		return accountsProcessed;
	}
	
	private Queue<Future<AccountInfoDTO>> executeThreadPool(List<AccountInfoDTO> accountsToUpdate) {
		Queue<Future<AccountInfoDTO>> futures = new ConcurrentLinkedQueue<>();
		
		//CÓDIUGO COM CONTROLE DE LOCKS
		executor.submit(() -> {
			do {
				AccountInfoDTO accountInfoDTO = accountsToUpdate.iterator().next();
						
				if ( canProcess(accountInfoDTO) ) {
					
					try {
						Future<AccountInfoDTO> accountInfoFuture = executor.submit(new AccountInfoCallableServiceImpl(accountInfoDTO));
						
						futures.add( accountInfoFuture );
						accountsToUpdate.remove(accountInfoDTO);
						
						releaseLock(accountInfoDTO);
						
					} catch (Exception e) {
						LOGGER.error("=============================================");
						LOGGER.error(messageService.getMessage("error.while.trying.to.execute.integration"));
						LOGGER.error("MESSAGE: {}", e.getMessage());
						LOGGER.error("STACK TRACE: {}", Arrays.toString(e.getStackTrace()));
						
						releaseLock(accountInfoDTO);
					}
				}
				
			} while ( !accountsToUpdate.isEmpty() );
		});
		
		return futures;
	}
	
	private boolean canProcess(AccountInfoDTO accountInfoDTO) {
		
		synchronized (accountsInProcessAccountLocks) {
			
			AccountLock accountLock;
			String accountLockId = String.valueOf(accountInfoDTO.getAgencia().concat(accountInfoDTO.getConta()));
			
			accountLock = accountsInProcessAccountLocks.get(accountLockId);
			
			if ( Objects.isNull(accountLock) ) {
				accountLock = new AccountLock(accountLockId);
				accountLock.lock();
				
				accountsInProcessAccountLocks.put(accountLockId, accountLock);
				
				return true;
			}
			
			if ( !accountLock.isLocked() ) {
				accountLock.lock();
				
				return true;
			}
			
			return false;
		}
	}
	
	private void releaseLock(AccountInfoDTO accountInfoDTO) {
		
		synchronized (accountsInProcessAccountLocks) {
			
			AccountLock accountLock;
			String accountLockId = String.valueOf(accountInfoDTO.getAgencia().concat(accountInfoDTO.getConta()));
			
			accountLock = accountsInProcessAccountLocks.get(accountLockId);
			
			if ( Objects.nonNull(accountLock) ) {
				accountLock.unlock();
			}
		}
	}
}
