package br.com.company.api.services.receita;

import java.util.List;

import com.google.common.collect.Lists;

public class ReceitaService {

    public boolean atualizarConta(String agencia, String conta, double saldo, String status)
            throws InterruptedException {
		
        // Formato agencia: 0000
        if (agencia == null || agencia.length() != 4) {
            return false;
        }
        
        // Formato conta: 000000
        if (conta == null || conta.length() != 6) {
            return false;
        }
        
        // Tipos de status validos:
        List<String> tipos = Lists.newArrayList("A", "I", "B", "P");
                
        if (status == null || !tipos.contains(status)) {
            return false;
        }
        
        // Simula tempo de resposta do serviço (entre 1 e 5 segundos)
        long wait = Math.round(Math.random() * 4000) + 1000;
        Thread.sleep(wait);

        // Simula cenario de erro no serviço (0,1% de erro)
        long randomError = Math.round(Math.random() * 1000);
        if (randomError == 500) {
            throw new RuntimeException("Error");
        }
        
        return true;
    }
}
