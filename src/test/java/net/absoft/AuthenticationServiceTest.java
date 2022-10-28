package net.absoft;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

public class AuthenticationServiceTest {

  private AuthenticationService authenticationService;
  @BeforeClass
  public void setUp(){
    System.out.println("setup--setup--setup");
  }

  @Test(
          groups = "positive"
  )
  public void testSuccessfulAuthentication() throws InterruptedException {
    Response response = new AuthenticationService().authenticate("user1@test.com", "password1");
    assertEquals(response.getCode(), 200, "Response code should be 200");
    assertTrue(validateToken(response.getMessage()),
        "Token should be the 32 digits string. Got: " + response.getMessage());

    Thread.sleep(2000);
    System.out.println("testSuccessfulAuthentication:" + new Date());
  }

  @Test(
          groups = "negative"
  )
  public void testAuthenticationWithWrongPassword() {
    Response response = new AuthenticationService()
        .authenticate("user1@test.com", "wrong_password1");
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(response.getCode(), 501, "Response code should be 401");
    sa.assertEquals(response.getMessage(), "BROKEN Invalid email or password",
        "Response message should be \"Invalid email or password\"");
    sa.assertAll();
  }

  @Test(
          groups = "negative"
  )
  public void testAuthenticationWithEmptyEmail() {
    Response response = new AuthenticationService().authenticate("", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Email should not be empty string",
        "Response message should be \"Email should not be empty string\"");
  }

  @Test(
          groups = "negative"
  )
  public void testAuthenticationWithInvalidEmail() {
    Response response = new AuthenticationService().authenticate("user1", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 200");
    assertEquals(response.getMessage(), "Invalid email",
        "Response message should be \"Invalid email\"");
  }

  @Test(
          groups = "negative"
  )
  public void testAuthenticationWithEmptyPassword() {
    Response response = new AuthenticationService().authenticate("user1@test", "");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Password should not be empty string",
        "Response message should be \"Password should not be empty string\"");
  }

  @DataProvider(name = "DataProviderTask", parallel = true)
public Object[][] invalidLogins(){
    return new Object[][]{
            new Object[]{"user1@test.com", "wrong_password1",
                    new Response(401, "Invalid email or password")},
            new Object[]{"", "wrong_password1",
                    new Response(400, "Email should not be empty string")},
            new Object[]{"user1@test.com", "",
                    new Response(400, "Password should not be empty string")},
            new Object[]{"user1", "wrong_password1",
                    new Response(400, "Invalid email")}
    };
}
  @Test(
          groups = "positive",
          dataProvider = "DataProviderTask"
  )
  public void testForDataProviderTask(String email, String password, Response expectedResponse) throws InterruptedException {
    Response actualResponse = new AuthenticationService()
            .authenticate(email, password);
    assertEquals(actualResponse.getCode(), expectedResponse.getCode(), "Unexpected code:");
    assertEquals(actualResponse.getMessage(), expectedResponse.getMessage(), "Unexpected message:");

    Thread.sleep(2000);
    System.out.println("testForDataProviderTask:" + new Date());
  }

  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }
}
