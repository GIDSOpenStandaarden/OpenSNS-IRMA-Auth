package nl.gids.poc.auth.irma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 */
@SpringBootApplication
@ComponentScan("nl.gids.poc.auth")
public class Application {
	public static void main(String[] args) {
     SpringApplication.run(Application.class, args);
 }
}
