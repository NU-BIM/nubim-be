package com.soyeon.nubim.security.oauth;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.nubim.security.oauth.exception.AppleKeyException;
import com.soyeon.nubim.security.oauth.exception.InvalidAppleIdTokenException;
import com.soyeon.nubim.security.oauth.exception.TokenProcessingException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

@Component
public class AppleIdTokenValidator {

	private static final String APPLE_PUBLIC_KEY_URL = "https://appleid.apple.com/auth/keys";

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate = new RestTemplate();

	public void validateAppleIdToken(String idToken) {
		try {
			Map<String, String> algAndKid = extractAlgAndKidFromIdToken(idToken);
			JsonNode applePublicKeys = fetchApplePublicKeys();
			JsonNode matchedApplePublicKey = findMatchingPublicKey(applePublicKeys, algAndKid.get("alg"),
				algAndKid.get("kid"));
			PublicKey publicKey = generatePublicKey(matchedApplePublicKey);

			JwtParser jwtParser = Jwts.parserBuilder()
				.setSigningKey(publicKey)
				.build();

			Jws<Claims> claims = jwtParser.parseClaimsJws(idToken);
			validateExpiration(claims);
		} catch (AppleKeyException | InvalidAppleIdTokenException e) {
			throw e;
		} catch (Exception e) {
			throw new TokenProcessingException("Unexpected error: " + e.getMessage());
		}
	}

	private void validateExpiration(Jws<Claims> claims) {
		Date expiration = claims.getBody().getExpiration();
		Date now = new Date();
		if (expiration.before(now)) {
			throw new InvalidAppleIdTokenException("Token has expired");
		}
	}

	private Map<String, String> extractAlgAndKidFromIdToken(String idToken) {
		Map<String, String> algAndKid = new HashMap<>();
		String[] parts = idToken.split("\\.");
		if (parts.length != 3) { //Header, Payload, Signature
			throw new InvalidAppleIdTokenException("Invalid JWT format");
		}
		String header = parts[0];
		Base64.Decoder decoder = Base64.getUrlDecoder();
		try {
			JsonNode headerContent = objectMapper.readTree(new String(decoder.decode(header)));
			algAndKid.put("alg", headerContent.get("alg").asText());
			algAndKid.put("kid", headerContent.get("kid").asText());
			return algAndKid;
		} catch (JsonProcessingException e) {
			throw new TokenProcessingException("Failed to parse JWT header: " + e.getMessage());
		}
	}

	private JsonNode fetchApplePublicKeys() {
		ResponseEntity<String> response = restTemplate.getForEntity(APPLE_PUBLIC_KEY_URL, String.class);
		try {
			JsonNode publicKeysResponse = objectMapper.readTree(response.getBody());
			return publicKeysResponse.get("keys");
		} catch (Exception e) {
			throw new AppleKeyException("Failed to fetch public keys: " + e.getMessage());
		}
	}

	private JsonNode findMatchingPublicKey(JsonNode applePublicKeys, String alg, String kid) {
		if (applePublicKeys == null || applePublicKeys.isEmpty()) {
			throw new AppleKeyException("Apple key info not received");
		}
		for (JsonNode key : applePublicKeys) {
			String algFromKey = key.get("alg").asText();
			String kidFromKey = key.get("kid").asText();
			if (Objects.equals(algFromKey, alg) && Objects.equals(kidFromKey, kid)) {
				return key;
			}
		}
		throw new AppleKeyException("Matching public key not found");
	}

	private PublicKey generatePublicKey(JsonNode matchedApplePublicKey) {
		Base64.Decoder decoder = Base64.getUrlDecoder();
		byte[] modulusBytes = decoder.decode(matchedApplePublicKey.get("n").asText());
		byte[] exponentBytes = decoder.decode(matchedApplePublicKey.get("e").asText());

		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
			new BigInteger(1, modulusBytes),
			new BigInteger(1, exponentBytes)
		);
		try {
			String keyType = matchedApplePublicKey.get("kty").asText();
			KeyFactory keyFactory = KeyFactory.getInstance(keyType);
			return keyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new AppleKeyException("generating public key: " + e.getMessage());
		}
	}
}
