package com.atucsara.Sistema_InstitutoBD.repositories;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    // Verifica si existen alumnos para un profesor específico
    boolean existsByProfesorId(Long profesorId);

    // Opcional: Contar cuántos alumnos tiene un profesor
    long countByProfesorId(Long profesorId);

    // Opcional: Si necesitas obtener los alumnos de un profesor
    @Query("SELECT a FROM Alumno a WHERE a.profesor.id = :profesorId")
    List<Alumno> findAlumnosByProfesorId(@Param("profesorId") Long profesorId);
}