package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ImagenAdapter;
import com.transporte_gomez.erp.dto.Imagen;
import com.transporte_gomez.erp.entity.ImagenEntity;
import com.transporte_gomez.erp.repository.ImagenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ImagenService {

    private final ImagenRepository imagenRepository;
    private final ImagenAdapter imagenAdapter;

    public List<Imagen> getImagen(Integer entidad, Long entidadId) {
        List<ImagenEntity> imagenEntities = imagenRepository.findByEntidadTipoAndEntidadId(entidad, entidadId);

        return imagenEntities.stream()
                .map(imagenAdapter::getImagen)
                .toList();
    }

    public void guardarArchivo(MultipartFile file, Integer entidadTipo, Long entidadId, String uploadDir) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío.");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen o PDF.");
        }

        long maxSize = 5 * 1024 * 1024; // 5 MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido de 5 MB.");
        }

        byte[] fileBytes = file.getBytes();
        String nombreOriginal = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String nombreArchivo = uuid + "_" + nombreOriginal;

        String subcarpeta = (entidadTipo.equals(4)) ? "documentos" :
                (entidadTipo.equals(1)) ? "ordenes" :
                        "otros";

        Path rutaArchivo = Paths.get(uploadDir).resolve("normal").resolve(nombreArchivo);
        Files.createDirectories(rutaArchivo.getParent());
        Files.write(rutaArchivo, fileBytes);
        log.info("✅ Archivo guardado en: {}", rutaArchivo.toAbsolutePath());

        String rutaRelativa = subcarpeta + "/normal/" + nombreArchivo;
        String rutaThumbRelativa = null;

        if (contentType.startsWith("image/")) {
            String nombreThumbnail = uuid + "_thumb_" + nombreOriginal;
            Path rutaThumbnail = Paths.get(uploadDir).resolve("thumb").resolve(nombreThumbnail);
            Files.createDirectories(rutaThumbnail.getParent());
            Thumbnails.of(new ByteArrayInputStream(fileBytes))
                    .size(150, 150)
                    .toFile(rutaThumbnail.toFile());
            log.info("✅ Thumbnail guardado en: {}", rutaThumbnail.toAbsolutePath());

            rutaThumbRelativa = subcarpeta + "/thumb/" + nombreThumbnail;
        }

        ImagenEntity imagen = new ImagenEntity();
        imagen.setEntidadTipo(entidadTipo);
        imagen.setEntidadId(entidadId);
        imagen.setNombreOriginal(nombreOriginal);
        imagen.setRuta(rutaRelativa);
        imagen.setRutaThumbnail(rutaThumbRelativa);

        log.info("✅ Guardando metadata de la imagen en la base de datos");

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

    public void eliminarImagenByEntidad(Integer entidadTipo, Long id) {
        List<ImagenEntity> imagenEntity = imagenRepository.findByEntidadTipoAndEntidadId(entidadTipo, id);

        for (ImagenEntity imagenEntity1 : imagenEntity) {
            eliminarImagen(imagenEntity1.getId());
        }
    }
}
