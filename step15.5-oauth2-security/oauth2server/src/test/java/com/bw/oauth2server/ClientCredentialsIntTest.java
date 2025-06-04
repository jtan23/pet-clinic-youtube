package com.bw.oauth2server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ClientCredentialsIntTest {

	private static final String CLIENT_ID = "api-client";
	private static final String CLIENT_SECRET = "api-client";
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private MockMvc mockMvc;

	@Test
	void testTokenWithSingleScope() throws Exception {
		String response = mockMvc.perform(post("/oauth2/token")
						.param("grant_type", "client_credentials")
						.param("scope", "read")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.access_token").isString())
				.andExpect(jsonPath("$.expires_in").isNumber())
				.andExpect(jsonPath("$.scope").value("read"))
				.andExpect(jsonPath("$.token_type").value("Bearer"))
				.andReturn().getResponse().getContentAsString();
		System.out.println("Response [" + response + "]");
	}

	@Test
	void testTokenWithMultipleScopes() throws Exception {
		mockMvc.perform(post("/oauth2/token")
						.param("grant_type", "client_credentials")
						.param("scope", "read write")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.access_token").isString())
				.andExpect(jsonPath("$.expires_in").isNumber())
				.andExpect(jsonPath("$.scope").value("read write"))
				.andExpect(jsonPath("$.token_type").value("Bearer"));
	}

	@Test
	void testTokenWithInvalidScope() throws Exception {
		mockMvc.perform(post("/oauth2/token")
						.param("grant_type", "client_credentials")
						.param("scope", "update")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("invalid_scope"));
	}

	@Test
	void testTokenWithInvalidCredential() throws Exception {
		mockMvc.perform(post("/oauth2/token")
						.param("grant_type", "client_credentials")
						.param("scope", "read")
						.with(httpBasic("bad", "password")))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error").value("invalid_client"));
	}

	@Test
	void testTokenWithUnregisteredGrantType() throws Exception {
		mockMvc.perform(post("/oauth2/token")
						.param("grant_type", "authorization_code")
						.param("scope", "read")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("invalid_request"));
	}

	@Test
	void testTokenWithUnsupportedGrantType() throws Exception {
		mockMvc.perform(post("/oauth2/token")
						.param("grant_type", "invalid")
						.param("scope", "read")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("unsupported_grant_type"));
	}

	@Test
	void testIntrospect() throws Exception {
		String response = mockMvc.perform(post("/oauth2/introspect")
						.param("token", getAccessToken())
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
				.andExpect(status().isOk())
				// Token active or not
				.andExpect(jsonPath("$.active").value("true"))
				// Token Subject, principal's ID
				.andExpect(jsonPath("$.sub").value(CLIENT_ID))
				// Token Intended Audiences
				.andExpect(jsonPath("$.aud[0]").value(CLIENT_ID))
				// Token Not To Be Used Before, Unix timestamp
				.andExpect(jsonPath("$.nbf").isNumber())
				// Token scopes, space-separated list
				.andExpect(jsonPath("$.scope").value("read"))
				// Subject's Roles, we added it manually
				.andExpect(jsonPath("$.roles", hasSize(0)))
				// Token Issuer, default http://localhost
				.andExpect(jsonPath("$.iss").value(startsWith("http://localhost")))
				// Token Expiration, Unix timestamp
				.andExpect(jsonPath("$.exp").isNumber())
				// Token Issued At, Unix timestamp
				.andExpect(jsonPath("$.iat").isNumber())
				// JWT Token Identifier
				.andExpect(jsonPath("$.jti").isString())
				// Token Client
				.andExpect(jsonPath("$.client_id").value(CLIENT_ID))
				// Token Type
				.andExpect(jsonPath("$.token_type").value("Bearer"))
				.andReturn().getResponse().getContentAsString();
		System.out.println("Response [" + response + "]");
	}

	@Test
	void testIntrospectWithInvalidAccessToken() throws Exception {
		String response = mockMvc.perform(post("/oauth2/introspect")
						.param("token", "invalid_token")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active").value("false"))
				.andReturn().getResponse().getContentAsString();
		System.out.println("Response [" + response + "]");
	}

	private String getAccessToken() throws Exception {
		MvcResult mvcResult = mockMvc.perform(post("/oauth2/token")
						.param("grant_type", "client_credentials")
						.param("scope", "read")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.access_token").exists())
				.andReturn();
		String tokenResponseJson = mvcResult.getResponse().getContentAsString();
		Map<String, Object> tokenResponse = this.objectMapper.readValue(tokenResponseJson, new TypeReference<>() {});
		return tokenResponse.get("access_token").toString();
	}

}
