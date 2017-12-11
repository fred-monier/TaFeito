package br.pe.recife.monier.tafeito.serviceREST;

public interface IRESTClientTask {

    void chamaSucesso(String operacao, Object retorno);
    void chamaFalha(String operacao, String retorno);
}
