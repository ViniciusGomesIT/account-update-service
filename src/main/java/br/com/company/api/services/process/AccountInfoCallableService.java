package br.com.company.api.services.process;

import java.util.concurrent.Callable;

import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.enums.AccountProcessedStatusEnum;
import br.com.company.api.services.receita.ReceitaService;

public class AccountInfoCallableService implements Callable<AccountInfoDTO> {
	
	private ReceitaService receitaService;
	private AccountInfoDTO accountInfoDTO;
	private int contador;
	
	public AccountInfoCallableService(AccountInfoDTO accountInfoDTO, int contador) {
		this.contador = contador;
		this.accountInfoDTO = accountInfoDTO;
		this.receitaService = new ReceitaService();
	}
	
	@Override
	public AccountInfoDTO call() throws Exception {
		//TODO REMOVER ESTE CÓDIGO
		System.out.println("CONTADOR: " + contador);
		return sendCentralBankUpdateIntegrationUpdateIntegration(accountInfoDTO);
	}
	
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
