package com.atucsara.Sistema_InstitutoBD.repositories;

import com.atucsara.Sistema_InstitutoBD.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    public Usuario findByEmail(String email);

}
