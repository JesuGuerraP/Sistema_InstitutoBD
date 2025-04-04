package com.atucsara.Sistema_InstitutoBD.services;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Finanza; // Importar Finanza
import com.atucsara.Sistema_InstitutoBD.repositories.AlumnoRepository;
import com.atucsara.Sistema_InstitutoBD.repositories.FinanzaRepository; // Importar FinanzaRepository
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar @Transactional
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired // Inyectar FinanzaRepository
    private FinanzaRepository finanzaRepository;

    public List<Alumno> findAll() {
        return alumnoRepository.findAll();
    }

    public Optional<Alumno> findById(Long id) {
        return alumnoRepository.findById(id);
    }

    public Alumno save(@Valid Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    public Alumno update(Long id, @Valid Alumno alumno) {
        if (!alumnoRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró un alumno con el ID proporcionado");
        }
        alumno.setId(id); // Asegurar que se actualice el correcto
        return alumnoRepository.save(alumno);
    }

    @Transactional // <-- Añadir Transaccionalidad
    public void delete(Long id) {
        // 1. Verificar si el alumno existe
        if (!alumnoRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró un alumno con el ID proporcionado para eliminar");
        }

        // 2. Buscar todas las finanzas asociadas a este alumno
        //    (Necesitas asegurarte de que FinanzaRepository tenga un método como findByAlumnoId)
        //    Si no lo tienes, añádelo a FinanzaRepository: List<Finanza> findByAlumnoId(Long alumnoId);
        List<Finanza> finanzasAsociadas = finanzaRepository.findByAlumnoId(id);

        // 3. Desvincular el alumno de cada finanza asociada
        for (Finanza finanza : finanzasAsociadas) {
            finanza.setAlumno(null); // Establecer la referencia a null
            finanzaRepository.save(finanza); // Guardar el cambio en la finanza
        }

        // 4. Ahora sí, eliminar el alumno
        alumnoRepository.deleteById(id);
    }

    public List<Alumno> obtenerTodosLosAlumnos() {
        return alumnoRepository.findAll();
    }

    public List<Alumno> buscarAlumnos(String searchTerm) {
        return alumnoRepository.findAll().stream()
                .filter(alumno -> alumno.getNombres().toLowerCase().contains(searchTerm.toLowerCase())
                        || alumno.getApellidos().toLowerCase().contains(searchTerm.toLowerCase())
                        || alumno.getIdentificacion().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
}