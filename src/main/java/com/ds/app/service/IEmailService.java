package com.ds.app.service;

public interface IEmailService {
    void sendPlainText(String to, String subject, String body);
}
