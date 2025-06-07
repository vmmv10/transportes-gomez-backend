package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ImagenAdapter;
import com.transporte_gomez.erp.dto.Imagen;
import com.transporte_gomez.erp.entity.ImagenEntity;
import com.transporte_gomez.erp.repository.ImagenRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImagenService {

    private final ImagenRepository imagenRepository;
    private final ImagenAdapter imagenAdapter;

    public List<Imagen> getImagen(String entidad, Long entidadId) {
        List<ImagenEntity> imagenEntities = imagenRepository.findByEntidadTipoAndEntidadId(entidad, entidadId);

        return imagenEntities.stream()
                .map(imagenAdapter::getImagen)
                .toList();
    }

    public void guardarImagen(MultipartFile file, String entidadTipo, Long entidadId, String uploadDir) throws IOException {
        // Validaciones
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío.");
        }

        // Validar tipo MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen.");
        }

        // Validar tamaño máximo (5 MB en este ejemplo)
        long maxSize = 5 * 1024 * 1024; // 5 MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido de 5 MB.");
        }

        // Leer archivo una sola vez
        byte[] imageBytes = file.getBytes();

        // Generar nombres
        String nombreOriginal = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String nombreArchivo = uuid + "_" + nombreOriginal;
        String nombreThumbnail = uuid + "_thumb_" + nombreOriginal;

        // Guardar imagen original
        Path rutaArchivo = Paths.get(uploadDir + "\\normal", nombreArchivo);
        Files.write(rutaArchivo, imageBytes);

        // Crear y guardar thumbnail
        Path rutaThumbnail = Paths.get(uploadDir + "\\thumb", nombreThumbnail);
        Thumbnails.of(new ByteArrayInputStream(imageBytes))
                .size(150, 150)
                .toFile(rutaThumbnail.toFile());

        // Guardar en base de datos
        ImagenEntity imagen = new ImagenEntity();
        imagen.setEntidadTipo(entidadTipo);
        imagen.setEntidadId(entidadId);
        imagen.setNombreOriginal(nombreOriginal);

        if (entidadTipo.equals("Documento")) {
            imagen.setRuta("documentos/normal/" + nombreArchivo);
            imagen.setRutaThumbnail("documentos/thumb/" + nombreThumbnail);
        } else if (entidadTipo.equals("Orden")) {
            imagen.setRuta("ordenes/normal/" + nombreArchivo);
            imagen.setRutaThumbnail("ordenes/thumb/" + nombreThumbnail);
        }

        imagenRepository.save(imagen);
    }

    public void eliminarImagen(Long id) {
        ImagenEntity imagenEntity = imagenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada con ID: " + id));

        Path rutaArchivo = Paths.get(imagenEntity.getRuta());
        try {
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar el archivo: " + e.getMessage(), e);
        }

        imagenRepository.delete(imagenEntity);
    }
}
