package demo.webmvcjsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

import org.springframework.boot.ApplicationRunner;

import magnuscapital.Stock;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	/*@Bean
	ApplicationRunner init(String t, String timestamp, Double open, Double high, Double low, Double close, int volume) {
		return args -> {
			Stream.of("AAPL", "TSLA")
			.forEach(name -> {
				Stock stock = new Stock();
				stock.setTicker(name);
				System.out.println("NAME: " + name);
			});
		};
	}*/
}
