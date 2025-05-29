package com.atucsara.Sistema_InstitutoBD.repositories;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Nota;
import com.atucsara.Sistema_InstitutoBD.models.Nota.GrupoActividad;
import com.atucsara.Sistema_InstitutoBD.models.Profesor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {

    // Consultas básicas mejoradas
    List<Nota> findByAlumno(Alumno alumno);
    List<Nota> findByProfesor(Profesor profesor);
    List<Nota> findByModulo(String modulo);
    List<Nota> findByAlumnoAndModulo(Alumno alumno, String modulo);
    List<Nota> findByProfesorAndModulo(Profesor profesor, String modulo);
    List<Nota> findByAlumnoAndProfesorAndModulo(Alumno alumno, Profesor profesor, String modulo);

    // Consultas optimizadas con JOIN FETCH
    @Query("SELECT DISTINCT n FROM Nota n JOIN FETCH n.alumno WHERE n.modulo = :modulo")
    List<Nota> findByModuloWithAlumno(@Param("modulo") String modulo);

    @Query("SELECT DISTINCT n FROM Nota n JOIN FETCH n.alumno JOIN FETCH n.profesor WHERE n.modulo = :modulo")
    List<Nota> findByModuloWithAlumnoAndProfesor(@Param("modulo") String modulo);

    // Consultas específicas por grupo de actividad
    @Query("SELECT n FROM Nota n WHERE n.alumno = :alumno AND n.modulo = :modulo AND n.grupoActividad = :grupoActividad")
    List<Nota> findByAlumnoAndModuloAndGrupoActividad(
            @Param("alumno") Alumno alumno,
            @Param("modulo") String modulo,
            @Param("grupoActividad") GrupoActividad grupoActividad);

    // Consultas estadísticas
    @Query("SELECT AVG(n.valorNota) FROM Nota n WHERE n.alumno = :alumno AND n.modulo = :modulo AND n.grupoActividad = :grupoActividad")
    Double calcularPromedioByGrupoActividad(
            @Param("alumno") Alumno alumno,
            @Param("modulo") String modulo,
            @Param("grupoActividad") GrupoActividad grupoActividad);

    @Query("SELECT COUNT(n) FROM Nota n WHERE n.alumno = :alumno AND n.modulo = :modulo AND n.grupoActividad = :grupoActividad")
    Long contarNotasByGrupoActividad(
            @Param("alumno") Alumno alumno,
            @Param("modulo") String modulo,
            @Param("grupoActividad") GrupoActividad grupoActividad);

    // Nuevas consultas para resúmenes
    @Query("SELECT n.grupoActividad, AVG(n.valorNota) FROM Nota n WHERE n.modulo = :modulo GROUP BY n.grupoActividad")
    List<Object[]> findPromediosPorGrupoActividad(@Param("modulo") String modulo);

    @Query("SELECT n.alumno, AVG(n.valorNota) FROM Nota n WHERE n.modulo = :modulo GROUP BY n.alumno")
    List<Object[]> findPromediosPorAlumno(@Param("modulo") String modulo);

    @Query("SELECT n.modulo, AVG(n.valorNota), MAX(n.valorNota), MIN(n.valorNota) FROM Nota n GROUP BY n.modulo")
    List<Object[]> findEstadisticasPorModulo();

    // Consulta para obtener resumen completo por módulo y alumno
    @Query("SELECT n.grupoActividad, AVG(n.valorNota), COUNT(n) FROM Nota n " +
            "WHERE n.alumno = :alumno AND n.modulo = :modulo " +
            "GROUP BY n.grupoActividad")
    List<Object[]> findResumenNotasPorAlumnoYModulo(
            @Param("alumno") Alumno alumno,
            @Param("modulo") String modulo);

    // Consulta para obtener módulos distintos
    @Query("SELECT DISTINCT n.modulo FROM Nota n ORDER BY n.modulo")
    List<String> findDistinctModulos();
    ;

    // Consulta optimizada para cargar relaciones
    @Query("SELECT DISTINCT n FROM Nota n JOIN FETCH n.alumno LEFT JOIN FETCH n.profesor")
    List<Nota> findAllWithAlumnoAndProfesor();

    // En NotaRepository.java
    @Query("SELECT DISTINCT n FROM Nota n LEFT JOIN FETCH n.alumno LEFT JOIN FETCH n.profesor")
    List<Nota> findAllWithRelations();



List<Nota> findAll(Specification<Nota> spec, Sort fechaRegistro);
}