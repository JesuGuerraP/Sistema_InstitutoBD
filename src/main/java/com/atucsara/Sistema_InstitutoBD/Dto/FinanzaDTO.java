package com.atucsara.Sistema_InstitutoBD.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinanzaDTO {
    private Long id;
    private Long alumnoId;
    private String alumnoNombres;
    private BigDecimal pagoInscripcion;
    private Integer descuento;
    private BigDecimal valorSemestre;
    private BigDecimal totalPagadoSemestre;
    private BigDecimal deudaTotalSemestre;
    private String estado;

    // Campos de pago
    private BigDecimal pago1;
    private BigDecimal pago2;
    private BigDecimal pago3;
    private BigDecimal pago4;
    private BigDecimal pago5;



    // Campos de fecha
    private LocalDateTime fechaCreacion; // Agregar este campo
    private LocalDateTime fechaActualizacion; // Agregar este campo
}