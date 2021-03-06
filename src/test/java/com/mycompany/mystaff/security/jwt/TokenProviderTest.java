package com.mycompany.mystaff.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.mycompany.mystaff.web.rest.TestUtil;

import io.github.jhipster.config.JHipsterProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenProviderTest {

  private final String secretKey = "e5c9ee274ae87bc031adda32e27fa98b9290da83";
  private final long ONE_MINUTE = 60000;
  private JHipsterProperties jHipsterProperties;
  private TokenProvider tokenProvider;

  @Before
  public void setup() {
    jHipsterProperties = Mockito.mock(JHipsterProperties.class);
    tokenProvider = new TokenProvider(jHipsterProperties);
    ReflectionTestUtils.setField(tokenProvider, "secretKey", secretKey);
    ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", ONE_MINUTE);
  }

  @Test
  public void testReturnFalseWhenJWThasInvalidSignature() {
    boolean isTokenValid = tokenProvider.validateToken(createTokenWithDifferentSignature());

    assertThat(isTokenValid).isEqualTo(false);
  }

  @Test
  public void testReturnFalseWhenJWTisMalformed() {
    Authentication authentication = TestUtil.createAuthentication();
    String token = tokenProvider.createToken(authentication, false, 0L);
    String invalidToken = token.substring(1);
    boolean isTokenValid = tokenProvider.validateToken(invalidToken);

    assertThat(isTokenValid).isEqualTo(false);
  }

  @Test
  public void testReturnFalseWhenJWTisExpired() {
    ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", -ONE_MINUTE);

    Authentication authentication = TestUtil.createAuthentication();
    String token = tokenProvider.createToken(authentication, false, 0L);

    boolean isTokenValid = tokenProvider.validateToken(token);

    assertThat(isTokenValid).isEqualTo(false);
  }

  @Test
  public void testReturnFalseWhenJWTisUnsupported() {
    String unsupportedToken = createUnsupportedToken();

    boolean isTokenValid = tokenProvider.validateToken(unsupportedToken);

    assertThat(isTokenValid).isEqualTo(false);
  }

  @Test
  public void testReturnFalseWhenJWTisInvalid() {
    boolean isTokenValid = tokenProvider.validateToken("");

    assertThat(isTokenValid).isEqualTo(false);
  }

  private String createUnsupportedToken() {
    return Jwts.builder().setPayload("payload").signWith(SignatureAlgorithm.HS512, secretKey).compact();
  }

  private String createTokenWithDifferentSignature() {
    return Jwts.builder().setSubject("anonymous").signWith(SignatureAlgorithm.HS512, "e5c9ee274ae87bc031adda32e27fa98b9290da90")
        .setExpiration(new Date(new Date().getTime() + ONE_MINUTE)).compact();
  }

}
