package com.ds.app.service;

import com.ds.app.entity.Employee;
import com.ds.app.entity.Leave;
import com.ds.app.enums.ApprovalStatus;

public interface IEmailService {
    void sendPlainText(String to, String subject, String body);

    void notifyManagerForNewLeave(Employee employee, Leave leave);

    void notifyEmployeeForLeaveDecision(Employee employee, Leave leave);

    void notifyManagerForCancellationRequest(Employee employee, Leave leave);

    void notifyEmployeeForCancellationDecision(Employee employee, Leave leave, ApprovalStatus decision, String reason);
}