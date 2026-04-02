package com.ds.app.service;

import java.time.LocalDateTime;

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
                                            int failed,int skipped) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Salary Job Completed — " + jobName);
        message.setText(
            "Salary Job: " + jobName + " completed.\n\n" +
            "Total Employees : " + total + "\n" +
            "Successfully Credited : " + success + "\n" +
            "Skipped Credited : " + skipped + "\n" +
            "Failed : " + failed + "\n\n" +
            "Regards,\nFinSecure System"
        );

        mailSender.send(message);
    }
    
    @Async
    public void sendFraudAlertEmailToEmployee(String toEmail , String subject , Long Id , String firstName , String lastName , Integer modifiedAttempted , LocalDateTime coolDownPeriod)
    {
    	SimpleMailMessage message = new SimpleMailMessage();
    	
    	message.setTo(toEmail);
    	message.setSubject("🚨 Suspicious Bank Account Activity Detected");
    	message.setText("Employee ID: " + Id + "\n" +
                "Name: " +firstName
                + " " + lastName + "\n" +
       "Changed bank account " + modifiedAttempted
                + " times in 24 hours.\n" +
       "Account is now in cooldown until: "
                + coolDownPeriod + "\n\n" +
       "Please review immediately.");
    	mailSender.send(message);
    }
    
    @Async
    public void sendFraudAlertEmailToFinance(
            String toEmail,
            Long id,
            String firstName,
            String lastName,
            Integer modifiedAttempted,
            LocalDateTime coolDownPeriod
    ) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("🚨 Fraud Monitoring Alert – Bank Account Change Activity");

        message.setText(
                "Fraud monitoring systems have detected unusual activity related to " +
                "bank account modifications.\n\n" +

                "Employee Details:\n" +
                "-----------------\n" +
                "Employee ID   : " + id + "\n" +
                "Employee Name : " + firstName + " " + lastName + "\n\n" +

                "Incident Summary:\n" +
                "-----------------\n" +
                "• Number of bank account change attempts (last 24 hours): " + modifiedAttempted + "\n" +
                "• Automated cooldown enforced until: " + coolDownPeriod + "\n\n" +

                "Action Required:\n" +
                "---------------\n" +
                "Please review this activity for potential fraud risk and take " +
                "any necessary action in accordance with internal compliance policies.\n\n" +

                "This is a system‑generated alert."
        );

        mailSender.send(message);
    }
}