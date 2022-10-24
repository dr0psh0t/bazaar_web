package com.jadc.bazaar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@ComponentScan("com.jadc.persistence.entity")
@ComponentScan("com.jadc.persistence.repository")
@ComponentScan("com.jadc.service")
@EntityScan("com.jadc.persistence.entity")
@EnableJpaRepositories("com.jadc.persistence.repository")
@SpringBootApplication
public class BazaarApplication {

	public static void main(String[] args) {
		SpringApplication.run(BazaarApplication.class, args);
	}

}
