package com.example.microservice.controllers;

import com.example.microservice.dtos.MessageDTO;
import com.example.microservice.dtos.NotificationRequestDTO;
import com.example.microservice.services.EmailService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class EmailController {

    private static final String FIRST_TOKEN = "e2a0c9b8-5f11-4b84-a1d2-6f3f12e89e3d";
    private static final String SECOND_TOKEN = "3c7e45a3-86c7-42e7-89c1-5d79fc8957af";

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<MessageDTO> sendEmail(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody NotificationRequestDTO notificationRequestDTO,
            BindingResult bindingResult) {

        if (Objects.isNull(notificationRequestDTO)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid request body"));
        }

        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageDTO("Invalid authorization token"));
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO(bindingResult.getFieldError().getDefaultMessage()));
        }

        emailService.sendEmail(notificationRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Email successfully sent!"));
    }

    private boolean validateToken(String token) {
        String[] tokenParts = token.split(" ");
        if (tokenParts.length != 2) {
            return false;
        }
        String concatenatedToken = tokenParts[1];
        String validToken = FIRST_TOKEN + SECOND_TOKEN;
        return validToken.equals(concatenatedToken);
    }
}
