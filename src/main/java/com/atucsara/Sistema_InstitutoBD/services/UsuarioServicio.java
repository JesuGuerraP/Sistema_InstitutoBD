package com.atucsara.Sistema_InstitutoBD.services;

import com.atucsara.Sistema_InstitutoBD.Dto.UsuarioRegistroDTO;
import com.atucsara.Sistema_InstitutoBD.models.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsuarioServicio extends UserDetailsService {

    public Usuario guardar(UsuarioRegistroDTO registroDTO);

    public List<Usuario> listarUsuarios();

}
