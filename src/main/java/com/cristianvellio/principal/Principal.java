package com.cristianvellio.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.cristianvellio.config.Config;
import com.cristianvellio.model.DatosEpisodio;
import com.cristianvellio.model.DatosSerie;
import com.cristianvellio.model.DatosTemporada;
import com.cristianvellio.model.Episodio;
import com.cristianvellio.screenmatch.service.ConsumoApi;
import com.cristianvellio.screenmatch.service.ConvierteDatos;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String API_KEY = "apikey=" + Config.getApiKey();
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraElMenu() {
        System.out.println("Por favor, escriba el nombre de la serie que desea buscar:");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&" + API_KEY);

        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        // Busca los datos de todas las temporadas
        List<DatosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= datos.totalDeTemporadas(); i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + "&" + API_KEY);
            DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporada);
        }
        // temporadas.forEach(System.out::println);
        // Mostrar solo el titulo de los episodios de todas las temporadas
        for (int i = 0; i < datos.totalDeTemporadas(); i++) {
            List<DatosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporadas.size(); j++) {
                System.out.println(episodiosTemporadas.get(j).titulo());
            }
        }

        // mejora usando funciones lamda
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        // Top 5 Episodios con mayor calificación
        System.out.println("\n Top 5 Episodios:");
        datosEpisodios.stream()
                .filter(e -> !e.calificacion().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Primer filtro (N/A)" + e))
                .sorted(Comparator.comparing(DatosEpisodio::calificacion).reversed())
                .limit(5)
                .forEach(e -> System.out.println(e.titulo() + " - " + e.calificacion()));

        // Convirtiendo los datos a una Lista de tipo espidodios
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numeroDeTemporada(), d)))
                .collect(Collectors.toList());

        // episodios.forEach(System.out::println);

        // Busqueda Episodios a partir x año
        // System.out.println("Indica el año a partir del cual deseas buscar
        // episodios:");
        // var fecha = teclado.nextInt();
        // teclado.nextLine();

        // LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // episodios.stream()
        // .filter(e -> e.getFechaEstreno() != null
        // && e.getFechaEstreno().isAfter(fechaBusqueda))
        // .forEach(
        // e -> System.out.println("Temporada " + e.getTemporada() + " - Episodio " +
        // e.getNumeroEpisodio()
        // + ": " + e.getTitulo() + " - " + e.getCalificacion() + " - "
        // + e.getFechaEstreno().format(dtf)));

        // Busca episodios por parte del titulo
        // System.out.println("Indica una parte del titulo de los episodios que deseas
        // buscar:");
        // var parteTitulo = teclado.nextLine();
        // Optional<Episodio> episodioBuscado = episodios.stream()
        // .filter(e -> e.getTitulo().toUpperCase().contains(parteTitulo.toUpperCase()))
        // .findFirst();
        // if (episodioBuscado.isPresent()) {
        // System.out.println("Episodio encontrado: ");
        // System.out.println("La info es: " + episodioBuscado.get());
        // } else {
        // System.out.println("Episodio no encontrado");
        // }

        Map<Integer, Double> calificacionesPorTemporada = episodios.stream()
                .filter(e -> e.getCalificacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getCalificacion)));
        System.out.println(calificacionesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getCalificacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getCalificacion));
        System.out.println("El promedio de las Calificaciones es: " + est.getAverage());
        System.out.println("Episodio mejor calificado: " + est.getMax());
        System.out.println("Episodio peor calificado: " + est.getMin());
    }
}
