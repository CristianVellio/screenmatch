package com.cristianvellio.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.persistence.*;

@Entity
@Table(name = "episodios")
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double calificacion;
    private LocalDate fechaEstreno;

    @ManyToOne
    private Serie serie;

    public Episodio() {
    }

    public Episodio(Integer numeroDeTemporada, DatosEpisodio d) {
        this.temporada = numeroDeTemporada;
        this.titulo = d.titulo();
        this.numeroEpisodio = d.numeroEpisodio();
        try {
            this.calificacion = d.calificacion().equalsIgnoreCase("N/A") ? 0.0 : Double.valueOf(d.calificacion());
        } catch (NumberFormatException e) {
            this.calificacion = 0.0;
        }
        try {
            this.fechaEstreno = d.fechaDeEstreno() == null || d.fechaDeEstreno().isEmpty() ? null
                    : LocalDate.parse(d.fechaDeEstreno());
        } catch (DateTimeParseException e) {
            this.fechaEstreno = null;
        }
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public LocalDate getFechaEstreno() {
        return fechaEstreno;
    }

    public void setFechaEstreno(LocalDate fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    @Override
    public String toString() {
        return "temporada=" + temporada +
                ", titulo=" + titulo + '\'' +
                ", numeroEpisodio=" + numeroEpisodio +
                ", calificacion=" + calificacion +
                ", fechaEstreno=" + fechaEstreno;
    }
}
