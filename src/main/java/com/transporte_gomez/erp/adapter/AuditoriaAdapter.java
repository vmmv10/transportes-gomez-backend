package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Auditoria;
import com.transporte_gomez.erp.entity.AuditoriaEntity;
import com.transporte_gomez.erp.entity.DocumentoTipoEntity;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaAdapter {

    public AuditoriaEntity getAuditoria(AuditoriaEntity auditoriaEntity) {
        AuditoriaEntity auditoria = new AuditoriaEntity();
        auditoria.setId(auditoriaEntity.getId());
        auditoria.setFecha(auditoriaEntity.getFecha());
        auditoria.setOperacion(auditoriaEntity.getOperacion());
        return auditoria;
    }

        public AuditoriaEntity getAuditoria(Auditoria auditoria, Long usuarioId, Long moduloUd) {
        AuditoriaEntity auditoriaEntity = new AuditoriaEntity();
        auditoriaEntity.setId(auditoria.getId());
        auditoriaEntity.setOperacion(auditoria.getOperacion());
        auditoriaEntity.setUsuarioId(usuarioId);
        auditoriaEntity.setModuloUd(moduloUd);
        return auditoriaEntity;
    }
}
