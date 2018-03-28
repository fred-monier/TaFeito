package br.pe.recife.monier.tafeito.http;

public class AcessoHttp {

    private String login;
    private String senha;
    private String tipoUsuario;

    public AcessoHttp() {
    }

    public AcessoHttp(String login, String senha, String tipoUsuario) {
        super();
        this.login = login;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

}
