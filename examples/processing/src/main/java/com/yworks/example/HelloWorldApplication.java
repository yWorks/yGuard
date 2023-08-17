package com.yworks.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point into a Spring console application.
 */
@SpringBootApplication
public class HelloWorldApplication implements CommandLineRunner {
  public static void main( String[] args ) {
    SpringApplication.run(HelloWorldApplication.class, args);
  }

  @Override
  public void run( String... args ) throws Exception {
    new HelloWorldImpl().printHelloWorld();
  }
}
