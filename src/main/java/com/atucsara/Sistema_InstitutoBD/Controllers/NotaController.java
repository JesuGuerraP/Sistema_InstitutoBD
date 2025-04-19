package com.atucsara.Sistema_InstitutoBD.Controllers;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Nota;
import com.atucsara.Sistema_InstitutoBD.models.Profesor;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/notas")
public class NotaController {

    private final NotaService notaService;
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;

    @Autowired
    public NotaController(NotaService notaService, AlumnoService alumnoService, ProfesorService profesorService) {
        this.notaService = notaService;
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
    }


    @GetMapping
    public String listarNotas(Model model) {
        List<Nota> notas =notaService.buscarPorModulo("");
        model.addAttribute("notas", notaService.buscarPorModulo(""));
        return "notas";
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
            model.addAttribute("estudiantes", alumnoService.obtenerTodosLosAlumnos());
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
        // 1. Buscar las notas por el módulo ingresado (necesitarás implementar este método en tu NotaService)
        List<Nota> notas = notaService.buscarPorModulo(modulo);

        // 2. Si encontraste notas, podrías intentar obtener información del alumno asociado
        //    Esto dependerá de cómo esté estructurada tu base de datos y cómo se relacionan las notas con los alumnos.
        //    Una nota, ¿siempre pertenece a un único alumno? Si es así, podrías obtener el primer alumno de la lista de notas (si no está vacía).
        Alumno alumno = null;
        if (!notas.isEmpty()) {
            // Asumiendo que cada nota tiene una referencia a un alumno
            alumno = notas.get(0).getAlumno(); // Necesitas tener el método getAlumno() en tu clase Nota
            // Si hay múltiples alumnos con notas en este módulo, tendrías que decidir cuál mostrar o cómo manejarlos.
        }

        // 3. Pasar el alumno y las notas al modelo
        model.addAttribute("alumno", alumno);
        model.addAttribute("moduloBuscado", modulo); // Opcional, para mostrar el módulo buscado en la vista
        model.addAttribute("notas", notas);

        // 4. Devolver el nombre de la vista que quieres mostrar (tu archivo HTML)
        System.out.println("Se está retornando la vista: informeNotasModulo");
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