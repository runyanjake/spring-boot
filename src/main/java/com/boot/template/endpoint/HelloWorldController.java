package com.boot.template.endpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class HelloWorldController {
	
	@RequestMapping(value="/helloworld", method = RequestMethod.GET)
	@ResponseBody
	public static String Welcome() {
	    return "Hello World!";
	}

}

