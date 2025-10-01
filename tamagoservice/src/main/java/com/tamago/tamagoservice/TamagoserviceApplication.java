package com.tamago.tamagoservice;

import com.tamago.tamagoservice.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class TamagoserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TamagoserviceApplication.class, args);
	}

}
