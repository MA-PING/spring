package org.maping.maping.api.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 인증번호를 저장하는 맵
    private final Map<String, VerificationData> verificationCodes = new ConcurrentHashMap<>();
    private static final long EXPIRATION_TIME = 5 * 60;

    private static class VerificationData {
        private final String code;
        private final long timestamp;

        public VerificationData(String code) {
            this.code = code;
            this.timestamp = Instant.now().getEpochSecond(); // 현재 초 단위 시간 저장
        }

        public boolean isExpired() {
            return Instant.now().getEpochSecond() - this.timestamp > EXPIRATION_TIME;
        }

        public String getCode() {
            return code;
        }
    }

    // 랜덤으로 숫자 생성
    public String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10)); // 0~9 사이 숫자
        }
        return key.toString();
    }


    private String loadHtmlTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/mail.template.html");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("이메일 템플릿을 로드하는 중 오류 발생", e);
        }
    }

    public MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("[MA-PING] 이메일 인증");

        // HTML 템플릿을 로드하고 인증번호를 삽입
        String htmlContent = loadHtmlTemplate().replace("{{authCode}}", number); // {{authCode}}로 대체
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        return message;
    }

    // 메일 발송 (일반 인증번호용)
    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        String number = createNumber(); // 랜덤 인증번호 생성

        MimeMessage message = createMail(sendEmail, number);
        try {
            javaMailSender.send(message);
            verificationCodes.put(sendEmail, new VerificationData(number)); // 이메일과 인증번호 저장
        } catch (MailException e) {
            e.printStackTrace();
            throw new MailSendException("메일 발송 중 오류가 발생했습니다.");
        }
        return number;
    }
    public boolean verifyCode(String email, String code) {
        VerificationData data = verificationCodes.get(email);

        if (data == null || data.isExpired()) {
            verificationCodes.remove(email); // 만료된 경우 삭제
            return false;
        }

        return data.getCode().equals(code);
    }
}
