package br.pe.recife.monier.tafeito.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.pe.recife.monier.tafeito.bd.BDSQLiteTaFeito;
import br.pe.recife.monier.tafeito.negocio.Fornecedor;
import br.pe.recife.monier.tafeito.negocio.ServicoCategoria;
import br.pe.recife.monier.tafeito.util.Util;

public class FornecedorDAO implements IDAOson<Fornecedor> {

    private static FornecedorDAO instancia;
    private BDSQLiteTaFeito bd;

    public static FornecedorDAO getInstancia(Context context) {

        if (instancia == null) {
            instancia = new FornecedorDAO(context);
        }

        return instancia;
    }

    private FornecedorDAO(Context context) {
        this.bd = BDSQLiteTaFeito.getInstancia(context);
    }

    private long inserir(Fornecedor fornecedor) {

        SQLiteDatabase db = bd.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_ID, fornecedor.getId());
        cv.put(BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_CNPJ, fornecedor.getCnpj());

        long id = db.insert(BDSQLiteTaFeito.TABELA_FORNECEDOR, null, cv);

        if (id != -1) {
            fornecedor.setId(id);
        }

        db.close();

        return id;
    }

    private int atualizar(Fornecedor fornecedor) {

        SQLiteDatabase db = bd.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_CNPJ, fornecedor.getCnpj());

        int linhasAlteradas = db.update(BDSQLiteTaFeito.TABELA_FORNECEDOR, cv,
                BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_ID + " = ?",
                new String[]{String.valueOf(fornecedor.getId())});
        db.close();

        return linhasAlteradas;
    }

    @Override
    public void salvar(Fornecedor fornecedor, boolean novo) {
        if (novo) {
            this.inserir(fornecedor);
        } else {
            this.atualizar(fornecedor);
        }
    }

    @Override
    public Fornecedor consultar(long id) {

        Fornecedor res = null;

        SQLiteDatabase db = bd.getReadableDatabase();

        String sql = "SELECT * FROM " + BDSQLiteTaFeito.TABELA_FORNECEDOR;

        sql = sql + " INNER JOIN " + BDSQLiteTaFeito.TABELA_USUARIO;
        sql = sql + " ON " + BDSQLiteTaFeito.TABELA_FORNECEDOR + "." + BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_ID;
        sql = sql + " = " + BDSQLiteTaFeito.TABELA_USUARIO + "." + BDSQLiteTaFeito.TABELA_USUARIO_COLUNA_ID;

        sql = sql + " WHERE " + BDSQLiteTaFeito.TABELA_FORNECEDOR + "." + BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_ID + " = ?";
        String args[] = new String[]{"" + id + ""};

        Cursor cursor = db.rawQuery(sql, args);
        if (cursor.moveToNext()) {

            //
            long idCol = cursor.getLong(0);
            String cnpjCol = cursor.getString(1);

            //From USUARIO
            int habUsu = cursor.getInt(3);
            String nomeUsu = cursor.getString(4);
            String endUsu = cursor.getString(5);
            String emailCol = cursor.getString(6);
            int telCol = cursor.getInt(7);

            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setId(idCol);
            fornecedor.setHabilitado(Util.valorBooleano(habUsu));
            fornecedor.setNome(nomeUsu);
            fornecedor.setEndereco(endUsu);
            fornecedor.setEmail(emailCol);
            fornecedor.setTelefone(telCol);
            fornecedor.setCnpj(cnpjCol);


            res = fornecedor;
        }

        cursor.close();

        return res;

    }

    @Override
    public int excluir(Fornecedor fornecedor) {

        SQLiteDatabase db = bd.getWritableDatabase();

        int linhasExcluidas = db.delete(BDSQLiteTaFeito.TABELA_FORNECEDOR,
                BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_ID + " = ?",
                new String[]{String.valueOf(fornecedor.getId())});
        db.close();

        return linhasExcluidas;
    }

    @Override
    public List<Fornecedor> listar() {

        List<Fornecedor> res = new ArrayList<Fornecedor>();

        SQLiteDatabase db = bd.getReadableDatabase();

        String sql = "SELECT * FROM " + BDSQLiteTaFeito.TABELA_FORNECEDOR;

        sql = sql + " INNER JOIN " + BDSQLiteTaFeito.TABELA_USUARIO;
        sql = sql + " ON " + BDSQLiteTaFeito.TABELA_FORNECEDOR + "." + BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_ID;
        sql = sql + " = " + BDSQLiteTaFeito.TABELA_USUARIO + "." + BDSQLiteTaFeito.TABELA_USUARIO_COLUNA_ID;

        //sql = sql + " WHERE " + BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_XXX + " = ?";
        //String args[] = new String[]{"" + "XXX" + ""};

        sql = sql + " ORDER BY " + "5";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {

            //
            long idCol = cursor.getLong(0);
            String cnpjCol = cursor.getString(1);

            //From USUARIO
            int habUsu = cursor.getInt(3);
            String nomeUsu = cursor.getString(4);
            String endUsu = cursor.getString(5);
            String emailCol = cursor.getString(6);
            int telCol = cursor.getInt(7);

            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setId(idCol);
            fornecedor.setHabilitado(Util.valorBooleano(habUsu));
            fornecedor.setNome(nomeUsu);
            fornecedor.setEndereco(endUsu);
            fornecedor.setEmail(emailCol);
            fornecedor.setTelefone(telCol);
            fornecedor.setCnpj(cnpjCol);

            res.add(fornecedor);
        }

        cursor.close();

        return res;
    }

    public List<Fornecedor> listarPorServicoCategoria(ServicoCategoria servCat) {

        List<Fornecedor> res = new ArrayList<Fornecedor>();

        SQLiteDatabase db = bd.getReadableDatabase();

        String sql = "SELECT * FROM " + BDSQLiteTaFeito.TABELA_FORNECEDOR;

        sql = sql + " INNER JOIN " + BDSQLiteTaFeito.TABELA_USUARIO;
        sql = sql + " ON " + BDSQLiteTaFeito.TABELA_FORNECEDOR + "." + BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_ID;
        sql = sql + " = " + BDSQLiteTaFeito.TABELA_USUARIO + "." + BDSQLiteTaFeito.TABELA_USUARIO_COLUNA_ID;
        sql = sql + " INNER JOIN " + BDSQLiteTaFeito.TABELA_SERVICO;
        sql = sql + " ON " + BDSQLiteTaFeito.TABELA_SERVICO + "." + BDSQLiteTaFeito.TABELA_SERVICO_COLUNA_ID_FORNECEDOR;
        sql = sql + " = " + BDSQLiteTaFeito.TABELA_FORNECEDOR + "." + BDSQLiteTaFeito.TABELA_FORNECEDOR_COLUNA_ID;
        sql = sql + " INNER JOIN " + BDSQLiteTaFeito.TABELA_SERVICO_CATEGORIA;
        sql = sql + " ON " + BDSQLiteTaFeito.TABELA_SERVICO + "." + BDSQLiteTaFeito.TABELA_SERVICO_COLUNA_ID_SERVICO_CATEGORIA;
        sql = sql + " = " + BDSQLiteTaFeito.TABELA_SERVICO_CATEGORIA + "." + BDSQLiteTaFeito.TABELA_SERVICO_CATEGORIA_COLUNA_ID;

        sql = sql + " WHERE " + BDSQLiteTaFeito.TABELA_SERVICO_CATEGORIA + "." + BDSQLiteTaFeito.TABELA_SERVICO_CATEGORIA_COLUNA_ID + " = ?";
        String args[] = new String[]{"" + servCat.getId() + ""};

        sql = sql + " ORDER BY " + "5";

        Cursor cursor = db.rawQuery(sql, args);
        while (cursor.moveToNext()) {

            //
            long idCol = cursor.getLong(0);
            String cnpjCol = cursor.getString(1);

            //From USUARIO
            int habUsu = cursor.getInt(3);
            String nomeUsu = cursor.getString(4);
            String endUsu = cursor.getString(5);
            String emailCol = cursor.getString(6);
            int telCol = cursor.getInt(7);

            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setId(idCol);
            fornecedor.setHabilitado(Util.valorBooleano(habUsu));
            fornecedor.setNome(nomeUsu);
            fornecedor.setEndereco(endUsu);
            fornecedor.setEmail(emailCol);
            fornecedor.setTelefone(telCol);
            fornecedor.setCnpj(cnpjCol);

            res.add(fornecedor);
        }

        cursor.close();

        return res;
    }
}
