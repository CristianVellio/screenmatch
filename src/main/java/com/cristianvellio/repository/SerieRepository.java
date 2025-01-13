package com.cristianvellio.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.cristianvellio.model.Categoria;
import com.cristianvellio.model.Episodio;
import com.cristianvellio.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

    List<Serie> findTop5ByOrderByCalificacionDesc();

    List<Serie> findByGenero(Categoria categoria);

    @Query("SELECT s FROM Serie s WHERE s.totalDeTemporadas <= :totalDeTemporadas AND s.calificacion >= :calificacion")
    List<Serie> seriesPorTemporadaYCalificacion(Integer totalDeTemporadas, Double calificacion);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")
    List<Episodio> episodiosPorNombre(String nombreEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.calificacion DESC LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);

    @Query("SELECT s FROM Serie s " + "JOIN s.episodios e " + "GROUP BY s "
            + "ORDER BY MAX (e.fechaEstreno) DESC LIMIT 5")
    List<Serie> lanzamientosRecientes();

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numeroDeTemporada")
    List<Episodio> obtenerTemporadaPorNumero(Long id, Long numeroDeTemporada);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.calificacion DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

}
