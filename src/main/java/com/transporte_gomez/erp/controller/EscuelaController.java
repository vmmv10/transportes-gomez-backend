package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Escuela;
import com.transporte_gomez.erp.dto.EscuelaFilter;
import com.transporte_gomez.erp.dto.Establecimiento;
import com.transporte_gomez.erp.services.EscuelasService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/escuelas")
public class EscuelaController {

    private EscuelasService escuelasService;

    @GetMapping
    public Page<Escuela> getEscuelas(Pageable pageable, EscuelaFilter escuelaFilter) {
        return escuelasService.findAll(escuelaFilter, pageable);
    }

    @GetMapping("/list")
    public List<Escuela> getAllEscuelas() {
        return escuelasService.findAll();
    }

    @GetMapping("/{id}")
    public Escuela getEscuelaById(@PathVariable Long id) {
        return escuelasService.findById(id);
    }

    @PostMapping
    public void createEscuela(@RequestBody Escuela escuela) {
        escuelasService.createEscuela(escuela);
    }

    @PutMapping("/{id}")
    public void updateEscuela(@PathVariable Long id, @RequestBody Escuela escuela) {
        escuelasService.updateEscuela(id, escuela);
    }

    @PostMapping("/cargar")
    public void cargarCsv(@RequestParam("file") MultipartFile file) throws Exception {
        escuelasService.leerEstablecimientos(file);
    }

    @PostMapping("/cargar/jardines")
    public void cargarJardines(@RequestParam("file") MultipartFile file) throws Exception {
        escuelasService.leerJardines(file);
    }
}
