package com.transporte_gomez.erp.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.transporte_gomez.erp.adapter.EscuelaAdapter;
import com.transporte_gomez.erp.dto.Escuela;
import com.transporte_gomez.erp.dto.Establecimiento;
import com.transporte_gomez.erp.entity.EscuelaEntity;
import com.transporte_gomez.erp.repository.EscuelaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class EscuelasService {

    private final EscuelaRepository escuelaRepository;
    private final EscuelaAdapter escuelaAdapter;


    public List<Escuela> findAll() {
        return escuelaAdapter.toDto(escuelaRepository.findAll());
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
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), "ISO-8859-1")) {
            CsvToBean<Establecimiento> csvToBean = new CsvToBeanBuilder<Establecimiento>(reader)
                    .withType(Establecimiento.class)
                    .withSeparator(';')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Establecimiento> establecimientos = csvToBean.parse();
            List<EscuelaEntity> escuelaEntities = new ArrayList<>();
            for (Establecimiento establecimiento : establecimientos) {
                if(Objects.equals(establecimiento.getCodProRbd(), "102")) {
                    EscuelaEntity escuelaEntity = new EscuelaEntity();
                    escuelaEntity.setNombre(establecimiento.getNombreRbd());
                    escuelaEntity.setLatitud(establecimiento.getLatitud());
                    escuelaEntity.setLongitud(establecimiento.getLongitud());
                    escuelaEntity.setRbd(establecimiento.getRbd());
                    escuelaEntity.setComuna(establecimiento.getNombreComRbd());
                    escuelaEntity.setSostenedorRut(establecimiento.getRutSostenedor());
                    escuelaEntities.add(escuelaEntity);
                }
            }

            if(!escuelaEntities.isEmpty()) {
                escuelaRepository.saveAll(escuelaEntities);
            }
        }
    }
}
