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
import br.pe.recife.monier.tafeito.negocio.Acesso;
import br.pe.recife.monier.tafeito.negocio.Autenticacao;
import br.pe.recife.monier.tafeito.negocio.Cliente;
import br.pe.recife.monier.tafeito.negocio.Fornecedor;
import br.pe.recife.monier.tafeito.negocio.Usuario;
import br.pe.recife.monier.tafeito.util.HttpUtil;

public class InserirAcessoRESTClientTask extends AsyncTask<String, Void, String> {

    private RESTClientTaskVO fRESTClientTaskVO;
    private Context contexto;
    private Acesso acesso;
    private Usuario usuario;

    public InserirAcessoRESTClientTask(RESTClientTaskVO pRESTClientTaskVO, Context context,
                                       Acesso acesso, Usuario usuario) {
        this.fRESTClientTaskVO = pRESTClientTaskVO;
        this.contexto = context;
        this.acesso = acesso;
        this.usuario = usuario;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s != null && !s.equals(HttpUtil.FAILURE_RESULT)) {

            Autenticacao aut = new Autenticacao();
            aut.setIdAcesso(HttpUtil.extrairIdResult(s));
            aut.setToken("");

            //onSignupSuccess(new Autenticacao());
            this.fRESTClientTaskVO.getClienteTask().chamaSucesso(this.fRESTClientTaskVO.getOperacao(), aut);
        }
        else{
            //onSignupFailed(null);
            this.fRESTClientTaskVO.getClienteTask().chamaFalha(this.fRESTClientTaskVO.getOperacao(), null);
        }
    }

    @Override
    protected String doInBackground(String... params) {

        String res = null;

        HttpURLConnection conexao = null;

        try {

            String documento;
            String valorDoc;
            String urlCaminho = this.contexto.getResources().
                    getText(R.string.webREST_URL).toString();

            if (this.usuario instanceof Fornecedor) {
                urlCaminho = urlCaminho + this.contexto.getResources().
                        getText(R.string.acessosFornecedor).toString();
                documento = "cnpj";
                valorDoc = ((Fornecedor) this.usuario).getCnpj();
            } else {
                urlCaminho = urlCaminho + this.contexto.getResources().
                        getText(R.string.acessosCliente).toString();
                documento = "cpf";
                valorDoc = ((Cliente) this.usuario).getCpf();
            }

            conexao = HttpUtil.conectarPOST(urlCaminho);

            //Building URI
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("login", this.acesso.getLogin())
                    .appendQueryParameter("senha", this.acesso.getSenha())
                    .appendQueryParameter("endereco", this.usuario.getEndereco())
                    .appendQueryParameter("habilitado", this.usuario.isHabilitado() + "")
                    .appendQueryParameter("nome", this.usuario.getNome())
                    .appendQueryParameter("telefone", this.usuario.getTelefone() + "")
                    .appendQueryParameter(documento, valorDoc);

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
