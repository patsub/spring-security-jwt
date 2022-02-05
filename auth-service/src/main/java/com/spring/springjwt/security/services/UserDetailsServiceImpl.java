package com.spring.springjwt.security.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spring.springjwt.models.AccessDetailsResponse;
import com.spring.springjwt.security.config.AuthTokenFilter;
import com.google.gson.Gson;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	String validateUserURL = null;

	private Properties myProperties;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			myProperties = new Properties();
			InputStream input = AuthTokenFilter.class.getClassLoader().getResourceAsStream("config.properties");

			myProperties.load(input);

			String authUrl = myProperties.getProperty("api.auth.url");
			validateUserURL = authUrl + "/api/v1/auth/validateUserAccess";

			AccessDetailsResponse userDetails = validateAccess(validateUserURL, username, "/api/loan/");
			return UserDetailsImpl.build(userDetails);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param uri
	 * @param userName
	 * @param apiEndPoint
	 * @return
	 */
	private AccessDetailsResponse validateAccess(String uri, String userName, String apiEndPoint) {
		AccessDetailsResponse accessResponse = null;
		try {
			HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
					.connectTimeout(Duration.ofSeconds(10)).build();

			Map<Object, Object> data = new HashMap();
			data.put("userName", userName);
			data.put("apiName", apiEndPoint);

			HttpRequest request = HttpRequest.newBuilder().POST(ofFormData(data)).uri(URI.create(uri))
					.header("Content-Type", "application/json").build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				Gson jsonObj = new Gson();
				accessResponse = jsonObj.fromJson(response.body(), AccessDetailsResponse.class);
			}
			response.body();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return accessResponse;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	private static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
		var builder = new StringBuilder();
		for (Map.Entry<Object, Object> entry : data.entrySet()) {
			if (builder.length() > 0) {
				builder.append("&");
			}
			builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
			builder.append("=");
			builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
		}
		return HttpRequest.BodyPublishers.ofString(builder.toString());
	}
}
