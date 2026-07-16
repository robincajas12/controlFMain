package com.controlf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Reenvía las rutas de navegación (sin extensión de archivo) hacia
 * {@code index.html}, permitiendo que el enrutador del frontend (SPA)
 * maneje la navegación del lado del cliente.
 */
@Controller
@CrossOrigin("*")
class HolaController
{
    /**
     * @return una instrucción de forward hacia {@code index.html}
     */
     @GetMapping(value = {
            "/",
            "/{path:[^\\.]*}",
            "/**/{path:[^\\.]*}"
    })
    public String redirect() {
        return "forward:/index.html";
    }
}
