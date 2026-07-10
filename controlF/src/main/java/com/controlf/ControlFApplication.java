package com.controlf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ControlFApplication {
    public static void main(String[] args) {
        SpringApplication.run(ControlFApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(DataSeederService seeder) {
        return args -> {
            try {
               /* System.out.println(">>> INICIANDO POBLACIÓN AUTOMÁTICA...");
                seeder.seed();
                System.out.println(">>> POBLACIÓN COMPLETADA CON ÉXITO.");*/
            } catch (Exception e) {
                System.err.println(">>> ERROR EN POBLACIÓN: " + e.getMessage());
            }
        };
    }
}
