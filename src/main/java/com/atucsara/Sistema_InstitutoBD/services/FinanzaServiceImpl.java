package com.atucsara.Sistema_InstitutoBD.services;

import com.atucsara.Sistema_InstitutoBD.Dto.FinanzaDTO;
import com.atucsara.Sistema_InstitutoBD.exeptions.ResourceNotFoundException;
import com.atucsara.Sistema_InstitutoBD.models.Finanza;
import com.atucsara.Sistema_InstitutoBD.repositories.AlumnoRepository;
import com.atucsara.Sistema_InstitutoBD.repositories.FinanzaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanzaServiceImpl implements FinanzaService {

    private final FinanzaRepository finanzaRepository;
    private final AlumnoRepository alumnoRepository;

    @Override
    public List<FinanzaDTO> findAll() {
        return finanzaRepository.findAll().stream()
                .peek(Finanza::calcularValores) // Recalcular valores si es necesario
                .map(this::convertToDTO) // Convertir a DTO
                .collect(Collectors.toList());
    }


    @Override
    public FinanzaDTO findById(Long id) {
        return finanzaRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Finanza no encontrada"));
    }
    @Override
    public Finanza save(@Valid Finanza finanza) {
        // Si hay un alumno asociado, verificar que exista en la base de datos
        if (finanza.getAlumno() != null && finanza.getAlumno().getId() != null) {
            alumnoRepository.findById(finanza.getAlumno().getId())
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        }

        // Calcular los valores de la finanza antes de guardar
        finanza.calcularValores();

        // Guardar la finanza en la base de datos
        return finanzaRepository.save(finanza);
    }

    private static final Logger log = LoggerFactory.getLogger(FinanzaServiceImpl.class);



    @Override
    public void desvincularAlumnoDeFinanza(Long finanzaId) {
        Finanza finanza = finanzaRepository.findById(finanzaId)
                .orElseThrow(() -> new ResourceNotFoundException("Finanza no encontrada"));

        finanza.setAlumno(null); // Desvincular el alumno
        finanzaRepository.save(finanza); // Guardar los cambios
        log.info("Alumno desvinculado de la finanza con ID: {}", finanzaId);
    }

    @Override
    public boolean existsById(Long id) {
        return finanzaRepository.existsById(id);
    }


    @Override
    @Transactional
    public FinanzaDTO create(FinanzaDTO finanzaDTO) {
        Finanza finanza = convertToEntity(finanzaDTO);
        finanza.setFechaCreacion(LocalDateTime.now());
        finanza.calcularValores(); // Calcular valores al crear

        // Si no hay alumno asociado, asegúrate de que finanza.setAlumno sea null
        if (finanzaDTO.getAlumnoId() == null) {
            finanza.setAlumno(null);
        }

        finanza = finanzaRepository.save(finanza);
        return convertToDTO(finanza);
    }


    @Override
    @Transactional
    public FinanzaDTO update(Long id, FinanzaDTO finanzaDTO) {
        Finanza finanza = finanzaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finanza no encontrada"));

        // Actualizar la entidad con los datos del DTO
        updateEntityFromDTO(finanza, finanzaDTO);

        // Actualizar la fecha de actualización
        finanza.setFechaActualizacion(LocalDateTime.now());

        // Calcular valores
        finanza.calcularValores();

        // Guardar la entidad actualizada
        finanza = finanzaRepository.save(finanza);

        // Verificar si la entidad se guardó correctamente
        if (finanza.getId() != null) {
            return convertToDTO(finanza);
        } else {
            throw new RuntimeException("No se pudo guardar la finanza actualizada");
        }
    }





    @Override
    @Transactional
    public void delete(Long id) {
        try {
            // Obtener la finanza primero
            Finanza finanza = finanzaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Finanza no encontrada con ID: " + id));

            // Desvincular alumno si existe
            if (finanza.getAlumno() != null) {
                finanza.setAlumno(null);
                finanzaRepository.save(finanza);
                log.info("Alumno desvinculado de la finanza con ID: {}", id);
            }

            // Eliminar finanza
            finanzaRepository.deleteById(id);
            log.info("Finanza eliminada con ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad referencial al eliminar la finanza con ID: {}", id, e);
            throw new DataIntegrityViolationException("La finanza tiene dependencias que impiden su eliminación.", e);
        } catch (Exception e) {
            log.error("Error al eliminar la finanza con ID: {}", id, e);
            throw new RuntimeException("No se pudo eliminar la finanza.", e);
        }
    }

    @Override
    public List<FinanzaDTO> findByEstado(String estado) {
        return finanzaRepository.findByEstado(Finanza.EstadoPago.valueOf(estado))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FinanzaDTO> findByAlumnoId(Long alumnoId) {
        return finanzaRepository.findByAlumnoId(alumnoId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FinanzaDTO convertToDTO(Finanza finanza) {
        FinanzaDTO dto = new FinanzaDTO();
        dto.setId(finanza.getId());
        if (finanza.getAlumno() != null) {
            dto.setAlumnoId(finanza.getAlumno().getId());
            dto.setAlumnoNombres(finanza.getAlumno().getNombres());
        }
        dto.setPagoInscripcion(finanza.getPagoInscripcion());
        dto.setDescuento(finanza.getDescuento());
        dto.setValorSemestre(finanza.getValorSemestre());
        dto.setTotalPagadoSemestre(finanza.getTotalPagadoSemestre());
        dto.setDeudaTotalSemestre(finanza.getDeudaTotalSemestre());
        dto.setEstado(finanza.getEstado().name());
        dto.setPago1(finanza.getPago1());
        dto.setPago2(finanza.getPago2());
        dto.setPago3(finanza.getPago3());
        dto.setPago4(finanza.getPago4());
        dto.setPago5(finanza.getPago5());
        dto.setFechaCreacion(finanza.getFechaCreacion());
        dto.setFechaActualizacion(finanza.getFechaActualizacion());

        return dto;
    }

    private Finanza convertToEntity(FinanzaDTO dto) {
        Finanza finanza = new Finanza();
        finanza.setAlumno(alumnoRepository.findById(dto.getAlumnoId())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado")));
        updateEntityFromDTO(finanza, dto);
        return finanza;
    }

    private void updateEntityFromDTO(Finanza finanza, FinanzaDTO dto) {
        // Actualizar solo los campos editables desde el formulario
        finanza.setPagoInscripcion(dto.getPagoInscripcion());
        finanza.setDescuento(dto.getDescuento());

        // Actualizar los pagos
        finanza.setPago1(dto.getPago1());
        finanza.setPago2(dto.getPago2());
        finanza.setPago3(dto.getPago3());
        finanza.setPago4(dto.getPago4());
        finanza.setPago5(dto.getPago5());

        // Recalcular los valores automáticamente
        finanza.calcularValores();

        // Conservar el estado actual si no viene en el DTO o está vacío
        if (dto.getEstado() == null || dto.getEstado().isEmpty()) {
            // No cambiar el estado, mantener el actual
        } else {
            try {
                finanza.setEstado(Finanza.EstadoPago.valueOf(dto.getEstado()));
            } catch (IllegalArgumentException e) {
                // Manejar el caso en que el valor no sea válido
                throw new IllegalArgumentException("Estado inválido: " + dto.getEstado());
            }
        }
    }


public List<Finanza> buscarFinanzas(String searchtTerm){
        return finanzaRepository.findAll().stream()
                .filter(finanza -> finanza.getAlumno().getNombres().toLowerCase().contains(searchtTerm.toLowerCase())
                        || finanza.getAlumno().getApellidos().toLowerCase().contains(searchtTerm.toLowerCase())
                        || finanza.getEstado().name().toLowerCase().contains(searchtTerm.toLowerCase())).collect(Collectors.toList());
}




}