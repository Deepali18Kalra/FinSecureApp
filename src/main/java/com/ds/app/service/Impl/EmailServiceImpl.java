package com.ds.app.service.Impl;

import com.ds.app.entity.Employee;
import com.ds.app.entity.Leave;
import com.ds.app.enums.ApprovalStatus;
import com.ds.app.service.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Override
    public void sendPlainText(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    @Override
    public void notifyHrForNewLeave(Employee employee, Leave leave) {
        Employee hr = employee.getHr();
        if (hr == null || hr.getEmail() == null || hr.getEmail().isBlank()) return;

        String subject = "New Leave Request - " + employee.getFirstName() + " " + employee.getLastName();
        String body = "Hello " + hr.getFirstName() + ",\n\n"
                + employee.getFirstName() + " " + employee.getLastName() + " has applied for leave.\n\n"
                + "Type: " + leave.getLeaveType() + "\n"
                + "Dates: " + leave.getStartDate() + " to " + leave.getEndDate() + "\n"
                + "Days: " + leave.getTotalDays() + "\n"
                + "Reason: " + leave.getReasonForLeave() + "\n"
                + "Status: " + leave.getStatus() + "\n\n"
                + "Please review it in the portal.";
        sendPlainText(hr.getEmail(), subject, body);
    }

    @Override
    public void notifyEmployeeForLeaveDecision(Employee employee, Leave leave) {
        if (employee.getEmail() == null || employee.getEmail().isBlank()) return;

        String subject = "Leave Request " + leave.getStatus();
        String body = "Hello " + employee.getFirstName() + ",\n\n"
                + "Your leave request has been " + leave.getStatus() + ".\n\n"
                + "Type: " + leave.getLeaveType() + "\n"
                + "Dates: " + leave.getStartDate() + " to " + leave.getEndDate() + "\n"
                + "Days: " + leave.getTotalDays() + "\n"
                + (leave.getStatus().name().equals("REJECTED")
                ? "Reason: " + (leave.getRejectionReason() == null ? "" : leave.getRejectionReason()) + "\n"
                : "")
                + "\nRegards,\nHR Team";
        sendPlainText(employee.getEmail(), subject, body);
    }

    @Override
    public void notifyHrForCancellationRequest(Employee employee, Leave leave) {
        Employee hr = employee.getHr();
        if (hr == null || hr.getEmail() == null || hr.getEmail().isBlank()) return;

        String subject = "Leave Cancellation Request - " + employee.getFirstName() + " " + employee.getLastName();
        String body = "Hello " + hr.getFirstName() + ",\n\n"
                + employee.getFirstName() + " " + employee.getLastName()
                + " has requested cancellation of an approved leave.\n\n"
                + "Type: " + leave.getLeaveType() + "\n"
                + "Dates: " + leave.getStartDate() + " to " + leave.getEndDate() + "\n"
                + "Days: " + leave.getTotalDays() + "\n"
                + "Current Status: " + leave.getStatus() + "\n\n"
                + "Please review the cancellation request in the portal.";
        sendPlainText(hr.getEmail(), subject, body);
    }

    @Override
    public void notifyEmployeeForCancellationDecision(Employee employee, Leave leave, ApprovalStatus decision, String reason) {
        if (employee.getEmail() == null || employee.getEmail().isBlank()) return;

        String subject = "Leave Cancellation Request " + leave.getStatus();
        String body = "Hello " + employee.getFirstName() + ",\n\n"
                + "Your leave cancellation request has been " + leave.getStatus() + ".\n\n"
                + "Type: " + leave.getLeaveType() + "\n"
                + "Dates: " + leave.getStartDate() + " to " + leave.getEndDate() + "\n"
                + "Days: " + leave.getTotalDays() + "\n"
                + (decision == ApprovalStatus.REJECTED ? "Reason: " + (reason == null ? "" : reason) + "\n" : "")
                + "\nRegards,\nHR Team";
        sendPlainText(employee.getEmail(), subject, body);
    }
}