package com.jjos.backendapirest.models.service;

import com.jjos.backendapirest.models.entity.Cliente;
import com.jjos.backendapirest.models.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClienteService {

    List<Cliente> buscarTodos();
    Page<Cliente> buscarTodos(Pageable paginador);
    Cliente guardar(Cliente cliente);
    void eliminar(Long idCliente);
    Cliente buscarPorId(Long idCliente);

    List<Region> buscarRegiones();
}
