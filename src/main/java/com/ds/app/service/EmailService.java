package com.ds.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendSalaryCreditEmail(String toEmail,
                                      String employeeName,
                                      String month,
                                      double netSalary,
                                      String maskedAccount) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Salary Credited — " + month);
        message.setText(
            "Dear " + employeeName + ",\n\n" +
            "Your salary for " + month + " has been credited.\n\n" +
            "Amount: INR " + netSalary + "\n" +
            "Account: " + maskedAccount + "\n\n" +
            "Regards,\nFinSecure Finance Team"
        );

        mailSender.send(message);
    }

    @Async
    public void sendSalaryJobCompletedEmail(String toEmail,
                                            String jobName,
                                            int total,
                                            int success,
                                            int failed) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Salary Job Completed — " + jobName);
        message.setText(
            "Salary Job: " + jobName + " completed.\n\n" +
            "Total Employees : " + total + "\n" +
            "Successfully Credited : " + success + "\n" +
            "Failed : " + failed + "\n\n" +
            "Regards,\nFinSecure System"
        );

        mailSender.send(message);
    }
}