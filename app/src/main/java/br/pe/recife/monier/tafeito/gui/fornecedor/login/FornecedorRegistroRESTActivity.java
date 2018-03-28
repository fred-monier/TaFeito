package br.pe.recife.monier.tafeito.gui.fornecedor.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.pe.recife.monier.tafeito.R;
import br.pe.recife.monier.tafeito.excecao.NegocioException;
import br.pe.recife.monier.tafeito.negocio.Acesso;
import br.pe.recife.monier.tafeito.clientrest.RESTClientTaskVO;
import br.pe.recife.monier.tafeito.negocio.Autenticacao;
import br.pe.recife.monier.tafeito.negocio.Fornecedor;
import br.pe.recife.monier.tafeito.negocio.Usuario;
import br.pe.recife.monier.tafeito.clientrest.IRESTClient;
import br.pe.recife.monier.tafeito.clientrest.UsuarioLiberarRESTClientTask;
import br.pe.recife.monier.tafeito.clientrest.UsuarioRegistrarRESTClientTask;
import br.pe.recife.monier.tafeito.util.HttpUtil;
import br.pe.recife.monier.tafeito.util.MaskaraCpfCnpj;
import br.pe.recife.monier.tafeito.util.MaskaraType;

public class FornecedorRegistroRESTActivity extends AppCompatActivity implements IRESTClient {

    private static final String OPERACAO_USUARIO_LIBERAR = "UsuarioLiberar";
    private static final String OPERACAO_USUARIO_REGISTRAR = "UsuarioRegistrar";

    EditText _nameText;
    EditText _cnpjText;
    EditText _phoneText;
    EditText _emailText;
    EditText _addressText;
    EditText _passwordText;
    Button _signupButton;
    TextView _loginLink;

