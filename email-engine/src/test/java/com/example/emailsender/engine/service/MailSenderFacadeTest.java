package com.example.emailsender.engine.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.emailsender.engine.exception.NoSenderStrategyException;
import com.example.emailsender.shared.event.EmailSendEvent.Priority;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.junit.jupiter.api.Test;

class MailSenderFacadeTest {
    @Test
    void delegatesToSupportingStrategy() {
        MailSenderStrategy strategy = mock(MailSenderStrategy.class);
        when(strategy.supports(Priority.NORMAL)).thenReturn(true);
        MailSenderFacade facade = new MailSenderFacade(List.of(strategy));

        facade.send(mock(MimeMessage.class), Priority.NORMAL);

        verify(strategy).send(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void throwsWhenNoStrategyMatches() {
        MailSenderStrategy strategy = mock(MailSenderStrategy.class);
        when(strategy.supports(Priority.HIGH)).thenReturn(false);
        MailSenderFacade facade = new MailSenderFacade(List.of(strategy));

        assertThatThrownBy(() -> facade.send(mock(MimeMessage.class), Priority.HIGH))
                .isInstanceOf(NoSenderStrategyException.class);
    }
}
