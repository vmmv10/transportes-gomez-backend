package com.transporte_gomez.erp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class ApiController {

    @GetMapping
    public String rootApi() {
        return "API raíz funcionando";
    }
}