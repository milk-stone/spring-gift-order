package gift.domain.auth.jwt;

import gift.domain.member.Member;
import gift.global.exception.BadRequestException;
import gift.global.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtProvider {
    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliseconds;

    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-in-ms}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = expiration;
    }
    public String generateToken(Member member) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(member.getEmail())
                .claim("tokenType", "access")
                .claim("name", member.getName())
                .claim("role", member.getRole())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String extractEmailFromAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (!"access".equals(claims.get("tokenType", String.class))) {
            throw new BadRequestException("사용된 토큰이 엑세스 토큰이 아닙니다. 요청하신 로직에서는 엑세스 토큰으로만 처리가 가능합니다.");
        }
        if (claims.getExpiration().before(new Date())) {
            throw new TokenExpiredException("액세스 토큰이 만료되었습니다. 리프레시 토큰으로 다시 액세스 토큰을 발급받으세요.");
        }
        return claims.getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
