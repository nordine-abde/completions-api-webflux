package com.anordine.simplifier.webflux.ai.example;

import com.anordine.simplifier.webflux.ai.configuration.CompletionToolConfiguration;
import com.anordine.simplifier.webflux.ai.configuration.HistoryConfiguration;
import com.anordine.simplifier.webflux.ai.configuration.SseConfiguration;
import com.anordine.simplifier.webflux.ai.configuration.WebClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
		WebClientConfiguration.class,
		HistoryConfiguration.class,
		SseConfiguration.class,
		CompletionToolConfiguration.class
})
public class ExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

}
