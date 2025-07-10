package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.entity.DocumentoEntity;
import com.transporte_gomez.erp.entity.DocumentoTipoEntity;
import com.transporte_gomez.erp.entity.UsuarioEntity;
import com.transporte_gomez.erp.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentoAdapter {

    private final DocumentoTipoRepository documentoTipoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final BodegaRepository bodegaRepository;

    public Documento getDocumento(DocumentoEntity documentoEntity) {
        Documento documento = new Documento();
        documento.setId(documentoEntity.getId());
        documento.setNumero(documentoEntity.getNumero());
        documento.setEntregado(documentoEntity.getEntregado());
        documento.setAsignado(documentoEntity.getAsignado());

        Usuario usuario = new Usuario();
        usuario.setId(documentoEntity.getUsuario().getId());
        usuario.setNombre(documentoEntity.getUsuario().getNombre());
        documento.setUsuario(usuario);

        Proveedor proveedorDto = new Proveedor();
        proveedorDto.setId(documentoEntity.getProveedor().getId());
        proveedorDto.setNombre(documentoEntity.getProveedor().getRazonSocial());
        proveedorDto.setRut(documentoEntity.getProveedor().getRut());
        proveedorDto.setDireccion(documentoEntity.getProveedor().getDireccion());
        proveedorDto.setTelefono(documentoEntity.getProveedor().getTelefono());
        proveedorDto.setEmail(documentoEntity.getProveedor().getEmail());
        proveedorDto.setActivo(documentoEntity.getProveedor().getActivo());

        documento.setProveedor(proveedorDto);

        /*Escuela escuela = new Escuela();
        escuela.setId(documentoEntity.getEscuela().getId());
        escuela.setNombre(documentoEntity.getEscuela().getNombre());
        escuela.setDireccion(documentoEntity.getEscuela().getDireccion());
        escuela.setEmail(documentoEntity.getEscuela().getEmail());
        escuela.setTelefono(documentoEntity.getEscuela().getTelefono());
        escuela.setDirector(documentoEntity.getEscuela().getDirector());
        escuela.setRbd(documentoEntity.getEscuela().getRbd());
        escuela.setLatitud(documentoEntity.getEscuela().getLatitud());
        escuela.setLongitud(documentoEntity.getEscuela().getLongitud());
        escuela.setComuna(documentoEntity.getEscuela().getComuna());

        documento.setEscuela(escuela);*/

        DocumentoTipoEntity documentoTipoEntity = documentoEntity.getTipo();
        DocumentoTipo documentoTipo = new DocumentoTipo();
        documentoTipo.setId(documentoTipoEntity.getId());
        documentoTipo.setNombre(documentoTipoEntity.getNombre());
        documentoTipo.setCodigo(documentoTipoEntity.getCodigo());

        documento.setTipo(documentoTipo);

        if (documentoEntity.getBodega() != null) {
            Bodega bodega = new Bodega();
            bodega.setId(documentoEntity.getBodega().getId());
            bodega.setNombre(documentoEntity.getBodega().getNombre());
            bodega.setDireccion(documentoEntity.getBodega().getUbicacion());
            documento.setBodega(bodega);
        }
        return documento;
    }

    public DocumentoEntity createEntity(Documento documento) {
        DocumentoEntity documentoEntity = new DocumentoEntity();

        documentoEntity.setNumero(documento.getNumero());

        UsuarioEntity usuarioEntity = usuarioRepository.findById(documento.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        documentoEntity.setUsuario(usuarioEntity);

        if (documento.getProveedor() != null && documento.getProveedor().getId() != null) {
            documentoEntity.setProveedor(proveedorRepository.findById(documento.getProveedor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado")));
        }

        DocumentoTipoEntity documentoTipoEntity = documentoTipoRepository.getReferenceById(documento.getTipo().getId());
        documentoEntity.setTipo(documentoTipoEntity);

        if (documento.getBodega() != null && documento.getBodega().getId() != null) {
            documentoEntity.setBodega(bodegaRepository.findById(documento.getBodega().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada")));
        }

        return documentoEntity;
    }

    public DocumentoEntity updateEntity(DocumentoEntity documentoEntity, Documento documento) {
        documentoEntity.setNumero(documento.getNumero());

        UsuarioEntity usuarioEntity = usuarioRepository.findById(documento.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        documentoEntity.setUsuario(usuarioEntity);

        if (documento.getProveedor() != null && documento.getProveedor().getId() != null) {
            documentoEntity.setProveedor(proveedorRepository.findById(documento.getProveedor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado")));
        }

        if (documento.getBodega() != null && documento.getBodega().getId() != null) {
            log.info("Actualizando bodega con ID: {}", documento.getBodega().getId());
            documentoEntity.setBodega(bodegaRepository.findById(documento.getBodega().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada")));
        }

        DocumentoTipoEntity documentoTipoEntity = documentoTipoRepository.getReferenceById(documento.getTipo().getId());
        documentoEntity.setTipo(documentoTipoEntity);

        return documentoEntity;
    }

}
