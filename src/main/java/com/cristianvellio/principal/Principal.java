package com.cristianvellio.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.cristianvellio.config.Config;
import com.cristianvellio.model.Categoria;
import com.cristianvellio.model.DatosSerie;
import com.cristianvellio.model.DatosTemporada;
import com.cristianvellio.model.Episodio;
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
        private List<Serie> series;
        Optional<Serie> serieBuscada;

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
                                            4 - Mostrar Series por Titulo
                                            5 - Mostrar Top 5 Mejores Series
                                            6 - Buscar Series por Categoria
                                            7 - Buscar Series por Temporada y Calificación
                                            8 - Buscar Episodios por Titulo
                                            9 - Top 5 Episodios por Serie
                                            0 - Salir
                                        """;
                        System.out.println(menu);
                        opcion = teclado.nextInt();
                        teclado.nextLine();

                        switch (opcion) {
                                case 1 -> buscarSerieWeb();
                                case 2 -> buscarEpisodioPorSerie();
                                case 3 -> mostrarSeriesBuscadas();
                                case 4 -> buscarSeriesPorTitulo();
                                case 5 -> bucarTop5Series();
                                case 6 -> buscarSeriesPorcategoria();
                                case 7 -> filtrarSeriesPorTemporadaYCalificacion();
                                case 8 -> buscarEpisodiosPorTitulo();
                                case 9 -> buscarTop5Episodios();
                                case 0 -> System.out.println("Cerrando la aplicación...");
                                default -> System.out.println("Opción inválida");
                        }
                }
        }

        private DatosSerie getDatosSerie() {
                System.out.println("Escribe el nombre de la serie que deseas buscar");
                var nombreSerie = teclado.nextLine();
                var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&" + API_KEY);
                System.out.println(json);
                return conversor.obtenerDatos(json, DatosSerie.class);
        }

        private void buscarEpisodioPorSerie() {
                mostrarSeriesBuscadas();
                System.out.println("Indica el nombre de la serie que deseas buscar episodios:");
                var nombreSerie = teclado.nextLine();

                Optional<Serie> serie = series.stream()
                                .filter(s -> s.getTitulo().equalsIgnoreCase(nombreSerie))
                                .findFirst();

                if (serie.isPresent()) {
                        var serieEncontrada = serie.get();
                        List<DatosTemporada> temporadas = new ArrayList<>();

                        for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas(); i++) {
                                var json = consumoApi.obtenerDatos(
                                                URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&Season="
                                                                + i + "&" + API_KEY);
                                DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
                                temporadas.add(datosTemporada);
                        }
                        temporadas.forEach(System.out::println);

                        List<Episodio> episodios = temporadas.stream()
                                        .flatMap(d -> d.episodios().stream()
                                                        .map(e -> new Episodio(e.numeroEpisodio(), e)))
                                        .collect(Collectors.toList());
                        serieEncontrada.setEpisodios(episodios);
                        repositorio.save(serieEncontrada);
                }
        }

        private void buscarSerieWeb() {
                DatosSerie datos = getDatosSerie();
                Serie serie = new Serie(datos);
                repositorio.save(serie);
                System.out.println(datos);
        }

        private void mostrarSeriesBuscadas() {
                series = repositorio.findAll();

                series.stream()
                                .sorted(Comparator.comparing(Serie::getGenero))
                                .forEach(System.out::println);
        }

        private void buscarSeriesPorTitulo() {
                System.out.println("Indica una parte del título de las series que deseas buscar:");
                var nombreSerie = teclado.nextLine();
                serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

                if (serieBuscada.isPresent()) {
                        System.out.println("Serie encontrada: ");
                        System.out.println("La info es: " + serieBuscada.get());
                } else {
                        System.out.println("Serie no encontrada");
                }
        }

        private void bucarTop5Series() {
                List<Serie> top5Series = repositorio.findTop5ByOrderByCalificacionDesc();
                top5Series.forEach(s -> System.out
                                .println("Serie: " + s.getTitulo() + " - Calificación: " + s.getCalificacion()));
        }

        private void buscarSeriesPorcategoria() {
                System.out.println("Indica el nombre de la categoria que deseas buscar:");
                var genero = teclado.nextLine();
                var categoria = Categoria.fromString(genero);
                List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
                System.out.println("Series de la categoria encontradas: " + genero);
                seriesPorCategoria.forEach(System.out::println);
        }

        private void filtrarSeriesPorTemporadaYCalificacion() {
                try {
                        System.out.println("¿Buscar séries con cuántas temporadas? ");
                        int totalDeTemporadas = teclado.nextInt();
                        teclado.nextLine();

                        System.out.println("¿Com evaluación apartir de cuál valor? ");
                        double calificacion = teclado.nextDouble();
                        teclado.nextLine();

                        List<Serie> filtroSeries = repositorio.seriesPorTemporadaYCalificacion(totalDeTemporadas,
                                        calificacion);
                        System.out.println("*** Series Encontradas ***");
                        filtroSeries.forEach(
                                        s -> System.out.println(
                                                        s.getTitulo() + "  - calificacion: " + s.getCalificacion()));
                } catch (InputMismatchException e) {
                        System.out.println("Entrada no válida. Por favor, ingrese un número válido.");
                        teclado.nextLine();
                }
        }

        private void buscarEpisodiosPorTitulo() {
                System.out.println("Indica una parte del título de los episodios que deseas buscar:");
                var nombreEpisodio = teclado.nextLine();
                List<Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(nombreEpisodio);
                System.out.println("Episodios encontrados: ");
                episodiosEncontrados.forEach(
                                e -> System.out.printf("Serie: %s - Temporada %s- Episodio %s - Calificacion %s\n",
                                                e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(),
                                                e.getCalificacion()));
        }

        private void buscarTop5Episodios() {
                buscarSeriesPorTitulo();
                if (serieBuscada.isPresent()) {
                        Serie serie = serieBuscada.get();
                        List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
                        topEpisodios.forEach(e -> System.out.printf(
                                        "Serie: %s - Temporada %s- Episodio %s - Calificacion %s\n",
                                        e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(),
                                        e.getCalificacion()));
                }
        }
}