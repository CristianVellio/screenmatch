package com.cristianvellio.screenmatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cristianvellio.principal.Principal;
import com.cristianvellio.repository.SerieRepository;

@SpringBootApplication
@EntityScan("com.cristianvellio.model")
@EnableJpaRepositories("com.cristianvellio.repository")
public class ScreenmatchApplication implements CommandLineRunner {

	@Autowired
	private SerieRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository);
		principal.muestraElMenu();

		// EjemploStreams ejemploStreams = new EjemploStreams();
		// ejemploStreams.muestrEjemplo();

	}

}
