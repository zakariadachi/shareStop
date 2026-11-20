package ma.youcode.covoiturage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI covoiturageOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Covoiturage Multi-Étapes")
                        .description("API REST pour la gestion d'une plateforme de covoiturage multi-étapes. "
                                + "Permet la gestion des conducteurs, passagers, véhicules, trajets avec points d'arrêt "
                                + "et réservations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("YouCode")
                                .email("contact@youcode.ma")));
    }
}
