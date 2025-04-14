package com.PFE.DTT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String verificationCode) throws MessagingException {
        logger.info("Attempting to send verification email to: {}", to);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Email Verification");
        helper.setFrom("zaagkhalyl@gmail.com");

        String htmlContent = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Email Verification</title>
              <style>
                body {
                  font-family: Arial, sans-serif;
                  background-color: #e8ecef;
                  margin: 0;
                  padding: 0;
                }
                .container {
                  max-width: 600px;
                  margin: 0 auto;
                  background-color: #ffffff;
                  border-radius: 0;
                  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                }
                .header {
                  background-color: #003087;
                  color: #ffffff;
                  text-align: center;
                  padding: 20px 0;
                }
                .header img {
                  max-width: 150px;
                  margin-bottom: 10px;
                }
                .header h1 {
                  font-size: 24px;
                  margin: 0;
                  text-transform: uppercase;
                }
                .content {
                  text-align: center;
                  padding: 40px 20px;
                }
                .content p {
                  color: #666666;
                  font-size: 16px;
                  line-height: 1.5;
                  margin: 0 0 20px;
                }
                .button {
                  display: inline-block;
                  background-color: #ff6200;
                  color: #ffffff;
                  text-decoration: none;
                  padding: 12px 30px;
                  font-size: 16px;
                  font-weight: bold;
                  border-radius: 5px;
                  text-transform: uppercase;
                }
                .signature {
                  text-align: center;
                  color: #333333;
                  font-size: 16px;
                  margin-top: 20px;
                }
                .contact {
                  background-color: #f4f4f4;
                  text-align: center;
                  padding: 20px;
                  color: #003087;
                  font-size: 14px;
                }
                .contact p {
                  margin: 5px 0;
                }
                .social-icons {
                  margin: 10px 0;
                }
                .social-icons img {
                  width: 24px;
                  margin: 0 5px;
                }
                .footer {
                  background-color: #003087;
                  color: #ffffff;
                  text-align: center;
                  padding: 10px;
                  font-size: 12px;
                }
              </style>
            </head>
            <body>
            <div class="container">
              <div class="header">
                <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/f/f4/Leoni_AG_Logo.svg/2560px-Leoni_AG_Logo.svg.png" alt="Company Logo">
                <h1>Thanks for Signing Up!</h1>
                <h1>Verify Your E-mail Address</h1>
              </div>
              <div class="content">
                <p>Hi,</p>
                <p>You're almost ready to get started. Please click on the button below to verify your email address and enjoy exclusive cleaning services with us!</p>
                <a href="#" class="button">VERIFICATION CODE %s</a>
              </div>
              <div class="signature">
                <p>Thanks,</p>
                <p>The Company Team</p>
              </div>
              <div class="contact">
                <p><strong>Get in touch</strong></p>
                <p>+11 113 323 4444</p>
                <p>info@yourcompany.com</p>
                <div class="social-icons">
                  <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/733/733547.png" alt="Facebook" width="24"></a>
                  <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/174/174857.png" alt="LinkedIn" width="24"></a>
                  <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/2111/2111463.png" alt="Instagram" width="24"></a>
                  <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/3670/3670147.png" alt="YouTube" width="24"></a>
                  <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/5968/5968534.png" alt="Email" width="24"></a>
                </div>
              </div>
              <div class="footer">
                <p>Copyright © Company. All Rights Reserved.</p>
              </div>
            </div>
            </body>
            </html>
            """.formatted(verificationCode);

        helper.setText(htmlContent, true);

        try {
            mailSender.send(message);
            logger.info("Verification email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}. Error: {}", to, e.getMessage(), e);
            throw e;
        }
    }
}