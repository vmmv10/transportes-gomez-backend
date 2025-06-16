package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Auditoria;
import com.transporte_gomez.erp.entity.AuditoriaEntity;
import com.transporte_gomez.erp.entity.DocumentoTipoEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuditoriaAdapter {

    public Auditoria getAuditoria(AuditoriaEntity auditoriaEntity) {
        Auditoria auditoria = new Auditoria();
        auditoria.setId(auditoriaEntity.getId());
        auditoria.setFecha(auditoriaEntity.getFecha());
        auditoria.setOperacion(auditoriaEntity.getOperacion());
        return auditoria;
    }

    public AuditoriaEntity createAuditoria(Auditoria auditoria) {
        AuditoriaEntity auditoriaEntity = new AuditoriaEntity();
        auditoriaEntity.setOperacion(auditoria.getOperacion());
        auditoriaEntity.setUsuarioId(auditoria.getUsuario());
        auditoriaEntity.setModuloId(auditoria.getModulo());
        auditoriaEntity.setEntidadId(auditoria.getEntidad());
        auditoriaEntity.setFecha(OffsetDateTime.now());
        return auditoriaEntity;
    }

}
