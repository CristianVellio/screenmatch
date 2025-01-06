package com.cristianvellio.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import com.cristianvellio.config.Config;
import com.cristianvellio.model.DatosSerie;
import com.cristianvellio.model.DatosTemporada;
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

        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        Integer totalTemporadas = datos.getTotalDeTemporadasAsInteger();
        if (totalTemporadas == null) {
            System.out.println(
                    "No se pudo obtener la cantidad de temporadas. Verifique el nombre ingresado o intente nuevamente.");
            return;
        }

        // Busca los datos de todas las temporadas
        List<DatosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= totalTemporadas; i++) {
            try {
                json = consumoApi.obtenerDatos(
                        URL_BASE + nombreSerie.replace(" ", "+") + "&season=" + i + "&" + API_KEY);
                var datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
                temporadas.add(datosTemporada);
            } catch (Exception e) {
                System.err.println("Error al obtener los datos de la temporada " + i + ": " + e.getMessage());
            }
        }
        // temporadas.forEach(System.out::println);

        // Mostrar solo el titulo de los episodios por temporada

        IntStream.range(0, datos.getTotalDeTemporadasAsInteger())
                .forEach(i -> temporadas.get(i).episodios().forEach(episodio -> System.out.println(
                        "Temporada " + (i + 1) + " - Episodio " + (temporadas.get(i).episodios().indexOf(episodio) + 1)
                                +
                                ": " + episodio.titulo())));
    }
}
