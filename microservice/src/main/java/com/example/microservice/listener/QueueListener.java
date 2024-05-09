package com.example.microservice.listener;
import com.example.microservice.dtos.NotificationRequestDTO;
import com.example.microservice.services.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper; // Inject ObjectMapper for JSON serialization/deserialization

    @RabbitListener(queues = "email-queue")
    public void processEmailRequest(String in) { // Receive message as a String
        try {
            NotificationRequestDTO notificationRequestDTO = objectMapper.readValue(in, NotificationRequestDTO.class);
            System.out.println(notificationRequestDTO);
            emailService.sendEmail(notificationRequestDTO);
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }
}

