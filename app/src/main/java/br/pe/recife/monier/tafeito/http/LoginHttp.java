package br.pe.recife.monier.tafeito.http;

public class LoginHttp {

    private String login;

    public LoginHttp() {
    }

    public LoginHttp(String login) {
        super();
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
