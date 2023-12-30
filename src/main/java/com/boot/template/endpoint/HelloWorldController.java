package com.boot.template.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class HelloWorldController {
	final static Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

	@GetMapping("/helloworld")
	@ResponseBody
	public static String Welcome() {
		logger.info("Hello Logger!");
		return "Hello World!";
	}

}

