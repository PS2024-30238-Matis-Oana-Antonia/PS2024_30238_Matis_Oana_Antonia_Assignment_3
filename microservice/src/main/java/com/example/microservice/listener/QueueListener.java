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
    private ObjectMapper objectMapper;
    @RabbitListener(queues = "queue-for-email")
    public void processEmailRequest(String in) {
        try {
            NotificationRequestDTO notificationRequestDTO = objectMapper.readValue(in, NotificationRequestDTO.class);
            System.out.println(notificationRequestDTO);
            emailService.sendEmail(notificationRequestDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

