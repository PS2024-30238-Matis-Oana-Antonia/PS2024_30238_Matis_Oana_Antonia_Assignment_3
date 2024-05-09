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

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {
    public static final String FIRST_TOKEN = "9d7b1f62-28e3-4a15-9c98-3ec9f8e723f1";
    public static final String SECOND_TOKEN = "7f9da720-5148-4b47-9eb7-69e3ae11d8af";

    //token = 9d7b1f62-28e3-4a15-9c98-3ec9f8e723f17f9da720-5148-4b47-9eb7-69e3ae11d8af
    private final EmailService emailService;

    @PostMapping("/sendEmail")
    public ResponseEntity<MessageDTO> sendEmail(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody NotificationRequestDTO notificationRequestDTO,
            BindingResult bindingResult) {

        MessageDTO messageDto = new MessageDTO();

        if(!isTokenValid(token)){
            messageDto.setContent("Invalid authorization token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageDto);
        }

        if(bindingResult.hasErrors()){
            messageDto.setContent(bindingResult.getFieldError().getDefaultMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        }

        emailService.sendEmail(notificationRequestDTO);
        messageDto.setContent("Email successfully sent!");
        return ResponseEntity.status(HttpStatus.OK).body(messageDto);
    }

    private boolean isTokenValid(String token){
        String validToken = FIRST_TOKEN + SECOND_TOKEN;
        String t = token.substring(7);  //proba POSTMAN
        return validToken.equals(t);
    }
}
