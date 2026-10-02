package com.transporte_gomez.erp.services;

import jakarta.annotation.PreDestroy;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Envío de correos por SMTP (spring.mail.*). Si no hay servidor configurado (MAIL_HOST vacío)
 * los avisos simplemente no se envían y el resto del sistema funciona igual.
 */
@Slf4j
@Service
public class CorreoService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final ObjectProvider<JavaMailSender> mailSender;
    private final ExecutorService envios = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "correo");
        t.setDaemon(true);
        return t;
    });

    @Value("${spring.mail.host:}")
    private String host;

    @Value("${app.correo.remitente:}")
    private String remitente;

    @Value("${app.correo.remitente-nombre:Transportes Gomez Velasquez}")
    private String remitenteNombre;

    public CorreoService(ObjectProvider<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    /** Un correo listo para enviar. html puede ser null (solo texto). */
    public record Correo(List<String> para, String asunto, String texto, String html, String responderA) {
    }

    public boolean habilitado() {
        return StringUtils.hasText(host) && StringUtils.hasText(remitente) && mailSender.getIfAvailable() != null;
    }

    /** Separa una lista "a@x.cl, b@y.cl" y deja solo las direcciones válidas. */
    public static List<String> direcciones(String lista) {
        if (lista == null || lista.isBlank()) {
            return List.of();
        }
        return Arrays.stream(lista.split("[,;\\s]+")).map(String::trim).filter(s -> EMAIL.matcher(s).matches()).distinct().toList();
    }

    public static boolean esCorreo(String valor) {
        return valor != null && EMAIL.matcher(valor.trim()).matches();
    }

    /** Envía ahora. Si falla lanza IllegalStateException con un mensaje para mostrar al usuario. */
    public void enviar(Correo correo) {
        if (!habilitado()) {
            throw new IllegalStateException("El envío de correos no está configurado en el servidor");
        }
        if (correo.para() == null || correo.para().isEmpty()) {
            throw new IllegalArgumentException("El correo no tiene destinatarios");
        }
        JavaMailSender sender = mailSender.getObject();
        try {
            MimeMessage mensaje = sender.createMimeMessage();
            boolean conHtml = correo.html() != null;
            MimeMessageHelper h = new MimeMessageHelper(mensaje, conHtml, "UTF-8");
            h.setFrom(remitente, remitenteNombre);
            h.setTo(correo.para().toArray(String[]::new));
            if (esCorreo(correo.responderA())) {
                h.setReplyTo(correo.responderA().trim());
            }
            h.setSubject(correo.asunto());
            if (conHtml) {
                h.setText(correo.texto(), correo.html());
            } else {
                h.setText(correo.texto());
            }
            sender.send(mensaje);
            log.info("Correo enviado: \"{}\" a {} destinatario(s)", correo.asunto(), correo.para().size());
        } catch (MailAuthenticationException e) {
            log.warn("El servidor SMTP rechazó el usuario o la contraseña: {}", e.getMessage());
            throw new IllegalStateException("El servidor de correo rechazó el usuario o la contraseña configurados");
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            log.warn("No se pudo enviar el correo \"{}\": {}", correo.asunto(), e.getMessage());
            if (e.getCause() instanceof AuthenticationFailedException) {
                throw new IllegalStateException("El servidor de correo rechazó el usuario o la contraseña configurados");
            }
            throw new IllegalStateException("No se pudo enviar el correo, intenta nuevamente en unos minutos");
        }
    }

    /**
     * Envía en segundo plano, después de que se confirme la transacción en curso
     * (si se deshace, no se envía). Los errores solo quedan en el log.
     */
    public void enviarDespues(Correo correo) {
        if (!habilitado() || correo.para() == null || correo.para().isEmpty()) {
            return;
        }
        Runnable tarea = () -> {
            try {
                enviar(correo);
            } catch (IllegalStateException | IllegalArgumentException e) {
                // ya registrado en enviar()
            } catch (Throwable t) {
                log.error("Error inesperado al enviar el correo \"{}\"", correo.asunto(), t);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    envios.execute(tarea);
                }
            });
        } else {
            envios.execute(tarea);
        }
    }

    @PreDestroy
    void cerrar() {
        envios.shutdown();
    }
}
