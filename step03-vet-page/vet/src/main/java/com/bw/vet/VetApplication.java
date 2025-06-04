package com.bw.vet;

import com.bw.vet.domain.Vet;
import com.bw.vet.repository.VetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootApplication
public class VetApplication {

	public static void main(String[] args) {
		SpringApplication.run(VetApplication.class, args);
	}

//	@Bean
	public CommandLineRunner run(VetRepository vetRepository) {
		return args -> {
			System.out.println("All Vets: " + vetRepository.findAll());
			Page<Vet> vets = vetRepository.findAll(PageRequest.of(0, 3));
			System.out.println("First Page of Vets: " + vets.getContent());
		};
	}

}
