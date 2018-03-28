package br.pe.recife.monier.tafeito.clientrest;

public class RESTClientTaskVO {

    private IRESTClient clienteTask;
    private String operacao;

    public RESTClientTaskVO(IRESTClient clienteTask, String operacao) {
        this.clienteTask = clienteTask;
        this.operacao = operacao;
    }

    public IRESTClient getClienteTask() {
        return clienteTask;
    }

    public void setClienteTask(IRESTClient clienteTask) {
        this.clienteTask = clienteTask;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

}
