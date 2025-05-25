package com.mizerski.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Spring Boot.
 */
@SpringBootApplication
public class BackendApplication {

	private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

	public static void main(String[] args) {
		log.info("Iniciando a aplicação Backend...");
		SpringApplication.run(BackendApplication.class, args);
		log.info("Aplicação Backend iniciada com sucesso!");
	}
}