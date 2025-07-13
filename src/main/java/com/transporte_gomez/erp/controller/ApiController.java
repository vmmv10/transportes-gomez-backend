package com.transporte_gomez.erp.controller;

import com.opencsv.CSVReader;
import com.transporte_gomez.erp.entity.DocumentoEntity;
import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/csv")
public class ApiController {

    private final DocumentoTipoRepository documentoTipoRepository;
    private final ProveedorRepository proveedorRepository;
    private final BodegaRepository bodegaRepository;
    private final DocumentoRepository documentoRepository;
    private final EscuelaRepository escuelaRepository;
    private final OrdenServicioRepository ordenServicioRepository;

    @GetMapping
    public String rootApi() {
        return "API raíz funcionando";
    }

    @PostMapping("/upload")
    public String uploadCsv(@RequestParam("file") MultipartFile file) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int totalOrdenes = 0;
        int errores = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> filas = reader.readAll();

            for (String[] row : filas) {
                try {
                    if (row.length < 6) continue; // Asegura columna 5 para fecha y columna 3 para doc

                    // 1. Verificar fecha (columna 5)
                    LocalDate fecha = LocalDate.parse(row[5].trim(), formatter);
                    int mes = fecha.getMonthValue();
                    int anio = fecha.getYear();

                    if (anio != 2025 || (mes != 6 && mes != 7)) {
                        continue; // ❌ Fuera del rango junio-julio 2025
                    }

                    // 2. Verificar que NO esté entregado (columna 10)
                    if (row.length > 10 && row[10] != null && row[10].trim().equalsIgnoreCase("ENTREGADO")) {
                        continue; // ❌ Ya fue entregado
                    }

                    // 3. Documento (columna 3)
                    String numeroDocumento = row[3].trim();
                    if (numeroDocumento.isEmpty() || numeroDocumento.equalsIgnoreCase("null")) continue;

                    DocumentoEntity documentoEntity = documentoRepository.findByNumero(Long.valueOf(numeroDocumento));
                    if (documentoEntity == null) continue;

                    // 4. Crear OS
                    OrdenServicioEntity ordenServicio = new OrdenServicioEntity();
                    ordenServicio.setDocumento(documentoEntity);
                    ordenServicio.setEscuela(escuelaRepository.getReferenceById(416L)); // reemplaza por lógica dinámica si deseas
                    ordenServicio.setEntregado(false); // explícito

                    // Observaciones desde columna 0
                    if (row.length > 0 && !row[0].trim().isEmpty()) {
                        ordenServicio.setObservaciones(row[0].trim());
                    }

                    // 5. Crear detalle
                    OrdenServicioDetalleEntity detalle = new OrdenServicioDetalleEntity();

                    if (row.length > 8) {
                        int cantidad = Integer.parseInt(row[8].trim());
                        detalle.setCantidad(cantidad > 0 ? cantidad : 1);
                    } else {
                        detalle.setCantidad(1); // por defecto si falla
                    }

                    detalle.setNombre(row.length > 11 ? row[11].trim() : "Sin datos adicionales");
                    detalle.setOrdenServicio(ordenServicio);
                    ordenServicio.getDetalles().add(detalle);

                    ordenServicioRepository.save(ordenServicio);
                    totalOrdenes++;

                } catch (Exception e) {
                    errores++;
                }
            }

            return "Órdenes (junio/julio no entregadas) creadas: " + totalOrdenes + ". Filas con error: " + errores;

        } catch (Exception e) {
            return "Error general al procesar el archivo: " + e.getMessage();
        }
    }


}