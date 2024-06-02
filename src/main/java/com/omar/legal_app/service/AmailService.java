package com.omar.legal_app.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.omar.legal_app.dto.MailBody;

public class AmailService {
    private  final JavaMailSender javaMailSender;
    public AmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    
    public void  sendMail (MailBody mailBody){
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setTo(mailBody.to());
      mailMessage.setFrom("");
      mailMessage.setSubject(mailBody.subject());
      mailMessage.setText(mailBody.text());
      javaMailSender.send(mailMessage);




    }
     
}
