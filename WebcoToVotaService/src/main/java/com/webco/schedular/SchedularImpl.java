package com.webco.schedular;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.webco.WebcoToVotaServiceApplication;

public class SchedularImpl extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebcoToVotaServiceApplication.class);
	}
}
