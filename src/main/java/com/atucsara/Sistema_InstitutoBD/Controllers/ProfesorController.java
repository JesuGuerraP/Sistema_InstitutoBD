package com.atucsara.Sistema_InstitutoBD.Controllers;

import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Profesor;
import com.atucsara.Sistema_InstitutoBD.services.ProfesorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/profesores")
public class ProfesorController {

    @Autowired
    private ProfesorService profesorService;

    // Listar todos los profesores
    @GetMapping
    public String listProfesores(Model model) {
        model.addAttribute("profesores", profesorService.findAll());
        model.addAttribute("activeMenu", "profesores");

        return "profesores"; // Vista que muestra la lista de profesores
    }

    @GetMapping("/detalles/{id}")
    @ResponseBody
    public ResponseEntity<Profesor> getProfesorDetails(@PathVariable Long id) {
        Optional<Profesor> profesor = profesorService.findById(id);
        if (profesor.isPresent()) {
            System.out.println("Profesor encontrado: " + profesor.get()); // Agrega esta línea de depuración
            return ResponseEntity.ok(profesor.get());
        }
        System.out.println("Profesor no encontrado con ID: " + id); // Agrega esta línea de depuración
        return ResponseEntity.notFound().build();
    }



    // Mostrar formulario para agregar un nuevo profesor
    @GetMapping("/nuevo")
    public String showAddForm(Model model) {
        model.addAttribute("profesor", new Profesor());
        return "nuevo-profesor"; // Vista para agregar un nuevo profesor
    }

    // Guardar nuevo profesor
    @PostMapping("/nuevo")
    public String addProfesor(@Valid @ModelAttribute("profesor") Profesor profesor, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "nuevo-profesor"; // Retorna al formulario si hay errores
        }
        profesorService.save(profesor);
        return "redirect:/profesores"; // Redirige a la lista de profesores
    }

    // Mostrar formulario para editar un profesor existente
    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Profesor> profesor = profesorService.findById(id);
        if (profesor.isPresent()) {
            model.addAttribute("profesor", profesor.get());
            return "editar-profesor"; // Vista para editar profesor
        } else {
            return "redirect:/profesores?error=No encontrado";
        }
    }

    // Actualizar profesor existente
    @PostMapping("/update/{id}")
    public String updateProfesor(@PathVariable Long id, @Valid @ModelAttribute("profesor") Profesor profesor, BindingResult result) {
        if (result.hasErrors()) {
            return "editar-profesor"; // Retorna al formulario si hay errores
        }
        try {
            profesorService.update(id, profesor);
            return "redirect:/profesores"; // Redirige a la lista de profesores
        } catch (IllegalArgumentException e) {
            return "redirect:/profesores?error=No encontrado";
        }
    }

    @PostMapping("/eliminar/{id}")  // Asegúrate que esta ruta coincida con la del JavaScript
    @ResponseBody  // Importante para devolver la respuesta JSON
    public ResponseEntity<?> deleteProfesor(@PathVariable Long id) {
        try {
            profesorService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el profesor");
        }
    }

    @GetMapping("/buscarProfesores")
    @ResponseBody
    public List<Profesor> buscarProfesores(@RequestParam("searchTerm") String searchTerm) {
        return profesorService.buscarProfesores(searchTerm);
    }
}
