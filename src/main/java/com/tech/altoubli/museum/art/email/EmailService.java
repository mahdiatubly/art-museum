package com.tech.altoubli.museum.art.email;

import com.tech.altoubli.museum.art.following_request.FollowingRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendAccountActivationEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        sendEmail(to, username, emailTemplate, "activate-account", subject, properties);
    }

    @Async
    public void sendFollowingRequestEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String declineUrl,
            String subject,
            FollowingRequest followingRequest
    ) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("declineUrl", declineUrl);
        properties.put("followingRequest", followingRequest);

        sendEmail(to, username, emailTemplate, "following-request", subject, properties);
    }

    private void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String defaultTemplate,
            String subject,
            Map<String, Object> additionalProperties
    ) throws MessagingException {
        String templateName = (emailTemplate != null) ? emailTemplate.name() : defaultTemplate;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<>(additionalProperties);
        properties.put("to", to);
        properties.put("username", username);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("notification@museum.art.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        mailSender.send(mimeMessage);
    }
}
