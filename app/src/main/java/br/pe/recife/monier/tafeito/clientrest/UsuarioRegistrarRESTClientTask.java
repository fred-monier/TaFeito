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
import br.pe.recife.monier.tafeito.http.RetornoHttp;
import br.pe.recife.monier.tafeito.http.UsuarioHttp;
import br.pe.recife.monier.tafeito.negocio.Acesso;
import br.pe.recife.monier.tafeito.negocio.Autenticacao;
import br.pe.recife.monier.tafeito.negocio.Cliente;
import br.pe.recife.monier.tafeito.negocio.Fornecedor;
import br.pe.recife.monier.tafeito.negocio.Usuario;
import br.pe.recife.monier.tafeito.util.HttpUtil;

public class UsuarioRegistrarRESTClientTask extends AsyncTask<String, Void, RetornoHttp> {

    private RESTClientTaskVO fRESTClientTaskVO;
    private Context contexto;
    private Acesso acesso;
    private Usuario usuario;

    public UsuarioRegistrarRESTClientTask(RESTClientTaskVO pRESTClientTaskVO, Context context,
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
    protected void onPostExecute(RetornoHttp retorno) {
        super.onPostExecute(retorno);

        if (retorno != null && retorno.getResultado() != null
                && retorno.getResultado().equals(RetornoHttp.SUCESSO)) {

            Autenticacao aut = new Autenticacao();
            aut.setIdAcesso(Long.parseLong(retorno.getDescricao()));
            aut.setToken("");

            this.fRESTClientTaskVO.getClienteTask().chamaSucesso(this.fRESTClientTaskVO.getOperacao(), aut);
        }
        else{

            this.fRESTClientTaskVO.getClienteTask().chamaFalha(this.fRESTClientTaskVO.getOperacao(), retorno.getResultado());
        }
    }

    @Override
    protected RetornoHttp doInBackground(String... params) {

        RetornoHttp res = null;

        HttpURLConnection conexao = null;

        try {

            UsuarioHttp usuarioHttp = new UsuarioHttp();
            usuarioHttp.setLogin(acesso.getLogin());
            usuarioHttp.setSenha(acesso.getSenha());
            usuarioHttp.setHabilitado("true");
            usuarioHttp.setNome(usuario.getNome());
            usuarioHttp.setEndereco(usuario.getEndereco());
            usuarioHttp.setEmail(usuario.getEmail());
            usuarioHttp.setTelefone(usuario.getTelefone());

            if (this.usuario instanceof Fornecedor) {
                usuarioHttp.setTipoUsuario(UsuarioHttp.FORNECEDOR);
                usuarioHttp.setDocumento(((Fornecedor) this.usuario).getCnpj());
            } else {
                usuarioHttp.setTipoUsuario(UsuarioHttp.CLIENTE);
                usuarioHttp.setDocumento(((Cliente) this.usuario).getCpf());
            }

            Gson gson = new Gson();
            String jsonBody = gson.toJson(usuarioHttp);

            String urlCaminho = this.contexto.getResources().
                    getText(R.string.webREST_URL).toString();
            urlCaminho = urlCaminho + this.contexto.getResources().
                    getText(R.string.usuarioRegistrar).toString();

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
