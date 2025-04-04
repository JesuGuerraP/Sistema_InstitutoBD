package com.atucsara.Sistema_InstitutoBD.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "finanzas")
public class Finanza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true) // Permitir que el alumno sea null
    @JoinColumn(name = "alumno_id", nullable = true)
    private Alumno alumno;

    @Column(nullable = false)
    private Integer descuento;

    @Column(nullable = false)
    private BigDecimal valorSemestre;

    @Column(nullable = false)
    private BigDecimal pagoInscripcion;

    @Column(name = "pago_1")
    private BigDecimal pago1;

    @Column(name = "pago_2")
    private BigDecimal pago2;

    @Column(name = "pago_3")
    private BigDecimal pago3;

    @Column(name = "pago_4")
    private BigDecimal pago4;

    @Column(name = "pago_5")
    private BigDecimal pago5;

    @Column(nullable = false)
    private BigDecimal totalPagadoSemestre;

    @Column(nullable = false)
    private BigDecimal deudaTotalSemestre;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        inicializarPagos();
        calcularValores();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
        calcularValores();
    }

    private void inicializarPagos() {
        pago1 = pago1 != null ? pago1 : BigDecimal.ZERO;
        pago2 = pago2 != null ? pago2 : BigDecimal.ZERO;
        pago3 = pago3 != null ? pago3 : BigDecimal.ZERO;
        pago4 = pago4 != null ? pago4 : BigDecimal.ZERO;
        pago5 = pago5 != null ? pago5 : BigDecimal.ZERO;
    }

    public void calcularValores() {
        // Inicializar pagos si son null
        inicializarPagos();

        // Calcular valor del semestre con descuento
        BigDecimal valorBase = new BigDecimal("200000");
        BigDecimal porcentajeDescuento = new BigDecimal(descuento).divide(new BigDecimal("100"));
        valorSemestre = valorBase.multiply(BigDecimal.ONE.subtract(porcentajeDescuento));

        // Calcular total pagado
        totalPagadoSemestre = pago1
                .add(pago2)
                .add(pago3)
                .add(pago4)
                .add(pago5);

        // Calcular deuda total
        deudaTotalSemestre = valorSemestre.subtract(totalPagadoSemestre);

        // Actualizar estado
        actualizarEstado();
    }

    private void actualizarEstado() {
        if (deudaTotalSemestre.compareTo(BigDecimal.ZERO) == 0) {
            estado = EstadoPago.PAGADO;
        } else if (deudaTotalSemestre.compareTo(valorSemestre) == 0) {
            estado = EstadoPago.PENDIENTE;
        } else {
            estado = EstadoPago.PARCIAL;
        }
    }

    public enum EstadoPago {
        PAGADO, PENDIENTE, PARCIAL
    }

    // Constructor por defecto
    public Finanza() {
        this.descuento = 0;
        this.pagoInscripcion = BigDecimal.ZERO;
        inicializarPagos();
    }
}