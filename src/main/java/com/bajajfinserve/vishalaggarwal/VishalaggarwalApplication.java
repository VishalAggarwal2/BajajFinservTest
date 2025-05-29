package com.bajajfinserve.vishalaggarwal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
public class VishalaggarwalApplication {

	public static void main(String[] args) {
		SpringApplication.run(VishalaggarwalApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) {
		return args -> {
			// Step 1: Generate Webhook
			String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, String> requestBody = Map.of(
					"name", "Vishal",
					"regNo", "22ucs228",
					"email", "22ucs228@lnmiit.ac.in"
			);

			HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<Map<String, Object>> response = null;
			try {
				//noinspection unchecked
				response = restTemplate.postForEntity(generateUrl, entity, (Class<Map<String, Object>>) (Class<?>) Map.class);
			} catch (Exception ex) {
				System.err.println("Exception while generating webhook: " + ex.getMessage());
				return;
			}

			if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				Map<String, Object> body = response.getBody();
				String webhookUrl = (String) body.get("webhook");
				String accessToken = (String) body.get("accessToken");

				if (webhookUrl == null || accessToken == null) {
					System.err.println("Webhook URL or Access Token is missing in response.");
					return;
				}

				System.out.println("Webhook URL: " + webhookUrl);
				System.out.println("Access Token: " + accessToken);

				// Step 2: Your SQL Query
				String finalQuery = """
                        SELECT
                            e1.EMP_ID,
                            e1.FIRST_NAME,
                            e1.LAST_NAME,
                            d.DEPARTMENT_NAME,
                            COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
                        FROM EMPLOYEE e1
                        JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID
                        LEFT JOIN EMPLOYEE e2
                            ON e1.DEPARTMENT = e2.DEPARTMENT
                            AND e2.DOB > e1.DOB
                        GROUP BY
                            e1.EMP_ID,
                            e1.FIRST_NAME,
                            e1.LAST_NAME,
                            d.DEPARTMENT_NAME
                        ORDER BY
                            e1.EMP_ID DESC;
                        """;

				// Step 3: Submit final query to webhook URL with the raw access token (no Bearer prefix)
				HttpHeaders submitHeaders = new HttpHeaders();
				submitHeaders.setContentType(MediaType.APPLICATION_JSON);
				submitHeaders.set("Authorization", accessToken);

				Map<String, String> sqlBody = Map.of("finalQuery", finalQuery);
				HttpEntity<Map<String, String>> finalEntity = new HttpEntity<>(sqlBody, submitHeaders);

				try {
					ResponseEntity<String> finalResponse = restTemplate.postForEntity(webhookUrl, finalEntity, String.class);
					System.out.println("Submission Response Status: " + finalResponse.getStatusCode());
					System.out.println("Submission Response Body: " + finalResponse.getBody());
				} catch (Exception e) {
					System.err.println("Error submitting query: " + e.getMessage());
				}

			} else {
				System.err.println("Failed to generate webhook. Response: " + response);
			}
		};
	}
}
