package com.yworks.example.HelloWorld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloWorldApplication {
  private static String obfuscate() {
    return "This shall be obfuscated";
  }

	public static void main(String[] args) {
    System.out.println(obfuscate());
		SpringApplication.run(HelloWorldApplication.class, args);
	}

}
