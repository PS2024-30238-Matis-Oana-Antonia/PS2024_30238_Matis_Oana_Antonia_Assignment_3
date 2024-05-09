package com.example.microservice.services;

import com.example.microservice.dtos.NotificationRequestDTO;
import com.example.microservice.entities.Email;
import com.example.microservice.repositories.EmailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ObjectMapper objectMapper;

    private final EmailRepository emailRepository;

    @Autowired
    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public class EmailTemplateLoader {
        public String loadTemplate(String filePath) {
            try {
                return new String(Files.readAllBytes(Paths.get(filePath)));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void sendEmail(@Valid NotificationRequestDTO notificationRequestDTO) {
        try {
            // Convert EmailRequestDto to JSON string
            String jsonPayload = objectMapper.writeValueAsString(notificationRequestDTO);

            // Create MimeMessage
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set email properties
            helper.setFrom("matisoana06@gmail.com", "Carturesti");
            helper.setTo(notificationRequestDTO.getEmail());
            helper.setSubject(notificationRequestDTO.getSubject());
            EmailTemplateLoader loader = new EmailTemplateLoader();
            String htmlTemplate = null;
            try {
                htmlTemplate = StreamUtils.copyToString(
                        new ClassPathResource("templates/email.html").getInputStream(),
                        StandardCharsets.UTF_8);
            } catch (IOException e) {
                // Handle file loading exception
                e.printStackTrace();
            }
            String modifiedHtmlContent = htmlTemplate.replace("$BODY_CONTENT$", notificationRequestDTO.getBody());
            helper.setText(modifiedHtmlContent, true); // true indicates HTML content

            // Send email
            emailSender.send(message);

            // Save the email entity with status "SENT" initially
            Email emailEntity = new Email();
            emailEntity.setSubject(notificationRequestDTO.getSubject());
            emailEntity.setName(notificationRequestDTO.getName());
            emailEntity.setBody(notificationRequestDTO.getBody());
            emailEntity.setEmail(notificationRequestDTO.getEmail());
            emailRepository.save(emailEntity);

        } catch (JsonProcessingException | MessagingException | UnsupportedEncodingException |
                 jakarta.mail.MessagingException e) {
            // Handle exception
            e.printStackTrace();
        }
    }
}

