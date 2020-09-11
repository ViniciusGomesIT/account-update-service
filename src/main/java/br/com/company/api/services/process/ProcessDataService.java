package br.com.company.api.services.process;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.company.api.config.AccountLock;
import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.properties.AccountUpdateProperties;

@Service
public class ProcessDataService {
	
	private AccountUpdateProperties properties;
	private ConcurrentHashMap<String, AccountLock> accountsInProcessAccountLocks;
	
	//TODO REMOVER ESTE CÓDIGO
	private Integer contador = 0;
	
	@Autowired
	public ProcessDataService(AccountUpdateProperties properties) {
		this.properties = properties;
		this.accountsInProcessAccountLocks = new ConcurrentHashMap<>();
	}

	public Queue<AccountInfoDTO> processAccountRegistersMultiThreading(List<AccountInfoDTO> accountsToUpdate) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(properties.getThreadPoolLength());
		ConcurrentLinkedQueue<AccountInfoDTO> accountsProcessed = new ConcurrentLinkedQueue<>();
		
		//TODO ADICIONAR LOCK E UNLOCK DO RESOURCE
		List<Future<AccountInfoDTO>> list =	accountsToUpdate.parallelStream()
			.map(account -> executor.submit(new AccountInfoCallableService(account, ++contador)))
			.collect(Collectors.toList());
			
		for (Future<AccountInfoDTO> future : list) {
			accountsProcessed.add(future.get());
		}
	
		return accountsProcessed;
	}
	
	private AccountLock checkAndAddLockToAccount(AccountInfoDTO accountInfoDTO) {
		AccountLock accountLock;
		String accountLockId = String.valueOf(accountInfoDTO.getAgencia().concat(accountInfoDTO.getConta()));
		
		accountLock = accountsInProcessAccountLocks.get(accountLockId);
		
		if ( Objects.isNull(accountLock) ) {
			accountLock = new AccountLock(accountLockId);
			accountLock.lock();
			
			accountsInProcessAccountLocks.put(accountLockId, accountLock);
		}
		
		return accountLock;
	}
}
