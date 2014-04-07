package org.jamesdbloom.integration;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jamesdbloom.acceptance.login.LoginPage;
import org.jamesdbloom.acceptance.registration.RegistrationPage;
import org.jamesdbloom.acceptance.updatepassword.UpdatePasswordPage;
import org.jamesdbloom.email.NewRegistrationEmail;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * @author jamesdbloom
 */
public class SystemTest {

    private static Tomcat tomcat;
    private static Wiser wiser;
    private static KeyStore trustStore;

    @BeforeClass
    public static void setupFixture() throws Exception {
        // setup mock smtp
        System.setProperty("email.host", "127.0.0.1");
        System.setProperty("email.port", "2500");
        System.setProperty("email.starttls", "false");
        wiser = new Wiser();
        wiser.setPort(2500);
        wiser.start();

        // determine current filesystem location
        String classLocation = SystemTest.class.getCanonicalName().replace(".", "/") + ".class";
        String projectBase = SystemTest.class.getClassLoader().getResource(classLocation).toString().replace(classLocation, "../../").replace("file:", "");

        // start proxy (in tomcat)
        tomcat = new Tomcat();
        tomcat.setBaseDir(new File(".").getCanonicalPath() + File.separatorChar + "tomcat");

        // add http port
        tomcat.setPort(8080);

        // add https port
        Connector httpsConnector = new Connector();
        httpsConnector.setPort(8443);
        httpsConnector.setSecure(true);
        httpsConnector.setScheme("https");
        httpsConnector.setAttribute("keystorePass", "changeit");
        httpsConnector.setAttribute("keystoreFile", projectBase + "keystore");
        httpsConnector.setAttribute("clientAuth", "false");
        httpsConnector.setAttribute("sslProtocol", "TLS");
        httpsConnector.setAttribute("SSLEnabled", true);
        Service service = tomcat.getService();
        service.addConnector(httpsConnector);

        // add servlet
        Context ctx = tomcat.addContext("/", projectBase + "src/main/webapp");
        ContextConfig contextConfig = new ContextConfig();
        ctx.addLifecycleListener(contextConfig);
        contextConfig.setDefaultWebXml(projectBase + "src/main/webapp/WEB-INF/web.xml");

        // control logging level
        java.util.logging.Logger.getLogger("").setLevel(Level.FINER);

        // start server
        tomcat.start();

        // load key store for certificates
        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fileInputStream = new FileInputStream(new File(projectBase + "keystore"))) {
            trustStore.load(fileInputStream, "changeit".toCharArray());
        }
    }

    @AfterClass
    public static void shutdownFixture() throws LifecycleException {
        // stop server
        tomcat.stop();

        // stop smtp mock
        wiser.stop();
    }

    @Test
    public void registerUpdatePasswordLoginAndViewPage() throws Exception {
        // given
        HttpClient httpClient = createApacheClient();

        // when - register
        HttpResponse registerPageResponse = httpClient.execute(new HttpGet("https://127.0.0.1:8443/register"));
        RegistrationPage registrationPage = new RegistrationPage(getBodyAndClose(registerPageResponse));
        registrationPage.shouldHaveCorrectFields();
        String csrf = registrationPage.csrfValue();

        HttpPost register = new HttpPost("https://127.0.0.1:8443/register");
        register.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("name", "test_user"),
                new BasicNameValuePair("email", "fake@email.com"),
                new BasicNameValuePair("_csrf", csrf)
        )));
        HttpResponse registerResponse = httpClient.execute(register);

        // then - should respond with success message
        assertThat(registerResponse.getStatusLine().getStatusCode(), is(HttpStatus.SC_MOVED_TEMPORARILY));
        assertThat(registerResponse.getFirstHeader("Location").getValue(), is("https://127.0.0.1:8443/message"));
        getBodyAndClose(registerResponse);

        // then - should have sent correct email
        TimeUnit.SECONDS.sleep(2);
        assertThat(wiser.getMessages(), is(not(empty())));
        WiserMessage registrationWiserMessage = wiser.getMessages().get(0);
        MimeMessage mimeMessage = registrationWiserMessage.getMimeMessage();
        NewRegistrationEmail registrationEmail = new NewRegistrationEmail(mimeMessage.getContent().toString());
        String updatePasswordURL = registrationEmail.shouldHaveCorrectFields("fake@email.com");

        // when - updating password
        HttpResponse updatePasswordPageResponse = httpClient.execute(new HttpGet(updatePasswordURL));
        UpdatePasswordPage updatePasswordPage = new UpdatePasswordPage(getBodyAndClose(updatePasswordPageResponse));
        updatePasswordPage.shouldHaveCorrectFields();
        csrf = updatePasswordPage.csrfValue();

        HttpPost updatePasswordRequest = new HttpPost(updatePasswordURL);
        updatePasswordRequest.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("password", "NewPassword123"),
                new BasicNameValuePair("passwordConfirm", "NewPassword123"),
                new BasicNameValuePair("_csrf", csrf)
        )));
        HttpResponse updatePasswordResponse = httpClient.execute(updatePasswordRequest);

        // then - should respond with success message
        assertThat(updatePasswordResponse.getStatusLine().getStatusCode(), is(HttpStatus.SC_MOVED_TEMPORARILY));
        assertThat(updatePasswordResponse.getFirstHeader("Location").getValue(), is("https://127.0.0.1:8443/message"));
        getBodyAndClose(updatePasswordResponse);

        // when - hit secured page
        HttpResponse landingPageResponse = httpClient.execute(new HttpGet("http://127.0.0.1:8080/"));

        // then - login page is displayed
        LoginPage securePageResponse = new LoginPage(getBodyAndClose(landingPageResponse));
        securePageResponse.shouldHaveCorrectFields();
        csrf = securePageResponse.csrfValue();

        // when - login is performed
        HttpPost login = new HttpPost("https://127.0.0.1:8443/login");
        login.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("username", "fake@email.com"),
                new BasicNameValuePair("password", "NewPassword123"),
                new BasicNameValuePair("_csrf", csrf)
        )));
        HttpResponse loginResponse = httpClient.execute(login);

        // then - secured page is displayed
        assertThat(loginResponse.getStatusLine().getStatusCode(), is(HttpStatus.SC_MOVED_TEMPORARILY));
        assertThat(loginResponse.getFirstHeader("Location").getValue(), is("https://127.0.0.1:8443/"));
        getBodyAndClose(loginResponse);

        // when - logout
        httpClient = createApacheClient();
        HttpResponse logoutResponse = httpClient.execute(new HttpGet("https://127.0.0.1:8443/logout"));

        // then - should get redirected to login page
        LoginPage logoutRequestResponse = new LoginPage(getBodyAndClose(logoutResponse));
        logoutRequestResponse.shouldHaveCorrectFields();
    }

    private String getBodyAndClose(HttpResponse httpResponse) throws IOException {
        String body = EntityUtils.toString(httpResponse.getEntity());
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        return body;
    }

    private CloseableHttpClient createApacheClient() throws Exception {
        return HttpClients.custom()
                // make sure the tests don't block when server fails to start up
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout((int) TimeUnit.SECONDS.toMillis(4))
                        .build())
                .setSslcontext(
                        SSLContexts
                                .custom()
                                .loadTrustMaterial(trustStore, new TrustStrategy() {
                                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                        return true;
                                    }
                                })
                                .build()
                )
                .setHostnameVerifier(new AllowAllHostnameVerifier())
                .build();
    }
}
