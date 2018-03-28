package br.pe.recife.monier.tafeito.http;

public class UsuarioHttp {

    public static final String FORNECEDOR = "F";
    public static final String CLIENTE = "C";

    private String login;
    private String senha;
    private String habilitado;
    private String nome;
    private String endereco;
    private String email;
    private int telefone;
    private String tipoUsuario;
    private String documento;

    public UsuarioHttp() {
    }

    public UsuarioHttp(String login, String senha, String habilitado, String nome, String endereco,
                       String email, int telefone, String tipoUsuario, String documento) {
        super();
        this.login = login;
        this.senha = senha;
        this.habilitado = habilitado;
        this.nome = nome;
        this.endereco = endereco;
        this.email = email;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
        this.documento = documento;
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
    public String getHabilitado() {
        return habilitado;
    }
    public void setHabilitado(String habilitado) {
        this.habilitado = habilitado;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getTelefone() {
        return telefone;
    }
    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }
    public String getTipoUsuario() {
        return tipoUsuario;
    }
    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
    public String getDocumento() {
        return documento;
    }
    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
