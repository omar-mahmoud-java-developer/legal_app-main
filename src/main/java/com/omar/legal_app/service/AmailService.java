package com.omar.legal_app.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.omar.legal_app.entity.MailBody;
@Service
public class AmailService {
  private final JavaMailSender javaMailSender;

  public AmailService(JavaMailSender javaMailSender) {
      this.javaMailSender = javaMailSender;
  }

  public void sendMail(MailBody mailBody) {
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setTo(mailBody.getTo());
      mailMessage.setFrom(""); // Set a valid sender email address
      mailMessage.setSubject(mailBody.getSubject());
      mailMessage.setText(mailBody.getText());
      javaMailSender.send(mailMessage);
  }
     
}
