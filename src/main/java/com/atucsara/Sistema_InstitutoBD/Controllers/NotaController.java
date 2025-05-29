package com.atucsara.Sistema_InstitutoBD.Controllers;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Nota;
import com.atucsara.Sistema_InstitutoBD.models.Profesor;
import com.atucsara.Sistema_InstitutoBD.repositories.NotaRepository;
import com.atucsara.Sistema_InstitutoBD.services.AlumnoService;
import com.atucsara.Sistema_InstitutoBD.services.NotaService;
import com.atucsara.Sistema_InstitutoBD.services.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/notas")
public class NotaController {

    private final NotaService notaService;
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;
    private NotaRepository notaRepository;

    @Autowired
    public NotaController(NotaService notaService, AlumnoService alumnoService, ProfesorService profesorService, NotaRepository notaRepository) {
        this.notaService = notaService;
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
        this.notaRepository = notaRepository;
    }


    @GetMapping
    public String listarNotas(
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) Long alumnoId,
            @RequestParam(required = false) Nota.GrupoActividad grupo,
            Model model) {
        try {
            // Obtener datos filtrados
            List<Nota> notas = notaService.buscarNotasFiltradas(modulo, alumnoId, grupo);

            // Añadir todos los atributos necesarios - CORREGIDO: solo un atributo notas
            model.addAttribute("notas", notas != null ? notas : Collections.emptyList());
            model.addAttribute("activeMenu", "notas");
            model.addAttribute("modulosUnicos", notaService.obtenerModulosUnicos());
            model.addAttribute("alumnos", alumnoService.obtenerTodosLosAlumnos());
            model.addAttribute("gruposActividad", Nota.GrupoActividad.values());

            // Mantener los valores de filtro
            model.addAttribute("moduloFiltro", modulo);
            model.addAttribute("alumnoIdFiltro", alumnoId);
            model.addAttribute("grupoFiltro", grupo);

            return "notas";
        } catch (Exception e) {
            e.printStackTrace(); // Añade esto para ver el stacktrace completo
            model.addAttribute("error", "Ocurrió un error al cargar las notas: " + e.getMessage());
            return "error"; // Cambia a una página de error genérica
        }
    }


    @GetMapping("/nueva")
    public String mostrarFormularioNuevaNota(Model model) {
        model.addAttribute("nota", new Nota());
        model.addAttribute("alumnos", alumnoService.obtenerTodosLosAlumnos());
        model.addAttribute("profesores", profesorService.obtenerTodosLosProfesores());
        model.addAttribute("gruposActividad", Nota.GrupoActividad.values());
        return "nueva-nota";
    }

    @PostMapping("/guardar")
    public String guardarNota(@ModelAttribute Nota nota, RedirectAttributes redirectAttributes) {
        notaService.guardarNota(nota);
        redirectAttributes.addFlashAttribute("mensaje", "Nota guardada con éxito");
        return "redirect:/notas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarNota(@PathVariable Long id, Model model) {
        Optional<Nota> nota = notaService.buscarPorId(id);
        if (nota.isPresent()) {
            model.addAttribute("nota", nota.get());
            model.addAttribute("alumnos", alumnoService.obtenerTodosLosAlumnos());
            model.addAttribute("profesores", profesorService.obtenerTodosLosProfesores());
            model.addAttribute("gruposActividad", Nota.GrupoActividad.values());
            return "nueva-nota";
        } else {
            return "redirect:/notas";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarNota(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notaService.eliminarNota(id);
        redirectAttributes.addFlashAttribute("mensaje", "Nota eliminada con éxito");
        return "redirect:/notas";
    }

    @GetMapping("/alumno/{alumnoid}")
    public String verNotasAlumno(@PathVariable Long alumnoId, Model model) {
        Optional<Alumno> alumnoOpt = alumnoService.findById(alumnoId);
        if (alumnoOpt.isPresent()) {
            Alumno alumno = alumnoOpt.get();
            List<Nota> notas = notaService.buscarPoralumno(alumno);

            // Agrupar notas por módulo
            Map<String, List<Nota>> notasPorModulo = notas.stream()
                    .collect(Collectors.groupingBy(Nota::getModulo));

            model.addAttribute("alumno", alumno);
            model.addAttribute("notasPorModulo", notasPorModulo);

            // Calcular resumen para cada módulo
            Map<String, Map<String, Object>> resumenesPorModulo = new java.util.HashMap<>();
            for (String modulo : notasPorModulo.keySet()) {
                Map<String, Object> resumen = notaService.obtenerResumenNotas(alumno, modulo);
                resumenesPorModulo.put(modulo, resumen);
            }

            model.addAttribute("resumenesPorModulo", resumenesPorModulo);

            return "notas/alumno";
        } else {
            return "redirect:/alumnos";
        }
    }

    @GetMapping("/profesor/{profesorId}")
    public String verNotasProfesor(@PathVariable Long profesorId, Model model) {
        Optional<Profesor> profesorOpt = profesorService.findById(profesorId);
        if (profesorOpt.isPresent()) {
            Profesor profesor = profesorOpt.get();
            List<Nota> notas = notaService.buscarPorProfesor(profesor);

            model.addAttribute("profesor", profesor);
            model.addAttribute("notas", notas);

            return "notas/profesor";
        } else {
            return "redirect:/profesores";
        }
    }

    @GetMapping("/modulo")
    public String mostrarInformePorModulo(@RequestParam String modulo, Model model) {
        // Obtener notas con relaciones cargadas
        List<Nota> notas = notaRepository.findByModuloWithAlumnoAndProfesor(modulo);

        // Agrupar por alumno y calcular estadísticas
        Map<String, Map<String, Object>> reporteAlumnos = new LinkedHashMap<>();

        notas.stream()
                .filter(nota -> nota.getAlumno() != null)
                .collect(Collectors.groupingBy(
                        nota -> nota.getAlumno().getNombres() + " " + nota.getAlumno().getApellidos(),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                notasAlumno -> {
                                    Map<String, Object> datos = new HashMap<>();

                                    // Datos básicos
                                    datos.put("notas", notasAlumno);

                                    // Cálculo de estadísticas
                                    DoubleSummaryStatistics stats = notasAlumno.stream()
                                            .mapToDouble(Nota::getValorNota)
                                            .summaryStatistics();

                                    datos.put("promedio",notaService.calcularPromedioPonderado(notasAlumno));
                                    datos.put("maxima", stats.getMax());
                                    datos.put("minima", stats.getMin());
                                    datos.put("total", stats.getCount());

                                    return datos;
                                }
                        )
                ))
                .forEach((nombreAlumno, datos) -> reporteAlumnos.put(nombreAlumno, datos));

        // Calcular resumen general
        Map<String, Object> resumenGeneral = new HashMap<>();
        if (!notas.isEmpty()) {
            DoubleSummaryStatistics stats = notas.stream()
                    .mapToDouble(Nota::getValorNota)
                    .summaryStatistics();

            resumenGeneral.put("totalAlumnos", reporteAlumnos.size());
            resumenGeneral.put("promedioGeneral", stats.getAverage());
            resumenGeneral.put("notaMaxima", stats.getMax());
            resumenGeneral.put("notaMinima", stats.getMin());
        }

        // Agregar al modelo
        model.addAttribute("modulo", modulo);
        model.addAttribute("reporteAlumnos", reporteAlumnos);
        model.addAttribute("resumenGeneral", resumenGeneral);

        return "informeNotasModulo";
    }




    @GetMapping("/informe/{alumnoId}/{modulo}")
    public String generarInformeNotas(@PathVariable Long alumnoId, @PathVariable String modulo, Model model) {
        Optional<Alumno> alumnoOpt = alumnoService.findById(alumnoId);
        if (alumnoOpt.isPresent()) {
            Alumno alumno = alumnoOpt.get();
            List<Nota> notas = notaService.buscarPorAlumnoYModulo(alumno, modulo);
            Map<String, Object> resumen = notaService.obtenerResumenNotas(alumno, modulo);

            model.addAttribute("alumno", alumno);
            model.addAttribute("modulo", modulo);
            model.addAttribute("notas", notas);
            model.addAttribute("resumen", resumen);

            return "notas/informe";
        } else {
            return "redirect:/alumnos";
        }

    }

    // API REST para obtener datos para AJAX
    @GetMapping("/api/alumno/{alumnoId}/modulo/{modulo}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerResumenNotasAPI(
            @PathVariable Long estudianteId,
            @PathVariable String modulo) {

        Optional<Alumno> alumnoOpt = alumnoService.findById(estudianteId);
        if (alumnoOpt.isPresent()) {
            Alumno alumno = alumnoOpt.get();
            Map<String, Object> resumen = notaService.obtenerResumenNotas(alumno, modulo);
            return ResponseEntity.ok(resumen);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}