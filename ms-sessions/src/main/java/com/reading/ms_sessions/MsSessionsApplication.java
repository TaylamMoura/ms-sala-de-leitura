package com.reading.ms_sessions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsSessionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSessionsApplication.class, args);
	}

}
