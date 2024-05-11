package com.example.microservice.services;

import com.example.microservice.dtos.NotificationRequestDTO;
import com.example.microservice.entities.Email;
import com.example.microservice.repositories.EmailRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final ObjectMapper objectMapper;
    private final EmailRepository emailRepository;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, ObjectMapper objectMapper, EmailRepository emailRepository) {
        this.javaMailSender = javaMailSender;
        this.objectMapper = objectMapper;
        this.emailRepository = emailRepository;
    }

    public void sendEmail(@Valid NotificationRequestDTO notificationRequestDTO) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(notificationRequestDTO);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("matisoana06@gmail.com", "Cărturești");
            helper.setTo(notificationRequestDTO.getEmail());
            helper.setSubject(notificationRequestDTO.getSubject());

            String htmlTemplate = loadResourceAsString();
            String modifiedHtmlContent = htmlTemplate.replace("$BODY_CONTENT$", notificationRequestDTO.getBody());
            helper.setText(modifiedHtmlContent, true);

            javaMailSender.send(message);

            Email emailEntity = new Email();
            emailEntity.setSubject(notificationRequestDTO.getSubject());
            emailEntity.setName(notificationRequestDTO.getName());
            emailEntity.setBody(notificationRequestDTO.getBody());
            emailEntity.setEmail(notificationRequestDTO.getEmail());
            emailRepository.save(emailEntity);

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String loadResourceAsString() throws IOException {
        return StreamUtils.copyToString(
                new ClassPathResource("templates/email.html").getInputStream(),
                StandardCharsets.UTF_8);
    }
}
