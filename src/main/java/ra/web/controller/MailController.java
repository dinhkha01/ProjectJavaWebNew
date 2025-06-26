package ra.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ra.web.utils.JavaMailUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@Controller
public class MailController {
    @Autowired
    private JavaMailUtils mailUtils;
    @PostMapping("/sendmail")
    public String sendEmail(@RequestParam("file")List<MultipartFile> files) throws MessagingException, IOException {
        mailUtils.sendEmailWithAttachment("hung18061999hung@gmail.com","TEST EMAIL",files);
        return "home";
    }
}
