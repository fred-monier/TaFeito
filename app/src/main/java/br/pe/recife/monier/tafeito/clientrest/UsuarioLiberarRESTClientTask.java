package br.pe.recife.monier.tafeito.clientrest;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import br.pe.recife.monier.tafeito.R;
import br.pe.recife.monier.tafeito.http.LoginHttp;
import br.pe.recife.monier.tafeito.http.RetornoHttp;
import br.pe.recife.monier.tafeito.util.HttpUtil;

public class UsuarioLiberarRESTClientTask extends AsyncTask<String, Void, RetornoHttp> {

    private RESTClientTaskVO fRESTClientTaskVO;
    private Context contexto;
    private String login;

    public UsuarioLiberarRESTClientTask(RESTClientTaskVO pRESTClientTaskVO, Context context,
        String login) {

        this.fRESTClientTaskVO = pRESTClientTaskVO;
        this.contexto = context;
        this.login = login;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(RetornoHttp retorno) {
        super.onPostExecute(retorno);

        if (retorno != null && retorno.getResultado() != null
                && retorno.getResultado().equals(RetornoHttp.SUCESSO)) {

            if (retorno.getDescricao() != null
                    && retorno.getDescricao().equals(RetornoHttp.SUCESSO)) {

                this.fRESTClientTaskVO.getClienteTask().chamaSucesso(this.fRESTClientTaskVO.getOperacao(), null);

            } else {
                this.fRESTClientTaskVO.getClienteTask().chamaFalha(this.fRESTClientTaskVO.getOperacao(),
                        this.contexto.getResources().getText(R.string.registro_email_ja_existente).toString());
            }

        } else {

            this.fRESTClientTaskVO.getClienteTask().chamaFalha(this.fRESTClientTaskVO.getOperacao(), retorno.getDescricao());
        }
    }

    @Override
    protected RetornoHttp doInBackground(String... params) {

        RetornoHttp res = null;

        HttpURLConnection conexao = null;

        try {

            LoginHttp loginHttp = new LoginHttp();
            loginHttp.setLogin(login);

            Gson gson = new Gson();
            String jsonBody = gson.toJson(loginHttp);

            String urlCaminho = this.contexto.getResources().
                    getText(R.string.webREST_URL).toString();
            urlCaminho = urlCaminho + this.contexto.getResources().
                    getText(R.string.usuarioLiberar).toString();

            //
            conexao = HttpUtil.conectarPOST(urlCaminho);
            conexao.setFixedLengthStreamingMode(jsonBody.getBytes().length);
            conexao.setRequestProperty("Content-Type", "application/json");
            conexao.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conexao.connect();

            OutputStream os = new BufferedOutputStream(conexao.getOutputStream());
            os.write(jsonBody.getBytes());
            os.flush();
            //

            int resposta = conexao.getResponseCode();
            if (resposta == HttpURLConnection.HTTP_OK) {

                InputStream is = conexao.getInputStream();
                String str = HttpUtil.bytesParaString(is);
                JSONObject jo = new JSONObject(str);

                res = gson.fromJson(jo.toString(), RetornoHttp.class);
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
