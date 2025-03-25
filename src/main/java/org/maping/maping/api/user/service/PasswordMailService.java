package org.maping.maping.api.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PasswordMailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private String loadHtmlTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/password-reset.html");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("이메일 템플릿을 로드하는 중 오류 발생", e);
        }
    }

    // 이메일을 전송하는 메서드 (변경 링크 없이 이메일만 전송)
    public void sendPasswordResetEmail(String email) throws MessagingException {
        String htmlContent = loadHtmlTemplate();  // 템플릿 로드

        MimeMessage message = createMail(email, htmlContent);  // HTML 내용으로 이메일 생성
        try {
            javaMailSender.send(message);  // 이메일 발송
        } catch (MailSendException e) {
            throw new MailSendException("이메일 발송에 실패했습니다.", e);
        }
    }

    // 이메일 생성 메서드 (HTML 내용 포함)
    private MimeMessage createMail(String mail, String htmlContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("[MA-PING] 비밀번호 변경");
        message.setContent(htmlContent, "text/html; charset=UTF-8");  // HTML 콘텐츠 설정
        return message;
    }
}


