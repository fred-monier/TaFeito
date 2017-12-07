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

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;

import br.pe.recife.monier.tafeito.R;
import br.pe.recife.monier.tafeito.excecao.NegocioException;
import br.pe.recife.monier.tafeito.negocio.Autenticacao;
import br.pe.recife.monier.tafeito.util.HttpUtil;
import br.pe.recife.monier.tafeito.util.Util;

public class FornecedorLoginRESTActivity extends AppCompatActivity {

    public static final String AUTENTICACAO = "AUTENTICACAO";

    private static final int REQUEST_SIGNUP = 0;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;

    //Task Async
    private BuscarPorLoginPorSenhaFornRESTClientTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fornecedor_login_rest);

        _emailText    = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton  = (Button) findViewById(R.id.btn_login);
        _signupLink   = (TextView) findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Chama a tela de registro de fornecedor
                Intent intent = new Intent(getApplicationContext(), FornecedorRegistroRESTActivity.class);
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

    private void login() {

        if (!validate()) {
            onLoginFailed(null);
            return;
        }

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //Autenticar Fornecedor

        try {

            if (HttpUtil.temConexaoWeb(getApplicationContext())) {
                if (task == null || task.getStatus() != AsyncTask.Status.RUNNING) {

                    task = new BuscarPorLoginPorSenhaFornRESTClientTask(email, password);
                    task.execute();
                }
            } else {

                throw new NegocioException(getApplicationContext().getResources().
                        getText(R.string.login_conexaoweb_inexistente).toString());
            }

        } catch (Exception e) {

            onLoginFailed(e.getMessage());
        }

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

        //Chama a tela principal do fornecedor
        Intent intent = new Intent(getApplicationContext(), FornecedorPrincipalActivity.class);
        intent.putExtra(AUTENTICACAO, autenticacao);
        startActivity(intent);
    }

    private class BuscarPorLoginPorSenhaFornRESTClientTask extends AsyncTask<String, Void, Autenticacao> {

        private final String CAMINHO =
                //"http://192.168.1.107:8080/TaFeito_Servidor/rest/AcessoService/acessosLoginSenhaFornecedor";
                "http://187.87.134.250:8080/TaFeito_Servidor/rest/AcessoService/acessosLoginSenhaFornecedor";

        private String login;
        private String senha;

        private ProgressDialog progressDialog;

        public BuscarPorLoginPorSenhaFornRESTClientTask(String login, String senha) {
            this.login = login;
            this.senha = senha;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(FornecedorLoginRESTActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getApplicationContext().getResources().
                    getText(R.string.login_autenticando).toString());
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Autenticacao aut) {
            super.onPostExecute(aut);

            progressDialog.dismiss();

            if (aut != null){
                onLoginSuccess(aut);
            }
            else{
                onLoginFailed(null);
            }
        }

        @Override
        protected Autenticacao doInBackground(String... params) {

            Autenticacao res = null;

            HttpURLConnection conexao = null;

            try {

                String caminhoPar = CAMINHO + "/" + login + "/" + senha;

                conexao = HttpUtil.conectarGET(caminhoPar);
                conexao.connect();
                //

                int resposta = conexao.getResponseCode();
                if (resposta == HttpURLConnection.HTTP_OK) {

                    InputStream is = conexao.getInputStream();
                    String str = HttpUtil.bytesParaString(is);
                    JSONObject jo = new JSONObject(str);

                    Gson gson = new Gson();
                    res = gson.fromJson(jo.toString(), Autenticacao.class);
                }

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                if (conexao != null) {
                    conexao.disconnect();
                }
            }

            return res;
        }

    }

}


