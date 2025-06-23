package com.bcopstein.sistvendas.dominio.servicos.impostos;

public class ImpostoStrategyFactory {

    private ImpostoStrategyFactory() {
    }

    public static CalculadorImpostoEstadualStrategy getStrategy(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado não pode ser nulo ou vazio.");
        }

        String estadoUpper = estado.trim().toUpperCase();

        switch (estadoUpper) {
            case "RS":
                return new CalculadorImpostoRS();
            case "SP":
                return new CalculadorImpostoSP();
            case "PE":
                return new CalculadorImpostoPE();
            default:
                throw new IllegalArgumentException(
                        "Estado '" + estado + "' não possui uma estratégia de imposto definida.");
        }
    }
}