    //Task Async
    private UsuarioLiberarRESTClientTask taskEmail;
    private UsuarioRegistrarRESTClientTask taskAcesso;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fornecedor_registro_rest);

        _nameText     = (EditText) findViewById(R.id.input_name);
        _cnpjText     = (EditText) findViewById(R.id.input_cnpj);
        _phoneText    = (EditText) findViewById(R.id.input_phone);
        _emailText    = (EditText) findViewById(R.id.input_email);
        _addressText  = (EditText) findViewById(R.id.input_address);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button)   findViewById(R.id.btn_signup);
        _loginLink    = (TextView) findViewById(R.id.link_login);

        //Mascara
        _cnpjText.addTextChangedListener(MaskaraCpfCnpj.insert(_cnpjText, MaskaraType.CNPJ));

        _signupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Voltar para a tela de Login
                finish();
            }
        });
    }

    public void chamaSucesso(String operacao, Object retorno) {

        progressDialog.dismiss();

        switch (operacao) {

            case OPERACAO_USUARIO_LIBERAR:

                String name = _nameText.getText().toString();
                String cnpj = _cnpjText.getText().toString().replaceAll("\\D", "");
                String phone = _phoneText.getText().toString();
                String email = _emailText.getText().toString();
                String address = _addressText.getText().toString();
                String password = _passwordText.getText().toString();

                this.registerUsuario(name, cnpj, phone, email, address, password);

                break;

            case OPERACAO_USUARIO_REGISTRAR:

                this.onSignupSuccess((Autenticacao) retorno);
                break;

            default: break;
        }

    }

    public void chamaFalha(String operacao, String retorno) {

        switch (operacao) {

            case OPERACAO_USUARIO_LIBERAR:

                this.onSignupFailed(retorno);
                break;

            case OPERACAO_USUARIO_REGISTRAR:

                this.onSignupFailed(retorno);
                break;

            default: break;
        }
    }

    private void checkEmail(String email) {

        try {

            if (HttpUtil.temConexaoWeb(getApplicationContext())) {
                if (taskEmail == null || taskEmail.getStatus() != AsyncTask.Status.RUNNING) {

                    //
                    progressDialog = new ProgressDialog(FornecedorRegistroRESTActivity.this, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage( getApplicationContext().getResources().
                            getText(R.string.login_verificando).toString());
                    progressDialog.show();
                    //

                    RESTClientTaskVO vRESTClientTaskVO = new RESTClientTaskVO(this, OPERACAO_USUARIO_LIBERAR);
                    taskEmail = new UsuarioLiberarRESTClientTask(vRESTClientTaskVO, getApplicationContext(),
                            email);
                    taskEmail.execute();
                }
            } else {

                throw new NegocioException(getApplicationContext().getResources().
                        getText(R.string.login_conexaoweb_inexistente).toString());
            }


        } catch (Exception e)
        {
            onSignupFailed(e.getMessage());
        }

    }

    private void registerUsuario(String name, String cnpj, String phone, String email,
                                 String address, String password) {

        //Cadastrar fornecedor
        try {

            Acesso acesso = new Acesso();
            acesso.setLogin(email);
            acesso.setSenha(password);

            Usuario usuario = new Fornecedor();
            usuario.setHabilitado(true);
            usuario.setNome(name);
            usuario.setEndereco(address);
            usuario.setTelefone(Integer.parseInt(phone));
            usuario.setEmail(email);
            ((Fornecedor) usuario).setCnpj(cnpj);

            if (HttpUtil.temConexaoWeb(getApplicationContext())) {
                if (taskAcesso == null || taskAcesso.getStatus() != AsyncTask.Status.RUNNING) {

                    //
                    progressDialog = new ProgressDialog(FornecedorRegistroRESTActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(getApplicationContext().
                            getText(R.string.registro_criando_conta).toString());
                    progressDialog.show();
                    //

                    RESTClientTaskVO vRESTClientTaskVO = new RESTClientTaskVO(this, OPERACAO_USUARIO_REGISTRAR);
                    taskAcesso = new UsuarioRegistrarRESTClientTask(vRESTClientTaskVO, getApplicationContext(),
                            acesso, usuario);
                    taskAcesso.execute();
                }
            } else {

                throw new NegocioException(getApplicationContext().getResources().
                        getText(R.string.login_conexaoweb_inexistente).toString());
            }


        } catch (Exception e)
        {
            onSignupFailed(e.getMessage());
        }

    }

    private void signup() {

        if (!validate())
        {
            onSignupFailed(null);
            return;
        }

        _signupButton.setEnabled(false);

        String email = _emailText.getText().toString();

        this.checkEmail(email);
    }

    private void onSignupSuccess(Autenticacao autenticacao) {

        _signupButton.setEnabled(true);

        Intent devolve = getIntent();
        devolve.putExtra(FornecedorLoginRESTActivity.AUTENTICACAO, autenticacao);
        setResult(RESULT_OK, devolve);
        finish();
    }

    private void onSignupFailed(String message) {

        progressDialog.dismiss();

        if (message == null)
        {
            message =  getApplicationContext().getResources().
                    getText(R.string.registro_falhou).toString();
        }
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    private boolean validate() {

        boolean valid = true;

        String name     = _nameText.getText().toString();
        String cnpj     = _cnpjText.getText().toString().replaceAll("\\D", "");
        String phone    = _phoneText.getText().toString();
        String email    = _emailText.getText().toString();
        String address  = _addressText.getText().toString();
        String password = _passwordText.getText().toString();

        //nome
        if (name.isEmpty() || name.length() < 3 || name.length() > 200)
        {
            _nameText.setError(getApplicationContext().getResources().getText(R.string.registro_nome_invalido).toString());
            valid = false;
        } else
        {
            _nameText.setError(null);
        }
        //cnpj
        if (cnpj.isEmpty() || cnpj.length() != 14)
        {
            _cnpjText.setError(getApplicationContext().getResources().
                    getText(R.string.registro_cnpj_invalido).toString());
            valid = false;
        } else {
            _cnpjText.setError(null);
        }
        //telefone
        if (phone.isEmpty() || phone.length() < 10 || phone.length() > 11)
        {
            _phoneText.setError(getApplicationContext().getResources().getText(R.string.registro_phone_invalido).toString());
             valid = false;
        } else
        {
            _phoneText.setError(null);
        }
        //e-mail
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ||  email.length() > 100)
        {
            _emailText.setError(getApplicationContext().getResources().
                    getText(R.string.registro_email_invalido).toString());
            valid = false;
        } else {
            _emailText.setError(null);
        }
        //enderreço
        if (address.isEmpty() || address.length() > 200)
        {
            _addressText.setError(getApplicationContext().getResources().
                    getText(R.string.registro_endereco_invalido).toString());
            valid = false;
        } else
        {
            _addressText.setError(null);
        }
        //senha
        if (password.isEmpty() || password.length() < 4 || password.length() > 10)
        {
            _passwordText.setError(getApplicationContext().getResources().getText(R.string.login_senha_tamanho_invalido).toString());
            valid = false;
        } else
        {
            _passwordText.setError(null);
        }
        return valid;
    }

}
