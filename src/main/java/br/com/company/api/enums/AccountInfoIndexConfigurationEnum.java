package br.com.company.api.enums;

public enum AccountInfoIndexConfigurationEnum {

	AGENCY("agencia", 0),
	ACCOUNT("conta", 1),
	BALANCE("saldo", 2),
	STATUS("status", 3);
	
	private String headerName;
	private int index;

	private AccountInfoIndexConfigurationEnum(String headerName, int index) {
		this.headerName = headerName;
		this.index = index;
	}

	public String getHeaderName() {
		return headerName;
	}


	public int getIndex() {
		return index;
	}
}
