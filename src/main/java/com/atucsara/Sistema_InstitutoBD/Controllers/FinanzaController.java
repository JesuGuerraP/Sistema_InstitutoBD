package com.atucsara.Sistema_InstitutoBD.Controllers;

import com.atucsara.Sistema_InstitutoBD.Dto.FinanzaDTO;
import com.atucsara.Sistema_InstitutoBD.models.Alumno;
import com.atucsara.Sistema_InstitutoBD.models.Finanza;
import com.atucsara.Sistema_InstitutoBD.repositories.AlumnoRepository;
import com.atucsara.Sistema_InstitutoBD.services.AlumnoService;
import com.atucsara.Sistema_InstitutoBD.services.FinanzaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/finanzas")
@RequiredArgsConstructor
public class FinanzaController {

    private final FinanzaService finanzaService;
    private final AlumnoService alumnoService;
    private final AlumnoRepository alumnoRepository;
    private static final Logger log = LoggerFactory.getLogger(FinanzaController.class);

    // Listar todas las finanzas
    @GetMapping
    public String listarFinanzas(Model model) {
        model.addAttribute("finanzas", finanzaService.findAll());
        model.addAttribute("alumnos", alumnoService.findAll());
        return "finanzas";
    }

    // Obtener una finanza por ID
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<FinanzaDTO> obtenerFinanza(@PathVariable Long id) {
        return ResponseEntity.ok(finanzaService.findById(id));
    }

    // Crear una nueva finanza
    @GetMapping("/new")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("finanza", new Finanza());
        model.addAttribute("alumnos", alumnoService.findAll());
        return "crear-finanza";
    }

    @PostMapping("/save")
    public String saveFinanza(@ModelAttribute Finanza finanza) {
        // Verificar si se ha seleccionado un alumno
        if (finanza.getAlumno() != null && finanza.getAlumno().getId() != null) {
            // Buscar el alumno por ID
            Long alumnoId = finanza.getAlumno().getId();
            Alumno alumno = alumnoRepository.findById(alumnoId)
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

            // Asociar el alumno a la finanza
            finanza.setAlumno(alumno);
        } else {
            // Si no se selecciona un alumno, asegúrate de que finanza.setAlumno sea null
            finanza.setAlumno(null);
        }

        // Validar o asignar la fecha de creación si no está presente
        if (finanza.getFechaCreacion() == null) {
            finanza.setFechaCreacion(LocalDateTime.now());
        }

        // Guardar la finanza
        finanzaService.save(finanza);

        // Redirigir a la lista de finanzas
        return "redirect:/finanzas";
    }



    // Eliminar una finanza por ID


    // Filtrar finanzas por estado o alumnoId
    @GetMapping("/filtrar")
    @ResponseBody
    public ResponseEntity<List<FinanzaDTO>> filtrarFinanzas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long alumnoId) {
        if (estado != null) {
            return ResponseEntity.ok(finanzaService.findByEstado(estado));
        } else if (alumnoId != null) {
            return ResponseEntity.ok(finanzaService.findByAlumnoId(alumnoId));
        }
        return ResponseEntity.ok(finanzaService.findAll());
    }

    // Método adicional para obtener finanzas por alumnoId
    @GetMapping("/alumno/{alumnoId}")
    @ResponseBody
    public ResponseEntity<List<FinanzaDTO>> obtenerFinanzasPorAlumno(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(finanzaService.findByAlumnoId(alumnoId));
    }

    // Mostrar formulario para editar una finanza
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        FinanzaDTO finanza = finanzaService.findById(id);
        if (finanza == null) {
            return "redirect:/finanzas?error=notfound";
        }
        model.addAttribute("finanza", finanza);
        model.addAttribute("alumnos", alumnoService.findAll());
        return "editar-finanza";
    }

    @PostMapping("/editar")
    public String actualizarFinanza(@ModelAttribute FinanzaDTO finanzaDTO, BindingResult bindingResult, Model model) {
        log.info("Iniciando actualización de finanza con ID: {}", finanzaDTO.getId());
        log.info("Datos recibidos: {}", finanzaDTO);

        // Verificar si el ID es nulo
        if (finanzaDTO.getId() == null) {
            log.error("ID de finanza es nulo");
            return "redirect:/finanzas?error=missingid";
        }

        // Verificar el estado
        if (finanzaDTO.getEstado() == null || finanzaDTO.getEstado().isEmpty()) {
            log.error("Estado de finanza es nulo o vacío");
            bindingResult.rejectValue("estado", "error.estado", "El estado no puede ser nulo o vacío");
            model.addAttribute("finanza", finanzaDTO);
            model.addAttribute("alumnos", alumnoService.findAll());
            return "editar-finanza";
        }

        // Validar que el estado sea un valor válido del enum
        try {
            Finanza.EstadoPago.valueOf(finanzaDTO.getEstado());
        } catch (IllegalArgumentException e) {
            log.error("Estado inválido: {}", finanzaDTO.getEstado());
            bindingResult.rejectValue("estado", "error.estado", "Estado inválido: " + finanzaDTO.getEstado());
            model.addAttribute("finanza", finanzaDTO);
            model.addAttribute("alumnos", alumnoService.findAll());
            return "editar-finanza";
        }

        // Si hay errores de validación, devolver a la vista de edición
        if (bindingResult.hasErrors()) {
            log.error("Errores de validación: {}", bindingResult.getAllErrors());
            model.addAttribute("finanza", finanzaDTO);
            model.addAttribute("alumnos", alumnoService.findAll());
            return "editar-finanza";
        }

        try {
            // Llamar al servicio para actualizar
            log.info("Llamando al servicio para actualizar la finanza");
            FinanzaDTO finanzaActualizada = finanzaService.update(finanzaDTO.getId(), finanzaDTO);
            log.info("Finanza actualizada: {}", finanzaActualizada);

            return "redirect:/finanzas?success=updated";
        } catch (Exception e) {
            log.error("Error al actualizar la finanza: ", e);
            model.addAttribute("error", "Error al actualizar: " + e.getMessage());
            model.addAttribute("finanza", finanzaDTO);
            model.addAttribute("alumnos", alumnoService.findAll());
            return "editar-finanza";
        }
    }

    // Método para obtener detalles de una finanza específica (para el modal)
    @GetMapping("/detalles/{id}")
    public ResponseEntity<FinanzaDTO> verDetallesFinanza(@PathVariable Long id) {
        try {
            FinanzaDTO finanzaDTO = finanzaService.findById(id);
            if (finanzaDTO == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(finanzaDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarFinanza(@PathVariable Long id) {
        try {
            finanzaService.delete(id);
            return ResponseEntity.ok().body("Finanza eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo eliminar la finanza: " + e.getMessage());
        }
    }



    @PatchMapping("/finanza/desvincular/{id}")
    public ResponseEntity<Void> desvincularAlumno(@PathVariable Long id) {
        finanzaService.desvincularAlumnoDeFinanza(id);
        return ResponseEntity.noContent().build(); // Respuesta 204 No Content
    }

    // Otros métodos...
}





