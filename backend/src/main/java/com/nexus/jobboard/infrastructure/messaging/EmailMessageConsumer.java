package com.nexus.jobboard.infrastructure.messaging;

import com.nexus.jobboard.application.dto.message.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Email message consumer following SRP
 * - Single responsibility: Process email messages from queue
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailMessageConsumer {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void processEmailMessage(EmailMessage emailMessage) {
        log.info("Processing email message for: {}", emailMessage.getTo());
        
        try {
            sendEmail(emailMessage);
            log.info("Email sent successfully to: {}", emailMessage.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to: {}, error: {}", emailMessage.getTo(), e.getMessage());
            // In production, you might want to implement retry logic or dead letter queue
        }
    }
    
    private void sendEmail(EmailMessage emailMessage) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        helper.setTo(emailMessage.getTo());
        helper.setSubject(emailMessage.getSubject());
        
        if (emailMessage.getFrom() != null) {
            helper.setFrom(emailMessage.getFrom());
        }
        
        String content = generateEmailContent(emailMessage);
        helper.setText(content, true);
        
        mailSender.send(mimeMessage);
    }
    
    private String generateEmailContent(EmailMessage emailMessage) {
        if (emailMessage.getHtmlContent() != null) {
            return emailMessage.getHtmlContent();
        }
        
        if (emailMessage.getTemplateName() != null && emailMessage.getTemplateVariables() != null) {
            Context context = new Context();
            emailMessage.getTemplateVariables().forEach(context::setVariable);
            return templateEngine.process(emailMessage.getTemplateName(), context);
        }
        
        return emailMessage.getTextContent() != null ? emailMessage.getTextContent() : "";
    }
}
