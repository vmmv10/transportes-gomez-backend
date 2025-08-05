package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Imagen;
import com.transporte_gomez.erp.services.ImagenService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService imagenService;

    @GetMapping("{entidadTipo}/{entidadId}")
    public List<Imagen> getImagenesByEntidad(@PathVariable Integer entidadTipo, @PathVariable Long entidadId) {
        return imagenService.getImagen(entidadTipo, entidadId);
    }

    @DeleteMapping("{id}")
    public void deleteImagen(@PathVariable Long id) {
        imagenService.eliminarImagen(id);
    }

    @GetMapping("/test-write")
    public void testWrite() throws IOException {
        String path = "/home/servicios/transportes-gomez/images/ordenes/normal/test.txt";
        Files.writeString(Paths.get(path), "Test de escritura desde Spring Boot");
    }

}
