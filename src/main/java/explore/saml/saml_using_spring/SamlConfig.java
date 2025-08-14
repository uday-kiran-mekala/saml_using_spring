package explore.saml.saml_using_spring;

import explore.saml.saml_using_spring.authn_requests.SamlAuthnRequestsService;
import jakarta.servlet.http.HttpServletRequest;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationTokenConverter;
import org.springframework.security.saml2.provider.service.web.Saml2WebSsoAuthenticationRequestFilter;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.UUID;

import static explore.saml.saml_using_spring.Util.loadPrivateKeyFromPkcs8PemFile;
import static explore.saml.saml_using_spring.Util.loadX509CertificateFromPemFile;

@Configuration
@EnableWebSecurity
public class SamlConfig {

    private final SamlAuthnRequestsService samlAuthnRequestsService;

    public SamlConfig(SamlAuthnRequestsService samlAuthnRequestsService) {
        this.samlAuthnRequestsService = samlAuthnRequestsService;
    }

    public static void mainn(String[] args) throws Exception {
        System.out.println("SamlConfig.main");
        String path = "/home/tracxn-lp-714/test/";

        File privateKeyFile = new File(path + "signing-key.pem");
        File publicKeyFile = new File(path + "signing-cert.pem");

        PrivateKey privateKey = loadPrivateKeyFromPkcs8PemFile(privateKeyFile);
        System.out.println(privateKey.getAlgorithm());
        System.out.println(privateKey.getFormat());
        System.out.println(privateKey.getEncoded().length);

        X509Certificate x509Certificate = loadX509CertificateFromPemFile(publicKeyFile);
        System.out.println(x509Certificate.getSigAlgName());
    }

    @Bean
    SecurityFilterChain samlSpringSecurityFilterChain(HttpSecurity http, RelyingPartyRegistrationResolver resolver) throws Exception {
        Saml2AuthenticationTokenConverter converter = new Saml2AuthenticationTokenConverter(resolver);
        converter.setAuthenticationRequestRepository(samlAuthnRequestsService);

        DefaultSecurityFilterChain securityChain = http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/test/secure/**").authenticated()
                                .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .saml2Login(customizer -> {
                    customizer.authenticationConverter(converter);
                })
                .build();

        Saml2WebSsoAuthenticationRequestFilter requestFilter = securityChain.getFilters().stream()
                .filter(Saml2WebSsoAuthenticationRequestFilter.class::isInstance)
                .map(Saml2WebSsoAuthenticationRequestFilter.class::cast)
                .findFirst().orElseThrow();
        requestFilter.setAuthenticationRequestRepository(samlAuthnRequestsService);

        return securityChain;
    }

    @Bean
    OpenSaml4AuthenticationRequestResolver openSaml4AuthenticationRequestResolver(RelyingPartyRegistrationResolver relyingPartyRegistrationResolver) {
        OpenSaml4AuthenticationRequestResolver resolver = new OpenSaml4AuthenticationRequestResolver(relyingPartyRegistrationResolver);
        resolver.setAuthnRequestCustomizer(this::customizeAuthnRequest);
        resolver.setRequestMatcher(SPDetails.AUTHENTICATE_REQUEST_MATCHER);
        resolver.setRelayStateResolver(relayStateResolver());
        return resolver;
    }

    @Bean
    RelyingPartyRegistrationResolver relyingPartyRegistrationResolver(RelyingPartyRegistrationRepository relyingPartyRegistrationRepository) {
        return new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);
    }

    private void customizeAuthnRequest(OpenSaml4AuthenticationRequestResolver.AuthnRequestContext authnRequestContext) {
        AuthnRequest authnRequest = authnRequestContext.getAuthnRequest();
        authnRequest.setNameIDPolicy(Util.getEmailNameIDPolicy());
        authnRequest.setID("_" + UUID.randomUUID());
    }


    private Converter<HttpServletRequest, String> relayStateResolver() {
        return request -> {
            return "Custom-" + UUID.randomUUID() + "#" + request.getParameter("username");
        };
    }


}