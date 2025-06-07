package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Imagen;
import com.transporte_gomez.erp.services.ImagenService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService imagenService;

    @GetMapping("{entidadTipo}/{entidadId}")
    public List<Imagen> getImagenesByEntidad(@PathVariable String entidadTipo, @PathVariable Long entidadId) {
        return imagenService.getImagen(entidadTipo, entidadId);
    }

    @DeleteMapping("{id}")
    public void deleteImagen(@PathVariable Long id) {
        imagenService.eliminarImagen(id);
    }
}
