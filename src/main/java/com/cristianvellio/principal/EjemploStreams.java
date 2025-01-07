package com.cristianvellio.principal;

import java.util.Arrays;
import java.util.List;

public class EjemploStreams {
    public void muestrEjemplo() {
        List<String> nombres = Arrays.asList("Cosme", "Fulanito", "Pepito", "Juancito");

        nombres.stream()
                .sorted()
                .limit(4)
                .filter(n -> n.startsWith("C"))
                .map(n -> n.toUpperCase())
                .forEach(System.out::println);
    }
}
