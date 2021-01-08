package com.webco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class WebcoToVotaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebcoToVotaServiceApplication.class, args);
	}

	@Bean
	   public RestTemplate getRestTemplate(RestTemplateBuilder builder) {
	
		 return new RestTemplate();

	   }
}
