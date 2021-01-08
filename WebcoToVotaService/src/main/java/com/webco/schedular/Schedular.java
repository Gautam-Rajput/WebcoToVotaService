package com.webco.schedular;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.webco.controller.WebcoController;


@Component
public class Schedular {

	private static final Logger logger = LoggerFactory.getLogger(Schedular.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    @Autowired
    WebcoController webcon;
    
   @Scheduled(cron = "0 0/15 * * * *")
    public void schedularWithCronExpression() {
	    logger.info("Cron Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
		webcon.saveDataInEicherDB();
    }
}
