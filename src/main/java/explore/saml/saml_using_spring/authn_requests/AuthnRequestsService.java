package explore.saml.saml_using_spring.authn_requests;

import explore.saml.saml_using_spring.SPDetails;
import explore.saml.saml_using_spring.ssocredentials.repository.SSORelayingPartyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.Saml2PostAuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.Saml2RedirectAuthenticationRequest;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

@Service
public class AuthnRequestsService implements Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> {

    private final AuthnRequestsRepository repository;
    private final SSORelayingPartyRepository relayingPartyRepository;

    public AuthnRequestsService(AuthnRequestsRepository repository, SSORelayingPartyRepository relayingPartyRepository) {
        this.repository = repository;
        this.relayingPartyRepository = relayingPartyRepository;
    }

    @Override
    public AbstractSaml2AuthenticationRequest loadAuthenticationRequest(HttpServletRequest request) {
        System.out.println("Loading authentication request for request: " + request);
        Long registrationId = extractRegistrationIdFromRequest(request, SPDetails.AUTHORIZE_REQUEST_MATCHER);
        if (registrationId == null) {
            System.out.println("Not found registrationId in request ========================================================================================================");
            return null;
        }

        RelyingPartyRegistration relyingPartyRegistration = relayingPartyRepository.findByRegistrationId(registrationId);
        if (relyingPartyRegistration == null) {
            return null;
        }

        AuthnRequest authnRequest = repository.findByRegistrationId(registrationId).orElse(null);
        if (authnRequest == null) {
            return null;
        }

        if (authnRequest.getBinding() == Saml2MessageBinding.REDIRECT) {
            return Saml2RedirectAuthenticationRequest.withRelyingPartyRegistration(relyingPartyRegistration)
                    .samlRequest(authnRequest.getSamlRequest())
                    .id(authnRequest.getAuthnId())
                    .build();
        } else {
            return Saml2PostAuthenticationRequest.withRelyingPartyRegistration(relyingPartyRegistration)
                    .samlRequest(authnRequest.getSamlRequest())
                    .id(authnRequest.getAuthnId())
                    .build();
        }
    }

    @Override
    public void saveAuthenticationRequest(AbstractSaml2AuthenticationRequest authenticationRequest, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Saving authentication request: " + authenticationRequest);
        if (authenticationRequest == null) {
            throw new IllegalArgumentException("Authentication request cannot be null");
        }

        Long registrationId = extractRegistrationIdFromRequest(request, SPDetails.AUTHENTICATE_REQUEST_MATCHER);
        var saml2AuthenticationRequest = new AuthnRequest(
                registrationId,
                authenticationRequest.getId(),
                authenticationRequest.getSamlRequest(),
                authenticationRequest.getBinding()
        );
        repository.save(saml2AuthenticationRequest);
        System.out.println("Authentication request saved with registration ID: " + registrationId);
    }

    @Override
    public AbstractSaml2AuthenticationRequest removeAuthenticationRequest(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Removing authentication request for request: " + request);
        Long registrationId = extractRegistrationIdFromRequest(request, SPDetails.AUTHORIZE_REQUEST_MATCHER);
        if (registrationId == null) {
            System.out.println("Not found registrationId in request ========================================================================================================");
            return null;
        }

        AuthnRequest authnRequest = repository.findByRegistrationId(registrationId).orElse(null);
        if (authnRequest == null) {
            return null;
        }
//        repository.delete(authnRequest);

        RelyingPartyRegistration relyingPartyRegistration = relayingPartyRepository.findByRegistrationId(registrationId);
        if (authnRequest.getBinding() == Saml2MessageBinding.REDIRECT) {
            return Saml2RedirectAuthenticationRequest.withRelyingPartyRegistration(relyingPartyRegistration)
                    .samlRequest(authnRequest.getSamlRequest())
                    .id(authnRequest.getAuthnId())
                    .build();
        } else {
            return Saml2PostAuthenticationRequest.withRelyingPartyRegistration(relyingPartyRegistration)
                    .samlRequest(authnRequest.getSamlRequest())
                    .id(authnRequest.getAuthnId())
                    .build();
        }
    }

    private Long extractRegistrationIdFromRequest(HttpServletRequest request, RequestMatcher requestMatcher) {
        RequestMatcher.MatchResult result = requestMatcher.matcher(request);
        String registrationId = result.getVariables().get("registrationId");
        try {
            return Long.parseLong(registrationId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
