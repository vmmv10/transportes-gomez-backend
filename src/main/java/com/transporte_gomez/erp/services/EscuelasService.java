package com.transporte_gomez.erp.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.transporte_gomez.erp.adapter.EscuelaAdapter;
import com.transporte_gomez.erp.dto.Escuela;
import com.transporte_gomez.erp.dto.EscuelaFilter;
import com.transporte_gomez.erp.dto.Establecimiento;
import com.transporte_gomez.erp.entity.EscuelaEntity;
import com.transporte_gomez.erp.repository.EscuelaRepository;
import com.transporte_gomez.erp.specification.EscuelaSpecification;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class EscuelasService {

    private final EscuelaRepository escuelaRepository;
    private final EscuelaAdapter escuelaAdapter;

    public Page<Escuela> findAll(EscuelaFilter escuelaFilter, Pageable pageable) {
        return escuelaRepository.findAll(EscuelaSpecification.conFiltros(escuelaFilter), pageable)
                .map(escuelaAdapter::toDto);
    }

    public List<Escuela> findAll() {
        List<EscuelaEntity> escuelaEntities = escuelaRepository.findByActivo(true);
        return escuelaAdapter.toDto(escuelaEntities);
    }

    public Escuela findById(Long id) {
        EscuelaEntity escuelaEntity = escuelaRepository.getReferenceById(id);
        return escuelaAdapter.toDto(escuelaEntity);
    }

    public void createEscuela(Escuela escuela) {
        escuelaRepository.save(escuelaAdapter.createEscuelaEntity(escuela));
    }

    public void updateEscuela(Long id, Escuela escuela) {
        EscuelaEntity escuelaEntity = escuelaRepository.getReferenceById(id);
        escuelaRepository.save(escuelaAdapter.updateEscuelaEntity(escuela, escuelaEntity));
    }

    public void leerEstablecimientos(MultipartFile file) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CsvToBean<Establecimiento> csvToBean = new CsvToBeanBuilder<Establecimiento>(reader)
                    .withType(Establecimiento.class)
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Establecimiento> establecimientos = csvToBean.parse();
            List<EscuelaEntity> escuelaEntities = new ArrayList<>();
            for (Establecimiento establecimiento : establecimientos) {
                EscuelaEntity escuelaEntity = escuelaRepository.findByRbd(establecimiento.getRbd());

                if (escuelaEntity != null) {
                    escuelaEntity.setDirector(establecimiento.getDirector());
                    escuelaEntities.add(escuelaEntity);
                } else {
                    log.warn("No se encontró escuela con RBD: {} nombre {}", establecimiento.getRbd(), establecimiento.getNombre());
                }

            }

            if(!escuelaEntities.isEmpty()) {
                escuelaRepository.saveAll(escuelaEntities);
            }
        }
    }

    public void leerJardines(MultipartFile file) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CsvToBean<Establecimiento> csvToBean = new CsvToBeanBuilder<Establecimiento>(reader)
                    .withType(Establecimiento.class)
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Establecimiento> establecimientos = csvToBean.parse();
            List<EscuelaEntity> escuelaEntities = new ArrayList<>();
            for (Establecimiento establecimiento : establecimientos) {
                EscuelaEntity escuelaEntity = escuelaRepository.findByRbd(establecimiento.getRbd());
                if (escuelaEntity == null) {
                    log.warn("Ya existe escuela con RBD: {} nombre {}", establecimiento.getRbd(), establecimiento.getNombre());
                    continue; // Skip if the school already exists
                }

                int parenStart = establecimiento.getNombre().indexOf('(');
                int parenEnd = establecimiento.getNombre().indexOf(')');

                if (parenStart < 0 || parenEnd < 0 || parenEnd <= parenStart) {
                    throw new IllegalArgumentException("Paréntesis mal formados");
                }

                String nombre = establecimiento.getNombre().substring(0, parenStart).trim();
                String direccion = establecimiento.getNombre().substring(parenStart + 1, parenEnd).trim();

                escuelaEntity.setNombre("Jardin Infantil " + nombre);
                escuelaEntity.setDireccion(Objects.requireNonNull(direccion));

                escuelaEntities.add(escuelaEntity);
            }

            if(!escuelaEntities.isEmpty()) {
                escuelaRepository.saveAll(escuelaEntities);
            }
        }
    }
    //@PostConstruct
    public void normalizarNombres() {
        List<EscuelaEntity> escuelaEntities = escuelaRepository.findAll();

        for (EscuelaEntity escuelaEntity : escuelaEntities) {
            String nombreActual = escuelaEntity.getNombre();
            escuelaEntity.setNombre(nombreActual.toUpperCase());

            if (escuelaEntity.getDirector() != null) {
                String directorActual = escuelaEntity.getDirector();
                escuelaEntity.setDirector(directorActual.toUpperCase());
            }

            escuelaRepository.save(escuelaEntity);
        }
    }
}
