package br.pe.recife.monier.tafeito.servicerest;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import br.pe.recife.monier.tafeito.R;
import br.pe.recife.monier.tafeito.util.HttpUtil;

public class LiberadoPorLoginRESTClientTask extends AsyncTask<String, Void, String> {

    private RESTClientTaskVO fRESTClientTaskVO;
    private Context contexto;
    private String login;

    public LiberadoPorLoginRESTClientTask(RESTClientTaskVO pRESTClientTaskVO, Context context, String login) {
        this.fRESTClientTaskVO = pRESTClientTaskVO;
        this.contexto = context;
        this.login = login;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s != null && s.equals(HttpUtil.SUCCESS_RESULT)) {

            this.fRESTClientTaskVO.getClienteTask().chamaSucesso(this.fRESTClientTaskVO.getOperacao(), null);
        }
        else if(s != null && s.equals(HttpUtil.FAILURE_RESULT)) {

            this.fRESTClientTaskVO.getClienteTask().chamaFalha(this.fRESTClientTaskVO.getOperacao(),
                    this.contexto.getResources().getText(R.string.registro_email_ja_existente).toString());
        } else if (s!= null) {

            this.fRESTClientTaskVO.getClienteTask().chamaFalha(this.fRESTClientTaskVO.getOperacao(), s);
        } else {

            this.fRESTClientTaskVO.getClienteTask().chamaFalha(this.fRESTClientTaskVO.getOperacao(),
                    this.contexto.getResources().getText(R.string.login_conexaoweb_falhou).toString());
        }
    }

    @Override
    protected String doInBackground(String... params) {

        String res = null;

        HttpURLConnection conexao = null;

        try {

            String urlCaminho = this.contexto.getResources().
                    getText(R.string.webREST_URL).toString() +
                    this.contexto.getResources().
                            getText(R.string.liberadoLogin).toString();

            conexao = HttpUtil.conectarPOST(urlCaminho);

            //Building URI
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("login", this.login);

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

            int resposta = conexao.getResponseCode();
            if (resposta == HttpURLConnection.HTTP_OK) {

                InputStream is = conexao.getInputStream();

                res = HttpUtil.bytesParaString(is);

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
