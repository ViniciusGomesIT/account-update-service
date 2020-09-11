package br.com.company.api.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountUpdateProperties {
	
	@Value("${values.default.agency-length}")
	private int defaultAgencyLength;
	
	@Value("${values.default.account-length}")
	private int defaultAccountLength;
	
	@Value("${configuration.pool-thread}")
	private int threadPoolLength;
	
	@Value("${configuration.chunk-size}")
	private long maxChunkSizeProcessPerTime;
	
	@Value("${values.default.left-pad-value}")
	private String defaultLeftPadValue;
	
	@Value("${configuration.default-error-language}")
	private String defaultErrorLanguage;
	
	@Value("${configuration.output-file-name}")
	private String outputFileName;
	
	@Value("${configuration.output-file-extension}")
	private String outputFileExtension;

	public int getDefaultAgencyLength() {
		return defaultAgencyLength;
	}

	public int getDefaultAccountLength() {
		return defaultAccountLength;
	}

	public int getThreadPoolLength() {
		return threadPoolLength;
	}

	public long getMaxChunkSizeProcessPerTime() {
		return maxChunkSizeProcessPerTime;
	}

	public String getDefaultLeftPadValue() {
		return defaultLeftPadValue;
	}

	public String getDefaultErrorLanguage() {
		return defaultErrorLanguage;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public String getOutputFileExtension() {
		return outputFileExtension;
	}
	
}
