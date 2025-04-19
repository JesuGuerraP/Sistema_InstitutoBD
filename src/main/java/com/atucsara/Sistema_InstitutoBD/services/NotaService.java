package com.atucsara.Sistema_InstitutoBD.services;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Nota;
import com.atucsara.Sistema_InstitutoBD.models.Profesor;
import com.atucsara.Sistema_InstitutoBD.repositories.NotaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            Integer conteo = notaRepository.contarNotasByGrupoActividad(alumno, modulo, grupo);
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
}