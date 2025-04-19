package com.atucsara.Sistema_InstitutoBD.services;

import com.atucsara.Sistema_InstitutoBD.models.Profesor;
import com.atucsara.Sistema_InstitutoBD.repositories.AlumnoRepository;
import com.atucsara.Sistema_InstitutoBD.repositories.ProfesorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class ProfesorService {
    @Autowired
    private ProfesorRepository profesorRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;

    public List<Profesor> findAll() {
        return profesorRepository.findAll();
    }

    public Optional<Profesor> findById(Long id) {
        return profesorRepository.findById(id);
    }

    public Profesor save(@Valid Profesor profesor) {
        return profesorRepository.save(profesor);
    }

    public Profesor update(Long id, @Valid Profesor profesor) {
        if (!profesorRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró un profesor con el ID proporcionado");
        }
        profesor.setId(id); // Asegurar que se actualice el correcto
        return profesorRepository.save(profesor);
    }



    public List<Profesor> obtenerTodosLosProfesores() {
        return profesorRepository.findAll();
    }

    public List<Profesor> buscarProfesores(String searchTerm) {
        return profesorRepository.findAll().stream()
                .filter(profesor -> profesor.getNombres().toLowerCase().contains(searchTerm.toLowerCase())
                        || profesor.getApellidos().toLowerCase().contains(searchTerm.toLowerCase())
                        || profesor.getIdentificacion().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
    public void delete(Long id) {
        profesorRepository.deleteById(id);
    }
    public boolean tieneAlumnosAsignados(Long profesorId) {
        return alumnoRepository.existsByProfesorId(profesorId);
    }
}

