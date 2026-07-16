package com.controlf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot ControlF.
 */
@SpringBootApplication
public class ControlFApplication {

    /**
     * Arranca el contexto de Spring.
     *
     * @param args argumentos de línea de comandos recibidos por Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(ControlFApplication.class, args);
    }
}
