package com.mizerski.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe principal da aplicação Spring Boot.
 */
@SpringBootApplication
@Slf4j
public class BackendApplication {

	public static void main(String[] args) {
		log.info("Iniciando a aplicação Backend...");
		SpringApplication.run(BackendApplication.class, args);
		log.info("Aplicação Backend iniciada com sucesso!");
	}
}