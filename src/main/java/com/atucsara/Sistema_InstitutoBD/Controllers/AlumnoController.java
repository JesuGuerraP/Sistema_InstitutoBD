package com.atucsara.Sistema_InstitutoBD.Controllers;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Profesor;
import com.atucsara.Sistema_InstitutoBD.services.AlumnoService;
import com.atucsara.Sistema_InstitutoBD.services.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private ProfesorService profesorService;

    // Ver detalles del alumno (endpoint para el modal)
    @GetMapping("/detalles/{id}")
    @ResponseBody
    public ResponseEntity<Alumno> getAlumnoDetails(@PathVariable Long id) {
        try {
            Optional<Alumno> alumno = alumnoService.findById(id);
            if (alumno.isPresent()) {
                System.out.println("Alumno encontrado: " + alumno.get());
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(alumno.get());
            }
            System.out.println("Alumno no encontrado con ID: " + id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error al obtener detalles del alumno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping
    public String listAlumnos(Model model) {
        model.addAttribute("alumnos", alumnoService.findAll());
        model.addAttribute("activeMenu", "alumnos");

        return "alumnos"; // Vista de listar alumnos
    }

    @GetMapping("/nuevo")
    public String showAddForm(Model model) {
        List<Profesor> profesores = profesorService.findAll(); // Obtener la lista de profesores
        model.addAttribute("alumno", new Alumno()); // Crear un objeto Alumno vacío
        model.addAttribute("profesores", profesores); // Enviar la lista de profesores al formulario
        return "nuevo-alumno"; // Vista para agregar alumno
    }

    @PostMapping("/save")
    public String saveAlumno(@ModelAttribute Alumno alumno, @RequestParam Long profesorId) {
        // Verifica que se reciba un ID de profesor válido
        if (profesorId == null || profesorId <= 0) {
            return "redirect:/alumnos/nuevo?error=No se puede guardar sin un profesor";
        }

        Optional<Profesor> profesorOpt = profesorService.findById(profesorId);
        if (profesorOpt.isPresent()) {
            alumno.setProfesor(profesorOpt.get());
            alumnoService.save(alumno);
        } else {
            return "redirect:/alumnos/nuevo?error=Profesor no encontrado";
        }

        return "redirect:/alumnos";
    }




    @GetMapping("/update/{id}")
    public String editAlumno(@PathVariable Long id, Model model) {
        Optional<Alumno> alumnoOpt = alumnoService.findById(id);
        if (!alumnoOpt.isPresent()) {
            return "redirect:/alumnos"; // Redirigir si el alumno no existe
        }

        Alumno alumno = alumnoOpt.get();
        List<Profesor> profesores = profesorService.findAll(); // Obtener la lista de profesores

        model.addAttribute("alumno", alumno);
        model.addAttribute("profesores", profesores);

        return "editar-alumno"; // Vista de edición
    }

    @PostMapping("/update/{id}")
    public String updateAlumno(@PathVariable Long id, @ModelAttribute Alumno alumno) {
        Optional<Alumno> existingAlumnoOpt = alumnoService.findById(id);
        if (existingAlumnoOpt.isPresent()) {
            Alumno existingAlumno = existingAlumnoOpt.get();

            // Se actualizan los campos con los valores del formulario
            existingAlumno.setNombres(alumno.getNombres());
            existingAlumno.setApellidos(alumno.getApellidos());
            existingAlumno.setIdentificacion(alumno.getIdentificacion());
            existingAlumno.setGenero(alumno.getGenero());
            existingAlumno.setCurso(alumno.getCurso());
            existingAlumno.setNivelAcademico(alumno.getNivelAcademico());
            existingAlumno.setSemestre(alumno.getSemestre());
            existingAlumno.setCorreoElectronico(alumno.getCorreoElectronico());
            existingAlumno.setDireccion(alumno.getDireccion());
            existingAlumno.setFechaIngreso(alumno.getFechaIngreso());
            existingAlumno.setTelefono(alumno.getTelefono());

            // Verificar que se seleccione un profesor
            if (alumno.getProfesor() != null && alumno.getProfesor().getId() != null) {
                Optional<Profesor> profesorOpt = profesorService.findById(alumno.getProfesor().getId());
                if (profesorOpt.isPresent()) {
                    existingAlumno.setProfesor(profesorOpt.get()); // Asignar el profesor si existe
                } else {
                    // Manejar el caso de error: no se puede actualizar sin un profesor
                    return "redirect:/alumnos/update/" + id; // Redirigir a la vista de edición
                }
            } else {
                // Manejar el caso de error: no se puede actualizar sin un profesor
                return "redirect:/alumnos/update/" + id; // Redirigir a la vista de edición
            }

            alumnoService.save(existingAlumno); // Guardar cambios
        }
        return "redirect:/alumnos"; // Redirige a la lista de alumnos
    }
    @PostMapping("/delete/{id}")
    public String deleteAlumno(@PathVariable Long id) {
        Optional<Alumno> alumno = alumnoService.findById(id);  // Comprobar si el alumno existe antes de eliminar
        if (alumno.isPresent()) {
            alumnoService.delete(id);
        }
        return "redirect:/alumnos"; // Redirecciona después de eliminar
    }

    @GetMapping("/buscarAlumnos")
    @ResponseBody
    public List<Alumno> buscarAlumnos(@RequestParam("searchTerm") String searchTerm) {
        return alumnoService.buscarAlumnos(searchTerm);
    }

}



