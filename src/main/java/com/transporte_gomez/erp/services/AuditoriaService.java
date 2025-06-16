package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.AuditoriaAdapter;
import com.transporte_gomez.erp.dto.Auditoria;
import com.transporte_gomez.erp.entity.AuditoriaEntity;
import com.transporte_gomez.erp.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaAdapter auditoriaAdapter;

    public void registrarAuditoria(String accion, Integer moduloCodigo, Long entidadId, Long usuarioId) {
        Auditoria auditoria = new Auditoria();
        auditoria.setOperacion(accion);
        auditoria.setModulo(moduloCodigo);
        auditoria.setUsuario(usuarioId);
        auditoria.setEntidad(entidadId);
        auditoriaRepository.save(auditoriaAdapter.createAuditoria(auditoria));
    }

    public List<Auditoria> getAuditorias(Integer moduloCodigo, Long entidadId) {
        List<AuditoriaEntity> auditorias = auditoriaRepository.findByModuloIdAndEntidadId(moduloCodigo, entidadId);
        return auditorias.stream()
                .map(auditoriaAdapter::getAuditoria)
                .toList();
    }
}
