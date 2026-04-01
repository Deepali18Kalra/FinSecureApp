package com.ds.app.service;

public interface IEmailService {
	
	void sendEnrollmentEmail(String to,String employeeName,String trainingName);
	
	void sendTrainingStartEmail(String to,String employeeName,String trainingName,String startDate);
	
	void sendTrainingCompleteEmail(String to,String employeeName,String trainingName);
	
	void sendCertUploadEmail(String hrEmail,String employeeName,String certName);
	
	void sendCertVerificationEmail(String to,String employeeName,String certName);

}
