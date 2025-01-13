package com.cristianvellio.dto;

import com.cristianvellio.model.Categoria;

public record SerieDTO(Long id,
                String titulo,
                Integer totalDeTemporadas,
                Double calificacion,
                String poster,
                Categoria genero,
                String actores,
                String sinopsis) {

}
