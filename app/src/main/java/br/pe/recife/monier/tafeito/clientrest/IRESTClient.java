package br.pe.recife.monier.tafeito.clientrest;

public interface IRESTClient {

    void chamaSucesso(String operacao, Object retorno);
    void chamaFalha(String operacao, String retorno);
}
