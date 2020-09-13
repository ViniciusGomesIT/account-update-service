package br.com.company.api.service.utils;

import java.util.List;

import com.google.common.collect.Lists;

import br.com.company.api.dto.AccountInfoDTO;
import br.com.company.api.enums.AccountProcessedStatusEnum;
import br.com.company.api.enums.AccountStatusEnum;

public class GenerateDataUtils {
	
	public AccountInfoDTO generateOneAccountInfoDTOWithProcessedStatus() {
		AccountInfoDTO accountInfoDTO = this.generateOneAccountInfoDTO();
		
		accountInfoDTO.setProcessedStatus(AccountProcessedStatusEnum.PROCESSED);
		
		return accountInfoDTO;
	}
	
	public List<AccountInfoDTO> generateListOfAccountInfoDTO() {
		return Lists.newArrayList(this.generateOneAccountInfoDTO());
	}
	
	
	private AccountInfoDTO generateOneAccountInfoDTO() {
		AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
		
		accountInfoDTO.setAgencia("0000");
		accountInfoDTO.setConta("123456");
		accountInfoDTO.setSaldo(10.10);
		accountInfoDTO.setStatus(AccountStatusEnum.I);
		
		return accountInfoDTO;
	}




}
