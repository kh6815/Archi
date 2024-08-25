package com.architecture.archi.common.email;

import com.architecture.archi.common.email.model.EmailMessage;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonEmail {

    private final JavaMailSender javaMailSender;

    public Boolean sendMail(EmailMessage emailMessage) throws CustomException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo()); // 메일 수신자
            mimeMessageHelper.setSubject(emailMessage.getSubject()); // 메일 제목
            mimeMessageHelper.setText(emailMessage.getMessage()); // 메일 본문 내용
//            mimeMessageHelper.setText(emailMessage.getMessage(), true); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage);

            return true;

        } catch (MessagingException e) {
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, "이메일 발송 실패");
        }
    }

    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // 이메일 주소를 '@'를 기준으로 분리
        String[] parts = email.split("@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // '@' 앞 부분 처리 (최소 2글자)
        String localPart = parts[0];
        String maskedLocalPart;
        if (localPart.length() <= 2) {
            maskedLocalPart = localPart;
        } else {
            maskedLocalPart = localPart.substring(0, 2) + "xxx";
        }

        // 도메인 부분 처리 (최소 3글자)
        String domainPart = parts[1];
        String maskedDomainPart;
        if (domainPart.length() <= 3) {
            maskedDomainPart = domainPart;
        } else {
            maskedDomainPart = domainPart.substring(0, 3) + "xxx";
        }

        // 최종 이메일 주소 조합
        return maskedLocalPart + "@" + maskedDomainPart;
    }

}
