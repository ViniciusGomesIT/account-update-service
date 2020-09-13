package br.com.company.api.services.process.impl;

import java.util.concurrent.Callable;

import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.enums.AccountProcessedStatusEnum;
import br.com.company.api.services.process.AccountInfoCallableService;
import br.com.company.api.services.receita.ReceitaService;

public class AccountInfoCallableServiceImpl implements Callable<AccountInfoDTO>, AccountInfoCallableService {
	
	private ReceitaService receitaService;
	private AccountInfoDTO accountInfoDTO;
	
	public AccountInfoCallableServiceImpl(AccountInfoDTO accountInfoDTO) {
		this.accountInfoDTO = accountInfoDTO;
		this.receitaService = new ReceitaService();
	}
	
	
	@Override
	public AccountInfoDTO call() throws Exception {
		return sendCentralBankUpdateIntegrationUpdateIntegration(accountInfoDTO);
	}
	
	@Override
	public AccountInfoDTO sendCentralBankUpdateIntegrationUpdateIntegration(AccountInfoDTO accountInfoDTO) {
		try {
			boolean isExecuted = receitaService.atualizarConta(
					accountInfoDTO.getAgencia(), 
					accountInfoDTO.getConta(), 
					accountInfoDTO.getSaldo(), 
					accountInfoDTO.getStatus().toString());
			
			
			checkIntegrationExecutionStatus(accountInfoDTO, isExecuted);
		} catch (Exception e) {
			accountInfoDTO.setProcessedStatus( AccountProcessedStatusEnum.FAIL_IN_PROCESS );
			accountInfoDTO.setProcessError( e.getMessage() );
		}
		
		return accountInfoDTO;
	}

	private void checkIntegrationExecutionStatus(AccountInfoDTO accountInfoDTO, boolean isExecuted) {
		if ( isExecuted ) {
			accountInfoDTO.setProcessedStatus( AccountProcessedStatusEnum.PROCESSED );
		} else {
			accountInfoDTO.setProcessedStatus( AccountProcessedStatusEnum.NOT_PROCESSED );
		}
	}
}
