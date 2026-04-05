package tn.esprit.esprit_market.modules.shared.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendEmail() {
        emailService.sendEmail("test@mail.com", "Hello Subject", "Body");

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage captured = messageCaptor.getValue();
        assertEquals("test@mail.com", Objects.requireNonNull(captured.getTo())[0]);
        assertEquals("Hello Subject", captured.getSubject());
        assertEquals("Body", captured.getText());
        assertEquals("esprit.market@gmail.com", captured.getFrom());
    }
}
