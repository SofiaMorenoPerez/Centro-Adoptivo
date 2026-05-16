package co.edu.unbosque.centroadoptivo;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CentroadoptivoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CentroadoptivoApplication.class, args);
	}
	
	 @Bean
	  public ModelMapper getModelMapper() {
	    return new ModelMapper();
	  }

}
