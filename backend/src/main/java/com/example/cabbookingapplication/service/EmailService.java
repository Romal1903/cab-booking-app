package com.example.cabbookingapplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetEmail(String to, String token) {

        String link = "http://localhost:5173/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Your Password");
        message.setText(
                "Click the link below to reset your password:\n\n" +
                link +
                "\n\nThis link is valid for 15 minutes."
        );

        mailSender.send(message);
    }

    // ---------- NEW METHOD FOR INVOICE EMAIL ----------
    public void sendInvoiceEmail(String to, String riderName, byte[] pdfBytes) {

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Your Ride Invoice");

            helper.setText(
                    "Hello " + riderName + ",\n\n" +
                    "Thank you for riding with us.\n\n" +
                    "Please find your ride invoice attached.\n\n" +
                    "Regards,\nCab Booking Team"
            );

            helper.addAttachment(
                    "ride-invoice.pdf",
                    new ByteArrayResource(pdfBytes)
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send invoice email", e);
        }
    }
}
