package br.pe.recife.monier.tafeito.gui.cliente.login;

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
import br.pe.recife.monier.tafeito.serviceREST.RESTClientTaskVO;
import br.pe.recife.monier.tafeito.negocio.Autenticacao;
import br.pe.recife.monier.tafeito.serviceREST.BuscarPorLoginPorSenhaRESTClientTask;
import br.pe.recife.monier.tafeito.serviceREST.IRESTClientTask;
import br.pe.recife.monier.tafeito.util.HttpUtil;

public class ClienteLoginRESTActivity extends AppCompatActivity implements IRESTClientTask {

    public static final String AUTENTICACAO = "AUTENTICACAO";

    private static final String OPERACAO_BUSCAR_POR_LOGIN_SENHA_CLIENTE = "BuscarPorLoginSenhaCLIENTE";
    private static final int REQUEST_SIGNUP = 0;

    //@InjectView(R.id.input_email)
    EditText _emailText;
    //@InjectView(R.id.input_password)
    EditText _passwordText;
    //@InjectView(R.id.btn_login)
    Button _loginButton;
    //@InjectView(R.id.link_signup)
    TextView _signupLink;

    //Task Async
    private BuscarPorLoginPorSenhaRESTClientTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_login_rest);

        //ButterKnife.inject(this);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        ///////

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Chama a tela de registro de cliente
                Intent intent = new Intent(getApplicationContext(), ClienteRegistroRESTActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                Autenticacao autenticacao = (Autenticacao) data.getSerializableExtra(AUTENTICACAO);
                goOn(autenticacao);
            }
        }
    }

    @Override
    public void onBackPressed() {

        //Impede o comando de voltar para uma activity anterior
        moveTaskToBack(true);
    }

    public void chamaSucesso(String operacao, Object retorno) {

        switch (operacao) {

            case OPERACAO_BUSCAR_POR_LOGIN_SENHA_CLIENTE:

                this.onLoginSuccess((Autenticacao) retorno);
                break;

            default: break;
        }

    }

    public void chamaFalha(String operacao, String retorno) {

        switch (operacao) {

            case OPERACAO_BUSCAR_POR_LOGIN_SENHA_CLIENTE:

                this.onLoginFailed(retorno);
                break;

            default: break;
        }
    }

    private void login() {

        if (!validate()) {
            onLoginFailed(null);
            return;
        }

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //Autenticar Cliente

        try {

            if (HttpUtil.temConexaoWeb(getApplicationContext())) {
                if (task == null || task.getStatus() != AsyncTask.Status.RUNNING) {

                    RESTClientTaskVO vRESTClientTaskVO = new RESTClientTaskVO(this, OPERACAO_BUSCAR_POR_LOGIN_SENHA_CLIENTE);
                    task = new BuscarPorLoginPorSenhaRESTClientTask(vRESTClientTaskVO, getApplicationContext(),
                            email, password, false);
                    task.execute();
                }
            } else {

                throw new NegocioException(getApplicationContext().getResources().
                        getText(R.string.login_conexaoweb_inexistente).toString());
            }

        } catch (Exception e) {

            onLoginFailed(e.getMessage());
        }

        //new android.os.Handler().postDelayed(
        //        new Runnable() {
        //           public void run() {
        //                // Escolher se o login foi bem sucedido ou não para chamar o método adequado abaixo
        //                onLoginSuccess();
        //                // onLoginFailed();
        //                progressDialog.dismiss();
        //            }
        //        }, 3000);

    }

    private boolean validate() {

        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getApplicationContext().getResources().
                    getText(R.string.login_email_invalido).toString());
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError(getApplicationContext().getResources().
                    getText(R.string.login_senha_tamanho_invalido).toString());
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void onLoginSuccess(Autenticacao autenticacao) {
        _loginButton.setEnabled(true);
        goOn(autenticacao);
    }

    private void onLoginFailed(String message) {

        if (message == null) {
            message =  getApplicationContext().getResources().
                    getText(R.string.login_falhou).toString();
        }
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    private void goOn(Autenticacao autenticacao) {

        //Chama a tela principal do cliente
        Intent intent = new Intent(getApplicationContext(), ClientePrincipalActivity.class);
        intent.putExtra(AUTENTICACAO, autenticacao);
        startActivity(intent);
    }

//    private class BuscarPorLoginPorSenhaClientRESTClientTask extends AsyncTask<String, Void, Autenticacao> {
//
//        private final String CAMINHO =
//                //"http://192.168.1.107:8080/TaFeito_Servidor/rest/AcessoService/acessosLoginSenhaCliente";
//                "http://187.87.134.250:8080/TaFeito_Servidor/rest/AcessoService/acessosLoginSenhaCliente";
//
//        private String login;
//        private String senha;
//
//        private ProgressDialog progressDialog;
//
//        public BuscarPorLoginPorSenhaClientRESTClientTask(String login, String senha) {
//            this.login = login;
//            this.senha = senha;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressDialog = new ProgressDialog(ClienteLoginRESTActivity.this,
//                    R.style.AppTheme_Dark_Dialog);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setMessage(getApplicationContext().getResources().
//                    getText(R.string.login_autenticando).toString());
//            progressDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(Autenticacao aut) {
//            super.onPostExecute(aut);
//
//            progressDialog.dismiss();
//
//            if (aut != null){
//                onLoginSuccess(aut);
//            }
//            else{
//                onLoginFailed(null);
//            }
//        }
//
//        @Override
//        protected Autenticacao doInBackground(String... params) {
//
//            Autenticacao res = null;
//
//            HttpURLConnection conexao = null;
//
//            try {
//
//                String caminhoPar = CAMINHO + "/" + login + "/" + senha;
//
//                conexao = HttpUtil.conectarGET(caminhoPar);
//                conexao.connect();
//                //
//
//                int resposta = conexao.getResponseCode();
//                if (resposta == HttpURLConnection.HTTP_OK) {
//
//                    InputStream is = conexao.getInputStream();
//                    String str = HttpUtil.bytesParaString(is);
//                    JSONObject jo = new JSONObject(str);
//
//                    Gson gson = new Gson();
//                    res = gson.fromJson(jo.toString(), Autenticacao.class);
//                }
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//
//            } finally {
//
//                if (conexao != null) {
//                    conexao.disconnect();
//                }
//            }
//
//            return res;
//        }
//
//    }

}
