package vn.edu.fpt.be.service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

public interface ForgotPasswordService {

    String generateToken();
    LocalDateTime expireTimeRange();
    void sendEmail(String to, String subject, String emailLink) throws MessagingException, UnsupportedEncodingException;
    void requestForgotPassword(String email);
}
