package br.com.company.api.enums;

public enum OutputFileHeadersEnum {

	AGENCIA("agencia", 1), 
	CONTA("conta", 2), 
	SALDO("saldo", 3), 
	STATUS("status", 4),
	PROCESSED_STATUS("processed_status", 5), 
	ERROR("processed_error", 6);

	private String headerName;
	private int headerOrder;

	private OutputFileHeadersEnum(String headerName, int headerOrder) {
		this.headerName = headerName;
		this.headerOrder = headerOrder;
	}

	public String getHeaderName() {
		return headerName;
	}

	public int getHeaderOrder() {
		return headerOrder;
	}
}
