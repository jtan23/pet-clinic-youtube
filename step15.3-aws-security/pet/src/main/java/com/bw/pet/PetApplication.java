package com.bw.pet;

import com.bw.pet.repository.PetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PetApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetApplication.class, args);
	}

//	@Bean
	public CommandLineRunner run(PetRepository petRepository) {
		return args -> {
			System.out.println("All pets: " + petRepository.findAll());
			System.out.println("Pets for owner 1: " + petRepository.findByOwnerId(1));
		};
	}

}
