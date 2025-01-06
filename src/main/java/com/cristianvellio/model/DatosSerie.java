package com.cristianvellio.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosSerie(
                @JsonAlias("Title") String titulo,
                @JsonAlias("totalSeasons") String totalDeTemporadas,
                @JsonAlias("imdbRating") String calificacion) {

        public String getTitulo() {
                return titulo;
        }

        public String getCalificacion() {
                return calificacion;
        }

        public Integer getTotalDeTemporadasAsInteger() {
                try {
                        return Integer.parseInt(totalDeTemporadas.trim()); // Usa trim para eliminar espacios
                } catch (NumberFormatException e) {
                        System.err.println("Error al convertir totalDeTemporadas: " + e.getMessage());
                        return null;
                }
        }
}
