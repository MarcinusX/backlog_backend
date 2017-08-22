package com.swimHelper.service;

import com.swimHelper.exception.EmailFailedException;
import com.swimHelper.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(EmailMessage emailMessage) throws EmailFailedException {
        try {
            MimeMessage mimeMessage = convertMessage(emailMessage);
            mailSender.send(mimeMessage);
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new EmailFailedException(e);
        }
    }

    private MimeMessage convertMessage(EmailMessage emailMessage) throws UnsupportedEncodingException, MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.setFrom(
                new InternetAddress(emailMessage.getFrom().getEmailAddress(), emailMessage.getFrom().getName()));
        mimeMessage.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(emailMessage.getTo().getEmailAddress(), emailMessage.getTo().getName()));
        mimeMessage.setSubject(emailMessage.getSubject());
        mimeMessage.setText(emailMessage.getTextContent());
        return mimeMessage;
    }
}
