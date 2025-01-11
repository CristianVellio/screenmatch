package com.cristianvellio.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.cristianvellio.config.Config;
import com.cristianvellio.model.DatosSerie;
import com.cristianvellio.model.DatosTemporada;
import com.cristianvellio.model.Serie;
import com.cristianvellio.repository.SerieRepository;
import com.cristianvellio.screenmatch.service.ConsumoApi;
import com.cristianvellio.screenmatch.service.ConvierteDatos;

public class Principal {
        private Scanner teclado = new Scanner(System.in);
        private ConsumoApi consumoApi = new ConsumoApi();
        private final String API_KEY = "apikey=" + Config.getApiKey();
        private final String URL_BASE = "http://www.omdbapi.com/?t=";
        private ConvierteDatos conversor = new ConvierteDatos();
        private List<DatosSerie> datosSeries = new ArrayList<>();
        private SerieRepository repositorio;

        public Principal(SerieRepository repository) {
                this.repositorio = repository;
        }

        public void muestraElMenu() {
                var opcion = -1;
                while (opcion != 0) {
                        var menu = """
                                        1 - Buscar series
                                        2 - Buscar episodios
                                        3 - Mostrar series buscadas

                                        0 - Salir
                                        """;
                        System.out.println(menu);
                        opcion = teclado.nextInt();
                        teclado.nextLine();

                        switch (opcion) {
                                case 1:
                                        buscarSerieWeb();
                                        break;
                                case 2:
                                        buscarEpisodioPorSerie();
                                        break;
                                case 3:
                                        mostrarSeriesBuscadas();
                                        break;
                                case 0:
                                        System.out.println("Cerrando la aplicación...");
                                        break;
                                default:
                                        System.out.println("Opción inválida");
                        }
                }

        }

        private DatosSerie getDatosSerie() {
                System.out.println("Escribe el nombre de la serie que deseas buscar");
                var nombreSerie = teclado.nextLine();
                var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&" + API_KEY);
                System.out.println(json);
                DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
                return datos;
        }

        private void buscarEpisodioPorSerie() {
                DatosSerie datosSerie = getDatosSerie();
                List<DatosTemporada> temporadas = new ArrayList<>();

                for (int i = 1; i <= datosSerie.totalDeTemporadas(); i++) {
                        var json = consumoApi.obtenerDatos(
                                        URL_BASE + datosSerie.titulo().replace(" ", "+") + "&Season=" + i + "&"
                                                        + API_KEY);
                        DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
                        temporadas.add(datosTemporada);
                }
                temporadas.forEach(System.out::println);
        }

        private void buscarSerieWeb() {
                DatosSerie datos = getDatosSerie();
                Serie serie = new Serie(datos);
                repositorio.save(serie);
                // datosSeries.add(datos);
                System.out.println(datos);
        }

        private void mostrarSeriesBuscadas() {
                List<Serie> series = repositorio.findAll();

                series.stream()
                                .sorted(Comparator.comparing(Serie::getGenero))
                                .forEach(System.out::println);
        }
}
// // Busca los datos de todas las temporadas
// List<DatosTemporada> temporadas = new ArrayList<>();

// for(
// int i = 1;i<=datos.totalDeTemporadas();i++)
// {
// json = consumoApi.obtenerDatos(
// URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + "&" + API_KEY);
// DatosTemporada datosTemporada = conversor.obtenerDatos(json,
// DatosTemporada.class);
// temporadas.add(datosTemporada);
// }
// temporadas.forEach(System.out::println);
// Mostrar solo el titulo de los episodios de todas las temporadas
// for(
// int i = 0;i<datos.totalDeTemporadas();i++)
// {
// List<DatosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
// for (int j = 0; j < episodiosTemporadas.size(); j++) {
// System.out.println(episodiosTemporadas.get(j).titulo());
// }
// }

// // mejora usando funciones lamda
// temporadas.forEach(t->t.episodios().forEach(e->System.out.println(e.titulo())));

// List<DatosEpisodio> datosEpisodios = temporadas.stream()
// .flatMap(t -> t.episodios().stream())
// .collect(Collectors.toList());

// // Top 5 Episodios con mayor calificación
// System.out.println("\n Top 5
// Episodios:");datosEpisodios.stream().filter(e->!e.calificacion().equalsIgnoreCase("N/A")).peek(e->System.out.println("Primer
// filtro
// (N/A)"+e)).sorted(Comparator.comparing(DatosEpisodio::calificacion).reversed()).limit(5).forEach(e->System.out.println(e.titulo()+"
// - "+e.calificacion()));

// Convirtiendo los datos a una Lista de tipo espidodios
// List<Episodio> episodios = temporadas.stream()
// .flatMap(t -> t.episodios().stream()
// .map(d -> new Episodio(t.numeroDeTemporada(), d)))
// .collect(Collectors.toList());

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
// System.out.println("Indica una parte del titulo de los episodios que
// deseas
// buscar:");
// var parteTitulo = teclado.nextLine();
// Optional<Episodio> episodioBuscado = episodios.stream()
// .filter(e ->
// e.getTitulo().toUpperCase().contains(parteTitulo.toUpperCase()))
// .findFirst();
// if (episodioBuscado.isPresent()) {
// System.out.println("Episodio encontrado: ");
// System.out.println("La info es: " + episodioBuscado.get());
// } else {
// System.out.println("Episodio no encontrado");
// }

// Map<Integer, Double> calificacionesPorTemporada = episodios.stream()
// .filter(e -> e.getCalificacion() > 0.0)
// .collect(Collectors.groupingBy(Episodio::getTemporada,
// Collectors.averagingDouble(
// Episodio::getCalificacion)));System.out.println(calificacionesPorTemporada);

// DoubleSummaryStatistics est = episodios.stream()
// .filter(e -> e.getCalificacion() > 0.0)
// .collect(Collectors.summarizingDouble(
// Episodio::getCalificacion));System.out.println("El promedio de las
// Calificaciones es: "+est.getAverage());System.out.println("Episodio mejor
// calificado: "+est.getMax());System.out.println("Episodio peor calificado:
// "+est.getMin());
// }}
