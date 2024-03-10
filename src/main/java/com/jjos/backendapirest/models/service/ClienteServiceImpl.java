package com.jjos.backendapirest.models.service;

import com.jjos.backendapirest.models.dao.IClienteDao;
import com.jjos.backendapirest.models.entity.Cliente;
import com.jjos.backendapirest.models.entity.Region;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClienteServiceImpl implements IClienteService{

    private IClienteDao iClienteDao;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarTodos() {
        return (List<Cliente>) iClienteDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> buscarTodos(Pageable paginador) {
        return iClienteDao.findAll(paginador);
    }

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {
        return iClienteDao.save(cliente);
    }

    @Override
    @Transactional
    public void eliminar(Long idCliente) {
        iClienteDao.deleteById(idCliente);
    }

    @Override
    public Cliente buscarPorId(Long idCliente) {
        Optional<Cliente> clienteEncontrado = iClienteDao.findById(idCliente);
        return clienteEncontrado.orElse(null);
    }

    @Override
    public List<Region> buscarRegiones() {
        return iClienteDao.findAllRegions();
    }
}
