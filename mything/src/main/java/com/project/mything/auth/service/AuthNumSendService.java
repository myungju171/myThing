package com.project.mything.auth.service;


import net.nurigo.sdk.message.model.Message;

public interface AuthNumSendService {
    public abstract void send(String toNumber, String randomNumber);

}
