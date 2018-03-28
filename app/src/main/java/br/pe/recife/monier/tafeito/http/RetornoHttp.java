package br.pe.recife.monier.tafeito.http;

public class RetornoHttp {

    public static final String SUCESSO = "SUCESSO";
    public static final String FALHA = "FALHA";

    private String resultado;
    private String descricao;

    public RetornoHttp() {

    }

    public RetornoHttp(String resultado, String descricao) {
        super();
        this.resultado = resultado;
        this.descricao = descricao;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
