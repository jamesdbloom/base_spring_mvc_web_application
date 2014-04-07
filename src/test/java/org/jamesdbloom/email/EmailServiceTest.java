package org.jamesdbloom.email;

import org.jamesdbloom.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author jamesdbloom
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private Environment environment;
    @Mock
    private ThreadPoolTaskExecutor taskExecutor;
    @InjectMocks
    private EmailService emailService = new EmailService();
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;
    private ArgumentCaptor<MimeMessagePreparator> preparatorArgumentCaptor;
    private MimeMessage mimeMessage;

    @Before
    public void setupMocks() {
        runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        doNothing().when(taskExecutor).execute(runnableArgumentCaptor.capture());

        preparatorArgumentCaptor = ArgumentCaptor.forClass(MimeMessagePreparator.class);
        doNothing().when(mailSender).send(preparatorArgumentCaptor.capture());

        mimeMessage = new JavaMailSenderImpl().createMimeMessage();
    }

    @Test
    public void shouldSendEmail() throws Exception {
        // given
        String from = "from@email.com";
        String[] to = {"to@first-email.com", "to@second-email.com"};
        String subject = "subject";
        String msg = "<html>msg</html>";

        // when
        emailService.sendMessage(from, to, subject, msg);
        runnableArgumentCaptor.getValue().run();
        preparatorArgumentCaptor.getValue().prepare(mimeMessage);

        // then
        assertEquals(from, mimeMessage.getFrom()[0].toString());
        assertEquals(to[0], mimeMessage.getAllRecipients()[0].toString());
        assertEquals(to[1], mimeMessage.getAllRecipients()[1].toString());
        assertEquals(subject, mimeMessage.getSubject());
        assertEquals(msg, mimeMessage.getContent().toString());
    }

    @Test
    public void shouldSendRegistrationEmail() throws Exception {
        // given
        String token = "token";
        String email = "to@email.com";
        User user = new User()
                .withEmail(email)
                .withOneTimeToken(token);

        String hostName = "hostName";
        int port = 666;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Host")).thenReturn(hostName);
        when(request.getLocalPort()).thenReturn(port);

        String leagueEmail = "info@squash-league.com";
        when(environment.getProperty("email.contact.address")).thenReturn(leagueEmail);

        // when
        emailService.sendRegistrationMessage(user, request);
        runnableArgumentCaptor.getValue().run();
        preparatorArgumentCaptor.getValue().prepare(mimeMessage);

        // then
        String subject = EmailService.NAME_PREFIX + "New Registration";
        assertEquals(leagueEmail, mimeMessage.getFrom()[0].toString());
        assertEquals(email, mimeMessage.getAllRecipients()[0].toString());
        assertEquals(subject, mimeMessage.getSubject());

        assertEquals("<html><head><title>" + subject + "</title></head><body>\n" +
                "<h1>" + subject + "</h1>\n" +
                "<p>A new user has just been registered for " + email + "</p>\n" +
                "<p>To validate this email address please click on the following link <a href=https://" + hostName + ":" + port + "/updatePassword?email=to%40email.com&oneTimeToken=" + token + ">https://" + hostName + ":" + port + "/updatePassword?email=to%40email.com&oneTimeToken=" + token + "</a></p>\n" +
                "</body></html>", mimeMessage.getContent().toString());
    }

    @Test
    public void shouldSendUpdatePasswordEmail() throws Exception {
        // given
        String token = "token";
        String email = "to@email.com";
        User user = new User()
                .withEmail(email)
                .withOneTimeToken(token);

        String hostName = "hostName";
        int port = 666;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Host")).thenReturn(hostName);
        when(request.getLocalPort()).thenReturn(port);

        String leagueEmail = "info@squash-league.com";
        when(environment.getProperty("email.contact.address")).thenReturn(leagueEmail);

        // when
        emailService.sendUpdatePasswordMessage(user, request);
        runnableArgumentCaptor.getValue().run();
        preparatorArgumentCaptor.getValue().prepare(mimeMessage);

        // then
        String subject = EmailService.NAME_PREFIX + "Update Password";
        assertEquals(leagueEmail, mimeMessage.getFrom()[0].toString());
        assertEquals(email, mimeMessage.getAllRecipients()[0].toString());
        assertEquals(subject, mimeMessage.getSubject());

        assertEquals("<html><head><title>" + subject + "</title></head><body>\n" +
                "<h1>" + subject + "</h1>\n" +
                "<p>To update your password please click on the following link <a href=https://" + hostName + ":" + port + "/updatePassword?email=to%40email.com&oneTimeToken=" + token + ">https://" + hostName + ":" + port + "/updatePassword?email=to%40email.com&oneTimeToken=" + token + "</a></p>\n" +
                "</body></html>", mimeMessage.getContent().toString());
    }

    @Test
    public void shouldBuildCorrectURL() throws MalformedURLException, UnsupportedEncodingException {
        // given
        String hostName = "hostName";
        int port = 666;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Host")).thenReturn(hostName);
        when(request.getLocalPort()).thenReturn(port);

        // when
        URL actual = emailService.createUrl(new User().withEmail("to@email.com").withOneTimeToken("token"), request);

        // then
        assertEquals("https://" + hostName + ":" + port + "/updatePassword?email=to%40email.com&oneTimeToken=token", actual.toString());
    }

    @Test
    public void shouldBuildCorrectURLHostHeaderWithPort() throws MalformedURLException, UnsupportedEncodingException {
        // given
        String hostName = "hostName:12345";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Host")).thenReturn(hostName);
        when(request.getLocalPort()).thenReturn(666);

        // when
        URL actual = emailService.createUrl(new User().withEmail("to@email.com").withOneTimeToken("token"), request);

        // then
        assertEquals("https://" + hostName + "/updatePassword?email=to%40email.com&oneTimeToken=token", actual.toString());
    }
}

