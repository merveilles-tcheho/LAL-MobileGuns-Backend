package com.nlmk.LAL.MobileGuns.security;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	@Value("${app.secret-key}")
	private String secretKey;

	@Value("${app.expiration-time}")
	private Integer expirationTime;

	public String generateToken(String userId, String userName, List<String> roles) {
		Map<String, Object> claims = new HashMap<>();

		claims.put("name", userName);
		claims.put("roles", roles);

		return createToken(claims, userId);
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.subject(subject)
				.claims(claims)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(getSignKey(), Jwts.SIG.HS256)
				.compact();
	}

	private SecretKey getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);

		return Keys.hmacShaKeyFor(keyBytes);
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		String userId = extractUserId(token);

		return (userDetails.getUsername().equals(userId) && !isTokenExpired(token));
	}

	public String extractUserId(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private Date extractExpirationDate(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaims(token);

		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
	}

	private boolean isTokenExpired(String token) {
		return extractExpirationDate(token).before(new Date());
	}

}

