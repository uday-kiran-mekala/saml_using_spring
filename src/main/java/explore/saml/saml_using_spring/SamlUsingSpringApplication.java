package explore.saml.saml_using_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.saml2.core.OpenSamlInitializationService;

@SpringBootApplication
public class SamlUsingSpringApplication {

    static {
        OpenSamlInitializationService.initialize();
    }

    public static void main(String[] args) {
        SpringApplication.run(SamlUsingSpringApplication.class, args);
    }

}
