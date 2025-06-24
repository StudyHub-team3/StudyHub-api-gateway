package com.studyhub.api_gateway.secret.jwt;

import com.studyhub.api_gateway.secret.jwt.authentication.JwtAuthentication;
import com.studyhub.api_gateway.secret.jwt.authentication.UserPrincipal;
import com.studyhub.api_gateway.secret.jwt.domain.repository.BlacklistAccessTokenRepository;
import com.studyhub.api_gateway.secret.jwt.props.JwtConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {
    private final JwtConfigProperties jwtConfigProperties;
    private final BlacklistAccessTokenRepository blacklistAccessTokenRepository;

    private volatile SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            synchronized (this) {
                if (secretKey == null) {
                    secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfigProperties.getSecretKey()));
                }
            }
        }

        return secretKey;
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);

        if (authHeader != null && authHeader.startsWith("Bearer")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(jwtConfigProperties.getHeader());
    }

    public JwtAuthentication validateToken(String token) {
        final Claims claims = this.verifyAndGetClaims(token);

        if (claims == null) {
            return null;
        }

        String jti = claims.getId();
        if (jti == null || blacklistAccessTokenRepository.existsById(jti)) {
            return null;
        }

        Date expirationDate = claims.getExpiration();
        if (expirationDate == null || expirationDate.before(new Date())) {
            return null;
        }

        String tokenType = claims.get("tokenType", String.class);
        if (!"access".equals(tokenType)) {
            return null;
        }

        String userId = claims.getSubject();
        String userName = claims.get("userName", String.class);

        UserPrincipal principal = new UserPrincipal(userId, userName);

        return new JwtAuthentication(principal, token, getGrantedAuthorities("user"));
    }

    private Claims verifyAndGetClaims(String token) {
        Claims claims;

        try {
            claims = Jwts
                    .parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            claims = null;
        }

        return claims;
    }

    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if (role != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }

        return grantedAuthorities;
    }
}
