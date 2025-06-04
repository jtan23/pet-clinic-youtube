package com.bw.owner;

import com.bw.owner.domain.Owner;
import com.bw.owner.repository.OwnerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootApplication
public class OwnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OwnerApplication.class, args);
	}

//	@Bean
	public CommandLineRunner run(OwnerRepository ownerRepository) {
		return args -> {
			System.out.println("All owners: " + ownerRepository.findAll());
			Pageable pageable = PageRequest.of(0, 5);
			Page<Owner> ownerPage = ownerRepository.findAll(pageable);
			System.out.println("All paged owners: " + ownerPage.getContent());
			ownerPage = ownerRepository.findByLastName(pageable, "Davis");
			System.out.println("Davis paged owners: " + ownerPage.getContent());
		};
	}

}
