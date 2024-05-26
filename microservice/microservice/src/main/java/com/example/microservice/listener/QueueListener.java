package com.example.microservice.listener;

import com.example.microservice.dtos.NotificationRequestDTO;
import com.example.microservice.services.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class QueueListener {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ATTACHMENT_PATH = "D:\\Anul III - Semestrul II\\PS\\Proiect-Carturesti\\carturesti-backend\\bill.pdf";

    @RabbitListener(queues = "queue-for-email")
    public void processEmailRequest(String in) {
        try {
            NotificationRequestDTO notificationRequestDTO = objectMapper.readValue(in, NotificationRequestDTO.class);
            byte[] attachmentData = readAttachmentFromFile(ATTACHMENT_PATH);
            emailService.sendEmailWithAttachment(notificationRequestDTO, attachmentData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private byte[] readAttachmentFromFile(String attachmentPath) throws IOException {
        File attachmentFile = new File(attachmentPath);
        if (!attachmentFile.exists()) {
            throw new IOException("Attachment file not found");
        }
        return Files.readAllBytes(Paths.get(attachmentPath));
    }
}


