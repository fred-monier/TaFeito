package br.pe.recife.monier.tafeito.servicerest;

public interface IRESTClientTask {

    void chamaSucesso(String operacao, Object retorno);
    void chamaFalha(String operacao, String retorno);
}
