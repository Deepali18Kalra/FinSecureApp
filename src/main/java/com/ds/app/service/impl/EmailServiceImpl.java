package com.ds.app.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import com.ds.app.service.IEmailService;


import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class EmailServiceImpl implements IEmailService {


    @Autowired
    private JavaMailSender mailSender;   


    @Value("${spring.mail.username}")
    private String fromEmail;


    //  COMMON METHOD
    @Async
    public void sendEmail(String to, String subject, String body) {


        log.info("Sending email to: {} | Subject: {}", to, subject);


        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);


            mailSender.send(message);


            log.info("Email sent successfully to: {}", to);


        } catch (MailException e) {


            log.error("Email failed to: {} | Error: {}", to, e.getMessage(), e);
        }
    }


    // ENROLLMENT EMAIL
    @Override
    public void sendEnrollmentEmail(String to, String employeeName, String trainingName) {


        log.info("Preparing enrollment email for employee: {}", employeeName);


        String subject = "Enrolled in Training: " + trainingName;


        String body =
                "Dear " + employeeName + ",\n\n"
                        + "You have been enrolled in the following training:\n"
                        + "Training : " + trainingName + "\n\n"
                        + "Please login to dashboard.\n\n"
                        + "Regards,\nFinSecure HR Team";


        sendEmail(to, subject, body);
    }


    //  TRAINING START EMAIL
    @Override
    public void sendTrainingStartEmail(String to, String employeeName, String trainingName, String startDate) {


        log.info("Preparing training start email for: {}", employeeName);


        String subject = "Training Started: " + trainingName;


        String body =
                "Dear " + employeeName + ",\n\n"
                        + "Your training has started.\n"
                        + "Training: " + trainingName + "\n"
                        + "Start Date: " + startDate + "\n\n"
                        + "Regards,\nFinSecure HR Team";


        sendEmail(to, subject, body);
    }


    //  TRAINING COMPLETE EMAIL
    @Override
    public void sendTrainingCompleteEmail(String to, String employeeName, String trainingName) {


        log.info("Preparing training completion email for: {}", employeeName);


        String subject = "Training Completed: " + trainingName;


        String body =
                "Dear " + employeeName + ",\n\n"
                        + "Your training is completed.\n"
                        + "Training: " + trainingName + "\n\n"
                        + "Upload certification.\n\n"
                        + "Regards,\nFinSecure HR Team";


        sendEmail(to, subject, body);
    }


    //  CERT UPLOAD EMAIL (TO HR)
    @Override
    public void sendCertUploadEmail(String hrEmail, String employeeName, String certName) {


        log.info("Sending certification upload email to HR for employee: {}", employeeName);


        String subject = "Certification Uploaded — " + employeeName;


        String body =
                "Dear HR,\n\n"
                        + employeeName + " uploaded certification:\n"
                        + certName + "\n\n"
                        + "Please verify.\n\n"
                        + "FinSecure System";


        sendEmail(hrEmail, subject, body);
    }


    //  CERT VERIFIED EMAIL
    @Override
    public void sendCertVerificationEmail(String to, String employeeName, String certName) {


        log.info("Sending certification verification email to: {}", employeeName);


        String subject = "Certification Verified — " + certName;


        String body =
                "Dear " + employeeName + ",\n\n"
                        + "Your certification is verified.\n"
                        + "Certificate: " + certName + "\n\n"
                        + "You are eligible for projects.\n\n"
                        + "FinSecure HR Team";


        sendEmail(to, subject, body);
    }
}



