package br.com.company.api.builders.account;

import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.enums.AccountStatusEnum;

public class AccountInfoDTOBuilder {
	
	private String agencia;
	private String conta;
	private Double saldo;
	private AccountStatusEnum status;

	public static AccountInfoDTOBuilder getInstance() {
		return new AccountInfoDTOBuilder();
	}
	
	public AccountInfoDTOBuilder withAgencia(String agencia) {
		this.agencia = agencia;
		return this;
	}
	
	public AccountInfoDTOBuilder withConta(String conta) {
		this.conta = conta;
		return this;
	}
	
	public AccountInfoDTOBuilder withSaldo(Double saldo) {
		this.saldo = saldo;
		return this;
	}
	
	public AccountInfoDTOBuilder withStatus(AccountStatusEnum status) {
		this.status = status;
		return this;
	}
	
	public AccountInfoDTO build() {
		AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
		
		accountInfoDTO.setAgencia( agencia );
		accountInfoDTO.setConta( conta );
		accountInfoDTO.setSaldo( saldo );
		accountInfoDTO.setStatus( status );
		
		return accountInfoDTO;
	}
}

