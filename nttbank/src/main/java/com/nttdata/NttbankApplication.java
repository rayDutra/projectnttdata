package com.nttdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class NttbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(NttbankApplication.class, args);
	}

}
