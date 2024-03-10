package com.jjos.backendapirest.models.dao;

import com.jjos.backendapirest.models.entity.Cliente;
import com.jjos.backendapirest.models.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IClienteDao extends JpaRepository<Cliente, Long> {

    @Query("from Region")
    public List<Region> findAllRegions();
}
