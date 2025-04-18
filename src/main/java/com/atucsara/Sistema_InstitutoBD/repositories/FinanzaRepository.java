package com.atucsara.Sistema_InstitutoBD.repositories;

import com.atucsara.Sistema_InstitutoBD.models.Finanza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinanzaRepository extends JpaRepository<Finanza, Long> {

    List<Finanza> findByEstado(Finanza.EstadoPago estado);


    List<Finanza> findByAlumnoId(Long alumnoId);
    // Verificar si existe una finanza para un alumno
    boolean existsByAlumnoId(Long alumnoId);

    List<Finanza> findByEstado(String estado);



    Page<Finanza> findByEstado(String estado, Pageable pageable);
}
