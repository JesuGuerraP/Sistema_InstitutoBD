package com.atucsara.Sistema_InstitutoBD.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @Column(name = "modulo", nullable = false, length = 100)
    private String modulo;

    @Column(name = "nombre_actividad", nullable = false, length = 100)
    private String nombreActividad;

    @Column(name = "grupo_actividad", nullable = false)
    @Enumerated(EnumType.STRING)
    private GrupoActividad grupoActividad;

    @Column(name = "valor_nota", nullable = false)
    private Double valorNota;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // Enumeración para los grupos de actividades
    public enum GrupoActividad {
        ACTIVIDADES_1(30.0),
        ACTIVIDADES_2(30.0),
        EVALUACION_FINAL(40.0);

        private final Double porcentaje;

        GrupoActividad(Double porcentaje) {
            this.porcentaje = porcentaje;
        }

        public Double getPorcentaje() {
            return porcentaje;
        }
    }
    @PrePersist
    private void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nota nota = (Nota) o;
        return Objects.equals(id, nota.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


