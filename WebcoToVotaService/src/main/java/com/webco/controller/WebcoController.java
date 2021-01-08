package com.webco.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.webco.service.ProcessData;

@Controller
@RequestMapping
public class WebcoController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebcoController.class);
	@Autowired
	private ProcessData processData;
	
	@PostMapping("/saveDataInEicherDB")
	@ResponseBody
	public void saveDataInEicherDB(){
		try {
			long startTime=System.currentTimeMillis();
			processData.dataProcessing();
			long end = System.currentTimeMillis();
		    float sec = (end - startTime) / 1000F;
			System.out.println("Time Taken by WebcoToVota : "+sec+" Second");
			
		}
		catch(Exception ex) {
			LOGGER.info("Exception Occurred",ex.getMessage());
			ex.printStackTrace();
		}
	}
}
