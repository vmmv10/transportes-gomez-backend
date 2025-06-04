package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Documento;
import com.transporte_gomez.erp.dto.Proveedor;
import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.entity.DocumentoEntity;
import com.transporte_gomez.erp.entity.UsuarioEntity;
import com.transporte_gomez.erp.repository.ProveedorRepository;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DocumentoAdapter {

    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;

    public Documento getDocumento(DocumentoEntity documentoEntity) {
        Documento documento = new Documento();
        documento.setId(documentoEntity.getId());
        documento.setNumero(documentoEntity.getNumero());
        documento.setTipoCodigo(documentoEntity.getTipoCodigo());
        documento.setTipoNombre(documentoEntity.getTipoNombre());

        Usuario usuario = new Usuario();
        usuario.setId(documentoEntity.getUsuario().getId());
        usuario.setNombre(documentoEntity.getUsuario().getNombre());
        documento.setUsuario(usuario);

        Proveedor proveedor = new Proveedor();
        proveedor.setId(documentoEntity.getProveedor().getId());
        proveedor.setNombre(documentoEntity.getProveedor().getRazonSocial());
        proveedor.setRut(documentoEntity.getProveedor().getRut());

        documento.setProveedor(proveedor);

        return documento;
    }

    public DocumentoEntity createEntity(Documento documento) {
        DocumentoEntity documentoEntity = new DocumentoEntity();
        documentoEntity.setId(documento.getId());
        documentoEntity.setNumero(documento.getNumero());
        documentoEntity.setTipoCodigo(documento.getTipoCodigo());
        documentoEntity.setTipoNombre(documento.getTipoNombre());

        UsuarioEntity usuarioEntity = usuarioRepository.findById(documento.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        documentoEntity.setUsuario(usuarioEntity);

        if (documento.getProveedor() != null && documento.getProveedor().getId() != null) {
            documentoEntity.setProveedor(proveedorRepository.findById(documento.getProveedor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado")));
        }


        return documentoEntity;
    }

    public DocumentoEntity updateEntity(DocumentoEntity documentoEntity) {
        documentoEntity.setNumero(documentoEntity.getNumero());
        documentoEntity.setTipoCodigo(documentoEntity.getTipoCodigo());
        documentoEntity.setTipoNombre(documentoEntity.getTipoNombre());

        UsuarioEntity usuarioEntity = usuarioRepository.findById(documentoEntity.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        documentoEntity.setUsuario(usuarioEntity);

        if (documentoEntity.getProveedor() != null && documentoEntity.getProveedor().getId() != null) {
            documentoEntity.setProveedor(proveedorRepository.findById(documentoEntity.getProveedor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado")));
        }

        return documentoEntity;
    }

}
