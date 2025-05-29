package com.atucsara.Sistema_InstitutoBD.services;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Nota;
import com.atucsara.Sistema_InstitutoBD.models.Profesor;
import com.atucsara.Sistema_InstitutoBD.repositories.NotaRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Transactional
public class NotaService { // Marcar la clase como transaccional por defecto

    private final NotaRepository notaRepository;

    @Autowired
    public NotaService(NotaRepository notaRepository) {
        this.notaRepository = notaRepository;
    }

    public Nota guardarNota(Nota nota) {
        return notaRepository.save(nota);
    }

    public void eliminarNota(Long id) {
        notaRepository.deleteById(id);
    }

    public Optional<Nota> buscarPorId(Long id) {
        return notaRepository.findById(id);
    }

    public List<Nota> buscarPoralumno(Alumno alumno) {
        return notaRepository.findByAlumno(alumno);
    }

    public List<Nota> buscarPorProfesor(Profesor profesor) {
        return notaRepository.findByProfesor(profesor);
    }

    public List<Nota> buscarPorModulo(String modulo) {
        if (modulo == null || modulo.isEmpty()) {
            return notaRepository.findAll(); // Considera si este es el comportamiento deseado
        }
        return notaRepository.findByModulo(modulo);
    }

    public List<Nota> buscarPorAlumnoYModulo(Alumno alumno, String modulo) {
        return notaRepository.findByAlumnoAndModulo(alumno, modulo);
    }

    public Map<Nota.GrupoActividad, Double> calcularPromediosPorGrupo(Alumno alumno, String modulo) {
        Map<Nota.GrupoActividad, Double> promedios = new HashMap<>();

        for (Nota.GrupoActividad grupo : Nota.GrupoActividad.values()) {
            Double promedio = notaRepository.calcularPromedioByGrupoActividad(alumno, modulo, grupo);
            promedios.put(grupo, promedio != null ? promedio : 0.0);
        }

        return promedios;
    }

    public Double calcularNotaFinal(Alumno alumno, String modulo) {
        double notaFinal = 0.0;
        Map<Nota.GrupoActividad, Double> promedios = calcularPromediosPorGrupo(alumno, modulo);

        for (Map.Entry<Nota.GrupoActividad, Double> entry : promedios.entrySet()) {
            notaFinal += entry.getValue() * (entry.getKey().getPorcentaje() / 100.0);
        }

        return Math.round(notaFinal * 100.0) / 100.0;
    }

    public Map<String, Object> obtenerResumenNotas(Alumno alumno, String modulo) {
        Map<String, Object> resumen = new HashMap<>();
        Map<Nota.GrupoActividad, Double> promediosPorGrupo = calcularPromediosPorGrupo(alumno, modulo);
        Double notaFinal = calcularNotaFinal(alumno, modulo);

        Map<Nota.GrupoActividad, Integer> conteoActividades = new HashMap<>();
        Map<Nota.GrupoActividad, Double> notasPonderadas = new HashMap<>();

        for (Nota.GrupoActividad grupo : Nota.GrupoActividad.values()) {
            Integer conteo = Math.toIntExact(notaRepository.contarNotasByGrupoActividad(alumno, modulo, grupo));
            conteoActividades.put(grupo, conteo);
            Double promedio = promediosPorGrupo.getOrDefault(grupo, 0.0);
            notasPonderadas.put(grupo, Math.round((promedio * (grupo.getPorcentaje() / 100.0)) * 100.0) / 100.0);
        }

        resumen.put("promediosPorGrupo", promediosPorGrupo);
        resumen.put("notasPonderadas", notasPonderadas);
        resumen.put("conteoActividades", conteoActividades);
        resumen.put("notaFinal", notaFinal);

        return resumen;
    }


    // Métodos adicionales para el Service
    public Map<String, Object> obtenerResumenGeneralModulo(String modulo) {
        List<Nota> notas = buscarPorModulo(modulo);
        Map<Alumno, List<Nota>> notasPorAlumno = notas.stream()
                .filter(nota -> nota.getAlumno() != null)
                .collect(Collectors.groupingBy(Nota::getAlumno));

        return calcularResumenGeneralModulo(notasPorAlumno);
    }

