#!/bin/bash

# Carrega as variáveis de ambiente do arquivo .env
export $(grep -v '^#' .env | xargs)

# Inicia a aplicação Spring Boot
./mvnw spring-boot:run
