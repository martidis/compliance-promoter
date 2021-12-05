package com.tasosmartidis.mycmdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MyCmdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCmdbApplication.class, args);
	}

}