    private Map<String, Object> calcularResumenGeneralModulo(Map<Alumno, List<Nota>> notasPorAlumno) {
        Map<String, Object> resumen = new HashMap<>();

        // Obtener todas las notas válidas
        List<Double> todasLasNotas = notasPorAlumno.values().stream()
                .flatMap(notasAlumno -> notasAlumno.stream()
                        .map(Nota::getValorNota)
                        .filter(Objects::nonNull))
                .collect(Collectors.toList());

        if (!todasLasNotas.isEmpty()) {
            // Calcular estadísticas generales
            DoubleSummaryStatistics stats = todasLasNotas.stream()
                    .mapToDouble(Double::doubleValue)
                    .summaryStatistics();

            // Calcular total de aprobados (promedio >= 3.0)
            long totalAprobados = notasPorAlumno.keySet().stream()
                    .filter(alumno -> {
                        Double promedio = calcularPromedioAlumno(notasPorAlumno.get(alumno));
                        return promedio != null && promedio >= 3.0;
                    })
                    .count();

            resumen.put("totalAlumnos", notasPorAlumno.size());
            resumen.put("promedioGeneral", stats.getAverage());
            resumen.put("notaMaxima", stats.getMax());
            resumen.put("notaMinima", stats.getMin());
            resumen.put("totalAprobados", totalAprobados);
            resumen.put("totalReprobados", notasPorAlumno.size() - totalAprobados);
        } else {
            // Valores por defecto si no hay notas
            resumen.put("totalAlumnos", 0);
            resumen.put("promedioGeneral", 0.0);
            resumen.put("notaMaxima", "N/A");
            resumen.put("notaMinima", "N/A");
            resumen.put("totalAprobados", 0);
            resumen.put("totalReprobados", 0);
        }

        return resumen;
    }

    // En NotaService
    public Double calcularPromedioAlumno(List<Nota> notas) {
        return Optional.ofNullable(notas)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .mapToDouble(n -> Optional.ofNullable(n.getValorNota()).orElse(0.0))
                .average()
                .orElse(0.0);
    }

    public Double calcularPromedioPonderado(List<Nota> notas) {
        // Agrupar notas por tipo de actividad
        Map<Nota.GrupoActividad, List<Nota>> notasPorGrupo = notas.stream()
                .collect(Collectors.groupingBy(Nota::getGrupoActividad));

        // Calcular promedios parciales
        double promedioActividades1 = calcularPromedioGrupo(
                notasPorGrupo.getOrDefault(Nota.GrupoActividad.ACTIVIDADES_1, Collections.emptyList()));

        double promedioActividades2 = calcularPromedioGrupo(
                notasPorGrupo.getOrDefault(Nota.GrupoActividad.ACTIVIDADES_2, Collections.emptyList()));

        double promedioEvaluacionFinal = calcularPromedioGrupo(
                notasPorGrupo.getOrDefault(Nota.GrupoActividad.EVALUACION_FINAL, Collections.emptyList()));

        // Aplicar ponderaciones
        return (promedioActividades1 * 0.30)
                + (promedioActividades2 * 0.30)
                + (promedioEvaluacionFinal * 0.40);
    }

    private double calcularPromedioGrupo(List<Nota> notas) {
        if (notas == null || notas.isEmpty()) {
            return 0.0;
        }
        return notas.stream()
                .mapToDouble(Nota::getValorNota)
                .average()
                .orElse(0.0);
    }
    // Método para buscar notas con filtros
    public List<Nota> buscarNotasFiltradas(String modulo, Long alumnoId, Nota.GrupoActividad grupo) {
        try {
            Specification<Nota> spec = Specification.where(null);

            if (modulo != null && !modulo.isEmpty()) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("modulo"), modulo));
            }

            if (alumnoId != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("alumno").get("id"), alumnoId));
            }

            if (grupo != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("grupoActividad"), grupo));
            }

            return notaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "fechaRegistro"));
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar notas filtradas", e);
        }
    }

    // Métodos auxiliares para Specification
    private Specification<Nota> withModulo(String modulo) {
        return (root, query, cb) ->
                modulo == null || modulo.isEmpty() ?
                        cb.conjunction() :
                        cb.equal(root.get("modulo"), modulo);
    }

    private Specification<Nota> withAlumno(Long alumnoId) {
        return (root, query, cb) ->
                alumnoId == null ?
                        cb.conjunction() :
                        cb.equal(root.join("alumno").get("id"), alumnoId);
    }

    private Specification<Nota> withGrupo(Nota.GrupoActividad grupo) {
        return (root, query, cb) ->
                grupo == null ?
                        cb.conjunction() :
                        cb.equal(root.get("grupoActividad"), grupo);
    }
    // Método para obtener módulos únicos
    public List<String> obtenerModulosUnicos() {
        try {
            return notaRepository.findDistinctModulos();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener módulos únicos", e);
        }
    }

    // Método optimizado para obtener todas las notas con relaciones cargadas
    public List<Nota> findAllWithRelations() {
        return notaRepository.findAllWithAlumnoAndProfesor();
    }

}