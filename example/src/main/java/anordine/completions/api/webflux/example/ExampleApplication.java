package anordine.completions.api.webflux.example;

import com.anordine.completions.api.webflux.configuration.HistoryConfiguration;
import com.anordine.completions.api.webflux.configuration.SseConfiguration;
import com.anordine.completions.api.webflux.configuration.WebClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
		WebClientConfiguration.class,
		HistoryConfiguration.class,
		SseConfiguration.class
})
public class ExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

}
