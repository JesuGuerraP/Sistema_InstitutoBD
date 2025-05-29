package com.atucsara.Sistema_InstitutoBD.services;

import com.atucsara.Sistema_InstitutoBD.models.Rol;
import com.atucsara.Sistema_InstitutoBD.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Rol findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }
}
