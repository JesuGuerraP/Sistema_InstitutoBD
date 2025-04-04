package com.atucsara.Sistema_InstitutoBD.models;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombres;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellidos;

    @NotBlank(message = "La identificación no puede estar vacía")
    @Pattern(regexp = "\\d+", message = "La identificación solo puede contener números")
    private String identificacion;

    @NotBlank(message = "El género no puede estar vacío")
    private String genero;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String direccion;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "\\d{7,15}", message = "El teléfono debe tener entre 7 y 15 dígitos numéricos")
    private String telefono;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico no es válido")
    private String correoElectronico;

    @NotNull(message = "La fecha de ingreso no puede ser nula")
    private LocalDate fechaIngreso;

    @NotBlank(message = "El nivel académico no puede estar vacío")
    private String nivelAcademico;

    @NotBlank(message = "El curso no puede estar vacío")
    private String curso;

    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @NotBlank(message = "El semestre no puede estar vacío")
    private String semestre;


    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "El nombre no puede estar vacío") String getNombres() {
        return nombres;
    }

    public void setNombres(@NotBlank(message = "El nombre no puede estar vacío") String nombres) {
        this.nombres = nombres;
    }

    public @NotBlank(message = "El apellido no puede estar vacío") String getApellidos() {
        return apellidos;
    }

    public void setApellidos(@NotBlank(message = "El apellido no puede estar vacío") String apellidos) {
        this.apellidos = apellidos;
    }

    public @NotBlank(message = "La identificación no puede estar vacía") @Pattern(regexp = "\\d+", message = "La identificación solo puede contener números") String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(@NotBlank(message = "La identificación no puede estar vacía") @Pattern(regexp = "\\d+", message = "La identificación solo puede contener números") String identificacion) {
        this.identificacion = identificacion;
    }

    public @NotBlank(message = "El género no puede estar vacío") String getGenero() {
        return genero;
    }

    public void setGenero(@NotBlank(message = "El género no puede estar vacío") String genero) {
        this.genero = genero;
    }

    public @NotBlank(message = "La dirección no puede estar vacía") String getDireccion() {
        return direccion;
    }

    public void setDireccion(@NotBlank(message = "La dirección no puede estar vacía") String direccion) {
        this.direccion = direccion;
    }

    public @NotBlank(message = "El teléfono no puede estar vacío") @Pattern(regexp = "\\d{7,15}", message = "El teléfono debe tener entre 7 y 15 dígitos numéricos") String getTelefono() {
        return telefono;
    }

    public void setTelefono(@NotBlank(message = "El teléfono no puede estar vacío") @Pattern(regexp = "\\d{7,15}", message = "El teléfono debe tener entre 7 y 15 dígitos numéricos") String telefono) {
        this.telefono = telefono;
    }

    public @NotBlank(message = "El correo electrónico no puede estar vacío") @Email(message = "El correo electrónico no es válido") String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(@NotBlank(message = "El correo electrónico no puede estar vacío") @Email(message = "El correo electrónico no es válido") String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public @NotNull(message = "La fecha de ingreso no puede ser nula") LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(@NotNull(message = "La fecha de ingreso no puede ser nula") LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public @NotBlank(message = "El nivel académico no puede estar vacío") String getNivelAcademico() {
        return nivelAcademico;
    }

    public void setNivelAcademico(@NotBlank(message = "El nivel académico no puede estar vacío") String nivelAcademico) {
        this.nivelAcademico = nivelAcademico;
    }

    public @NotBlank(message = "El curso no puede estar vacío") String getCurso() {
        return curso;
    }

    public void setCurso(@NotBlank(message = "El curso no puede estar vacío") String curso) {
        this.curso = curso;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public @NotBlank(message = "El semestre no puede estar vacío") String getSemestre() {
        return semestre;
    }

    public void setSemestre(@NotBlank(message = "El semestre no puede estar vacío") String semestre) {
        this.semestre = semestre;
    }
}

