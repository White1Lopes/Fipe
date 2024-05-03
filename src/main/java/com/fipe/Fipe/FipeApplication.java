package com.fipe.Fipe;

import com.fipe.Fipe.Principal.PrincipalMenu;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FipeApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FipeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		PrincipalMenu principalMenu = new PrincipalMenu();
		principalMenu.showMenu();
	}
}
