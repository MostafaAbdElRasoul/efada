package com.efada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy()
@SpringBootApplication
public class EfadaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EfadaApplication.class, args);
	}

}
