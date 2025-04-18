package com.atucsara.Sistema_InstitutoBD.services;

import com.atucsara.Sistema_InstitutoBD.Dto.FinanzaDTO;
import com.atucsara.Sistema_InstitutoBD.models.Finanza;
import jakarta.validation.Valid;

import java.util.List;

public interface FinanzaService {
    List<FinanzaDTO> findAll();
    FinanzaDTO findById(Long id);
    Finanza save(@Valid Finanza finanza); // Nuevo método
    FinanzaDTO create(FinanzaDTO finanzaDTO);
    FinanzaDTO update(Long id, FinanzaDTO finanzaDTO);
    void delete(Long id);
    List<FinanzaDTO> findByEstado(String estado);
    List<FinanzaDTO> findByAlumnoId(Long alumnoId);

    void desvincularAlumnoDeFinanza(Long id);
    boolean existsById(Long id);

    List<Finanza> buscarFinanzas(String searchTerm);
}