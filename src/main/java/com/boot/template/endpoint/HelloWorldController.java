package com.boot.template.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class HelloWorldController {
	final static Logger logger = LoggerFactory.getLogger(HelloWorldController.class);	

	@RequestMapping(value="/helloworld", method = RequestMethod.GET)
	@ResponseBody
	public static String Welcome() {
		logger.info("Hello Logger!");
		return "Hello World!";
	}

}

