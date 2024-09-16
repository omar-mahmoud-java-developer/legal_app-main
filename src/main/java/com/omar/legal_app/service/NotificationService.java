package com.omar.legal_app.service;


import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.omar.legal_app.repository.NotificationRepo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor

public class NotificationService {
    @Autowired
    private NotificationRepo notificationRepo;

	@Autowired
    JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String emailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setText(emailContent, true);
        helper.setFrom("omaribnqenawy@gmail.com", "Legal App Support");
        helper.setSubject(subject);
        helper.setTo(to);
        javaMailSender.send(message);
    
	}



}