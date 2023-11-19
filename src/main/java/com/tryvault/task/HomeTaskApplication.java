package com.tryvault.task;

import com.tryvault.task.Response.FundLoadResponse;
import com.tryvault.task.service.FundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.List;

@SpringBootApplication
public class HomeTaskApplication implements CommandLineRunner {

	@Autowired
	private FundService fundService;

	public static void main(String[] args) {
		SpringApplication.run(HomeTaskApplication.class, args);
	}


	@Override
	public void run(String... args) {
		if(args.length == 0) {
			return;
		}

		var filePath = args[0];

		File inputFile = new File(filePath);

		List<FundLoadResponse> responses = fundService.loadFunds(inputFile);
		System.out.println("****** Printing Result ******");
		responses.forEach(response -> System.out.println(response.toString()));
		System.out.println("****** End Of Result ******");
		System.exit(0);
	}

}
