package com.omar.legal_app.controller;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omar.legal_app.entity.ForgotPassword;
import com.omar.legal_app.entity.MailBody;
import com.omar.legal_app.entity.User;
import com.omar.legal_app.repository.ForgotRepo;
import com.omar.legal_app.repository.UserRepository;
import com.omar.legal_app.service.AmailService;
import com.omar.legal_app.utils.ChangePassword;

@RestController
@RequestMapping("forgotPassword")
public class ForgotController {
    private final UserRepository userRepository;
    private final AmailService amailService;
    private final ForgotRepo forgotRepo;
    private final PasswordEncoder passwordEncoder;

    public ForgotController(PasswordEncoder passwordEncoder, AmailService amailService, ForgotRepo forgotRepo, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.amailService = amailService;
        this.forgotRepo = forgotRepo;
        this.userRepository = userRepository;
    }

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid email " + email));
        int code = codeGenerator();
        MailBody mailBody = MailBody.builder().to(email)
            .text("this is the code for forgot password " + code)
            .subject("Code for forgot password")
            .build();

        ForgotPassword fp = ForgotPassword.builder()
            .code(code)
            .expiration(new Date(System.currentTimeMillis() + 70 * 1000))
            .user(user)
            .build();
        amailService.sendMail(mailBody);
        forgotRepo.save(fp);

        return ResponseEntity.ok("Email sent for verification");
    }

    @PostMapping("/verifyCode/{code}/{email}")
    public ResponseEntity<String> verifyCode(@PathVariable Integer code, @PathVariable String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid email " + email));;
        ForgotPassword fp = forgotRepo.findByCodeAndUser(code, user)
            .orElseThrow(() -> new RuntimeException("Invalid email " + email));

        if (fp.getExpiration().before(Date.from(Instant.now()))) {
            forgotRepo.deleteById(fp.getId());
            return new ResponseEntity<>("Code has expired", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok("Code verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword changePassword, @PathVariable String email) {
        if (!Objects.equals(changePassword.pasword(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Please enter the password again", HttpStatus.EXPECTATION_FAILED);
        }
        String encodedPassword = passwordEncoder.encode(changePassword.pasword());
        userRepository.updatePassword(email, encodedPassword);
        return ResponseEntity.ok("Password has been changed!!");
    }

    private Integer codeGenerator() {
        Random random = new Random();
        return random.nextInt(10000, 99999);
    }





}
