package com.atucsara.Sistema_InstitutoBD.repositories;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Nota;
import com.atucsara.Sistema_InstitutoBD.models.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByAlumno(Alumno alumno);

    List<Nota> findByProfesor(Profesor profesor);

    List<Nota> findByModulo(String modulo);

    List<Nota> findByAlumnoAndModulo(Alumno alumno, String modulo);

    List<Nota> findByProfesorAndModulo(Profesor profesor, String modulo);

    List<Nota> findByAlumnoAndProfesorAndModulo(Alumno alumno, Profesor profesor, String modulo);

    @Query("SELECT n FROM Nota n WHERE n.alumno = :alumno AND n.modulo = :modulo AND n.grupoActividad = :grupoActividad")
    List<Nota> findByEstudianteAndModuloAndGrupoActividad(
            @Param("alumno") Alumno alumno,
            @Param("modulo") String modulo,
            @Param("grupoActividad") Nota.GrupoActividad grupoActividad);

    @Query("SELECT AVG(n.valorNota) FROM Nota n WHERE n.alumno = :alumno AND n.modulo = :modulo AND n.grupoActividad = :grupoActividad")
    Double calcularPromedioByGrupoActividad(
            @Param("alumno") Alumno alumno,
            @Param("modulo") String modulo,
            @Param("grupoActividad") Nota.GrupoActividad grupoActividad);

    @Query("SELECT COUNT(n) FROM Nota n WHERE n.alumno = :estudiante AND n.modulo = :modulo AND n.grupoActividad = :grupoActividad")
    Integer contarNotasByGrupoActividad(
            @Param("alumno") Alumno alumno,
            @Param("modulo") String modulo,
            @Param("grupoActividad") Nota.GrupoActividad grupoActividad);


}

