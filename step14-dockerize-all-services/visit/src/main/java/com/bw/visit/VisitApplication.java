package com.bw.visit;

import com.bw.visit.repository.VisitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VisitApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisitApplication.class, args);
	}

//	@Bean
	public CommandLineRunner run(VisitRepository visitRepository) {
		return args -> {
			System.out.println("All visits: " + visitRepository.findAll());
			System.out.println("Visits for pet 7: " + visitRepository.findByPetId(7));
		};
	}

}
