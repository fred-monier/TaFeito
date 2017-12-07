package br.pe.recife.monier.tafeito.dao;

import java.util.List;

public interface IDAO<T> {

    void salvar(T entidade);
    int excluir(T entidade);
    T consultar(long id);
    List<T> listar();

}
