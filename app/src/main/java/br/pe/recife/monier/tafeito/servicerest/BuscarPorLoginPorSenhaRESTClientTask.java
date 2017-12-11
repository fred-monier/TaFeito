package br.pe.recife.monier.tafeito.servicerest;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import br.pe.recife.monier.tafeito.R;
import br.pe.recife.monier.tafeito.negocio.Autenticacao;
import br.pe.recife.monier.tafeito.util.HttpUtil;

public class BuscarPorLoginPorSenhaRESTClientTask extends AsyncTask<String, Void, Autenticacao> {

    private RESTClientTaskVO fRESTClientTaskVO;
    private Context contexto;
    private String login;
    private String senha;
    private boolean fornecedor;

    public BuscarPorLoginPorSenhaRESTClientTask(RESTClientTaskVO pRESTClientTaskVO, Context context,
                                                String login, String senha, boolean fornecedor) {
        this.fRESTClientTaskVO = pRESTClientTaskVO;
        this.contexto = context;
        this.login = login;
        this.senha = senha;
        this.fornecedor = fornecedor;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Autenticacao aut) {
        super.onPostExecute(aut);

        if (aut != null){
            //onLoginSuccess(aut);
            this.fRESTClientTaskVO.getClienteTask().chamaSucesso(this.fRESTClientTaskVO.getOperacao(), aut);
        }
        else{
            //onLoginFailed(null);
            this.fRESTClientTaskVO.getClienteTask().chamaFalha(this.fRESTClientTaskVO.getOperacao(), null);
        }
    }

    @Override
    protected Autenticacao doInBackground(String... params) {

        Autenticacao res = null;

        HttpURLConnection conexao = null;

        try {

            String caminhoPar = this.contexto.getResources().
                    getText(R.string.webREST_URL).toString();
            if (fornecedor) {
                caminhoPar = caminhoPar + this.contexto.getResources().
                        getText(R.string.acessosLoginSenhaFornecedor).toString();
            } else {
                caminhoPar = caminhoPar + this.contexto.getResources().
                        getText(R.string.acessosLoginSenhaCliente).toString();
            }

            conexao = HttpUtil.conectarPOST(caminhoPar);

            //Building URI
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("login", this.login)
                    .appendQueryParameter("senha", this.senha);

            //Getting object of OutputStream from urlConnection to write some data to stream
            OutputStream outputStream = conexao.getOutputStream();

            //Writer to write data to OutputStream
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(builder.build().getEncodedQuery());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            conexao.connect();
            //
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
