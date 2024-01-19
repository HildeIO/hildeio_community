package com.hildeio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@OpenAPIDefinition
@Configuration
public class SpringdocConfiguration {

	  @Bean
	  public OpenAPI springShopOpenAPI() {
	      return new OpenAPI()
	              .info(new Info()
	            	  .title("HildeIO - Event Driven REST-API")
		              .description("Eventgesteuerte REST-API zur Synchronisation von JSON-Datenobjekten zwischen der HomeMatic und Google Firestore")
		              .version("v0.0.1")
		              )
	              .externalDocs(new ExternalDocumentation()	              
	              );
	  }	
}
