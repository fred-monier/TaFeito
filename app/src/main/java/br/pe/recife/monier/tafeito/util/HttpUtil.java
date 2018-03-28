package br.pe.recife.monier.tafeito.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Frederico on 07/12/2017.
 */

public class HttpUtil {

    public static boolean temConexaoWeb(Context ctx) {
        boolean res = false;

        ConnectivityManager con = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = con.getActiveNetworkInfo();

        if (net != null && net.isConnected()) {
            res = true;
        }

        return res;
    }

    public static HttpURLConnection conectarGET(String urlArquivo) throws IOException {

        final int MILI = 1000;

        URL url = new URL(urlArquivo);

        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setReadTimeout(20 * MILI);
        conexao.setConnectTimeout(30 * MILI);
        conexao.setRequestMethod("GET");
        conexao.setDoInput(true);
        conexao.setDoOutput(false);

        return conexao;

    }

    public static HttpURLConnection conectarPOST(String urlArquivo) throws IOException {

        final int MILI = 1000;

        URL url = new URL(urlArquivo);

        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setReadTimeout(20 * MILI);
        conexao.setConnectTimeout(30 * MILI);
        conexao.setRequestMethod("POST");
        conexao.setDoInput(true);
        conexao.setDoOutput(true);

        return conexao;

    }

    public static String bytesParaString(InputStream is) throws IOException {

        byte[] buffer = new byte[1024];

        ByteArrayOutputStream buferzao = new ByteArrayOutputStream();

        int bytesLidos;

        while ((bytesLidos = is.read(buffer)) != -1) {
            buferzao.write(buffer, 0, bytesLidos);
        }

        return new String(buferzao.toByteArray(), "UTF-8");
    }


}
