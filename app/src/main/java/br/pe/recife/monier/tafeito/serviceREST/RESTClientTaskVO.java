package br.pe.recife.monier.tafeito.serviceREST;

public class RESTClientTaskVO {

    private IRESTClientTask clienteTask;
    private String operacao;

    public RESTClientTaskVO(IRESTClientTask clienteTask, String operacao) {
        this.clienteTask = clienteTask;
        this.operacao = operacao;
    }

    public IRESTClientTask getClienteTask() {
        return clienteTask;
    }

    public void setClienteTask(IRESTClientTask clienteTask) {
        this.clienteTask = clienteTask;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

}
