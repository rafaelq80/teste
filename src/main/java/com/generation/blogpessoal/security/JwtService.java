package com.generation.blogpessoal.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
										
	private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
	private static final int EXPIRATION = 1000 * 60 * 60;
	
	private SecretKey getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSignKey()).build()
				.parseSignedClaims(token).getPayload();
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		if (token == null || token.trim().isEmpty())
            return null;
            
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Boolean isTokenExpired(String token) {
		Date expirationDate = extractExpiration(token);
        return expirationDate != null && expirationDate.before(new Date());
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		if (token == null || userDetails == null)
            return false;
            
		final String username = extractUsername(token);
		return username != null &&
				username.equals(userDetails.getUsername()) && 
				!isTokenExpired(token);
	}

	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder()
					.header()
						.type("JWT")
					.and()
					.claims(claims)
					.subject(userName)
					.issuedAt(new Date(System.currentTimeMillis()))
					.expiration(new Date(System.currentTimeMillis() + EXPIRATION))
					.signWith(getSignKey())
					.compact();
	}

	public String generateToken(String userName) {

		if (userName == null || userName.trim().isEmpty())
			throw new IllegalArgumentException("Username cannot be null or empty");

		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}

}