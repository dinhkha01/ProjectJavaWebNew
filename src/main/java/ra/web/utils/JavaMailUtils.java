package ra.web.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Component
public class JavaMailUtils {
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private JavaMailSender mailSender;
    public void sendEmailWithAttachment(
            String recipientEmail,
//            List<String> ccEmails,
//            List<String> bccEmails,
            String subject,
//            String htmlContent,
            List<MultipartFile> attachments) throws MessagingException, IOException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // 'true' for multipart, 'UTF-8' for encoding



        helper.setTo(recipientEmail);
        helper.setSubject(subject);

        // lấy nội dung file thymeleaf
        Context context = new Context();

        // 2. Add variables (attributes) to the Context
        // You can add individual variables like this:
        context.setVariable("name", "Nguyen Van A");
        context.setVariable("userId", "J103278");
        context.setVariable("registrationDate", LocalDate.now().toString());

       String htmlContent = templateEngine.process("mail-template", context);

        helper.setText(htmlContent, true); // 'true' indicates HTML content

//        if (ccEmails != null && !ccEmails.isEmpty()) {
//            helper.setCc(ccEmails.toArray(new String[0]));
//        }
//
//        if (bccEmails != null && !bccEmails.isEmpty()) {
//            helper.setBcc(bccEmails.toArray(new String[0]));
//        }

        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile attachment: attachments) {
                helper.addAttachment(attachment.getOriginalFilename(), new ByteArrayResource(attachment.getBytes()));
            }
        }
        mailSender.send(message);
    }

    // Overload method if you want to send without attachment
    public void sendEmail(
            String recipientEmail,
            List<String> ccEmails,
            List<String> bccEmails,
            String subject,
            String htmlContent) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        if (ccEmails != null && !ccEmails.isEmpty()) {
            helper.setCc(ccEmails.toArray(new String[0]));
        }

        if (bccEmails != null && !bccEmails.isEmpty()) {
            helper.setBcc(bccEmails.toArray(new String[0]));
        }

        mailSender.send(message);
    }
}

