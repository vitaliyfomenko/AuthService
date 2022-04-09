package net.absoft.services;

import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.db.DbConnection;

public class AuthenticationService {

  public Response authenticate(String email, String password) {
    if(email.isEmpty()) {
      return new Response(400, "Email should not be empty string");
    }

    if(!validateEmail(email)) {
      return new Response(400, "Invalid email");
    }

    if(password.isEmpty()) {
      return new Response(400, "Password should not be empty string");
    }

    if(DbConnection.getInstance().authenticate(email, password)) {
      return new Response(200, "Authentication successful");
    } else {
      return new Response(401, "Invalid email or password");
    }
  }

  /**
   * Returns true if email address matches RFC 5322 regex.
   * https://www.rfc-editor.org/info/rfc5322
   * @param emailAddress
   * @return true if email address matches regex
   */
  private boolean validateEmail(String emailAddress) {
    final String REGEX_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    return Pattern.compile(REGEX_PATTERN)
        .matcher(emailAddress)
        .matches();
  }
}
