package com.project.mything.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    public static final String AUTH_EMAIL_TITLE = "MyThing 인증 번호 입니다.";

    @Async("threadPoolTaskExecutor")
    public void sendMail(String email, String title, String content) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailProperties.getUsername());
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(content);
        javaMailSender.send(simpleMailMessage);
    }
}
