package com.omar.legal_app.controller;

import java.util.Date;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omar.legal_app.dto.MailBody;
import com.omar.legal_app.entity.ForgotPassword;
import com.omar.legal_app.entity.User;
import com.omar.legal_app.repository.ForgotRepo;
import com.omar.legal_app.repository.UserRepository;
import com.omar.legal_app.service.AmailService;

@RestController
@RequestMapping("forgotPassword")
public class ForgotController {
    private  final UserRepository userRepository;
private final AmailService amailService;
    private final ForgotRepo forgotRepo;
    
  

    public ForgotController(UserRepository userRepository, AmailService amailService, ForgotRepo forgotRepo) {
        this.userRepository = userRepository;
        this.amailService = amailService;
        this.forgotRepo = forgotRepo;
    }
    
    @PostMapping("/vefyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){


        User user=userRepository.findByEmail(email);
int code =codeGenerator();
        MailBody mailBody=MailBody.builder().to(email)
        .text("this is the code for frogot password "+ code)
        .subject("Code for forgot password")
        .build();

        ForgotPassword fp =ForgotPassword.builder()
        .code(code)
        .expiration(new Date(System.currentTimeMillis()+
        70*1000))
        .user(user)
        .build();
        amailService.sendMail(mailBody);
         forgotRepo.save(fp);


        return ResponseEntity.ok("email send for verifacation ");
        
    }
    private Integer codeGenerator(){


        Random random=new Random();
        return random.nextInt(10000 ,99999);
    }
    


    










}
