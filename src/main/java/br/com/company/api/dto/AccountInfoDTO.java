package br.com.company.api.dto;

import br.com.company.api.enums.AccountProcessedStatusEnum;
import br.com.company.api.enums.AccountStatusEnum;

public class AccountInfoDTO {

	private String agencia;
	private String conta;
	private Double saldo;
	private AccountStatusEnum status;
	
	private AccountProcessedStatusEnum processedStatus;
	private String processError;

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public AccountStatusEnum getStatus() {
		return status;
	}

	public void setStatus(AccountStatusEnum status) {
		this.status = status;
	}

	public AccountProcessedStatusEnum getProcessedStatus() {
		return processedStatus;
	}

	public void setProcessedStatus(AccountProcessedStatusEnum processedStatus) {
		this.processedStatus = processedStatus;
	}

	public String getProcessError() {
		return processError;
	}

	public void setProcessError(String processError) {
		this.processError = processError;
	}
}
