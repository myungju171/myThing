package com.project.mything.auth.service;


import net.nurigo.sdk.message.model.Message;

public interface SendService {
    public abstract Message send(String toNumber, String randomNumber);

}
