package br.com.company.api.services.process;

import java.util.List;
import java.util.Queue;

import br.com.company.api.dto.AccountInfoDTO;

public interface ProcessDataService {

	Queue<AccountInfoDTO> processAccountRegistersMultiThreading(List<AccountInfoDTO> accountsToUpdate, String fullOutputFilePath);
}
