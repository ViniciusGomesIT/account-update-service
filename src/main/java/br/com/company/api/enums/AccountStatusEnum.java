package br.com.company.api.enums;

import java.util.Arrays;

public enum AccountStatusEnum {
	
	A,
	I,
	B,
	P;
	
	public static AccountStatusEnum getByValue(String value) {
		return Arrays.stream(AccountStatusEnum.values())
			.filter(object -> object.toString().equals(value))
			.findFirst()
			.orElse(null);
	}
}
