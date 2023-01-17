package com.project.mything.auth.service;

import com.project.mything.auth.config.AuthConfig;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendServiceImpl implements SendService {
    private final DefaultMessageService defaultMessageService;
    private final AuthConfig authConfig;


    public SendServiceImpl(AuthConfig authConfig) {
        this.defaultMessageService =
                NurigoApp.INSTANCE.initialize(
                        authConfig.getPublicKey(),
                        authConfig.getSecretKey(),
                        "https://api.coolsms.co.kr"
                );
        this.authConfig = authConfig;
    }

    public Message send(String toNumber, String randomNumber) {

        Message message = new Message();
        message.setFrom(authConfig.getFromNumber());
        message.setTo(toNumber);
        message.setText("[MyThing] 인증번호는 " + randomNumber + " 입니다.");

        SingleMessageSentResponse response =
                defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
        log.info(message.getTo() + "로 문자를 발송완료 했습니다.");
        return message;
    }


}
