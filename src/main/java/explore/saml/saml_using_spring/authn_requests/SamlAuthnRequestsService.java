package explore.saml.saml_using_spring.authn_requests;

import com.fasterxml.jackson.core.TreeCodec;
import explore.saml.saml_using_spring.SPDetails;
import explore.saml.saml_using_spring.ssocredentials.repository.SSORelayingPartyRepository;
import jakarta.servlet.http.Cookie;
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
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
public class SamlAuthnRequestsService implements Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> {

    private final SamlAuthnRequestsRepository repository;
    private final SSORelayingPartyRepository relayingPartyRepository;
    private final TreeCodec treeCodec;

    public SamlAuthnRequestsService(SamlAuthnRequestsRepository repository, SSORelayingPartyRepository relayingPartyRepository, TreeCodec treeCodec) {
        this.repository = repository;
        this.relayingPartyRepository = relayingPartyRepository;
        this.treeCodec = treeCodec;
    }

    // Used for SAML2 authentication request resolution on login endpoint
    @Override
    public AbstractSaml2AuthenticationRequest loadAuthenticationRequest(HttpServletRequest request) {
        System.out.println("Loading authentication request for request: " + request);

        Optional<String> sessionInRequest = extractSessionFromRequest(request);
        if (sessionInRequest.isEmpty()) {
            System.out.println("Session not found in request cookies ========================================================================================================");
            return null;
        }
        Optional<SamlAuthnRequest> samlAuthnRequestInDB = repository.findBySessionToken(sessionInRequest.get());
        if (samlAuthnRequestInDB.isEmpty()) {
            System.out.println("Saml Authentication request not found in database for session: " + sessionInRequest.get());
            return null;
        }

        SamlAuthnRequest samlAuthnRequest = samlAuthnRequestInDB.get();
        Long registrationId = extractRegistrationIdFromRequest(request, SPDetails.AUTHORIZE_REQUEST_MATCHER);

        Assert.state(Objects.equals(registrationId, samlAuthnRequest.getRegistrationId()), "Registration ID mismatch: " + registrationId + " != " + samlAuthnRequest.getRegistrationId());

        RelyingPartyRegistration relyingPartyRegistration = relayingPartyRepository.findByRegistrationId(registrationId);
        if (relyingPartyRegistration == null) {
            return null;
        }

        if (samlAuthnRequest.getBinding() == Saml2MessageBinding.REDIRECT) {
            return Saml2RedirectAuthenticationRequest.withRelyingPartyRegistration(relyingPartyRegistration)
                    .samlRequest(samlAuthnRequest.getSamlRequest())
                    .id(samlAuthnRequest.getAuthnId())
                    .build();
        } else {
            return Saml2PostAuthenticationRequest.withRelyingPartyRegistration(relyingPartyRegistration)
                    .samlRequest(samlAuthnRequest.getSamlRequest())
                    .id(samlAuthnRequest.getAuthnId())
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
        Optional<String> sessionOpt = extractSessionFromRequest(request);

        String sessionToken = resolveSession(request, response);

        var saml2AuthenticationRequest = new SamlAuthnRequest(
                sessionToken,
                registrationId,
                authenticationRequest.getId(),
                authenticationRequest.getSamlRequest(),
                authenticationRequest.getBinding()
        );
        repository.save(saml2AuthenticationRequest);
        System.out.println("Authentication request saved with registration ID: " + registrationId);
    }

    private String resolveSession(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> sessionOpt = extractSessionFromRequest(request);
        if (sessionOpt.isPresent()) {
            return sessionOpt.get();
        } else {
            String newSessionId = java.util.UUID.randomUUID().toString();
            setSessionCookie(response, newSessionId);
            System.out.println("New session created: " + newSessionId);
            return newSessionId;
        }
    }

    @Override
    public AbstractSaml2AuthenticationRequest removeAuthenticationRequest(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Removing authentication request for request: " + request);
        Long registrationId = extractRegistrationIdFromRequest(request, SPDetails.AUTHORIZE_REQUEST_MATCHER);
        if (registrationId == null) {
            System.out.println("Not found registrationId in request ========================================================================================================");
            return null;
        }

        Optional<String> sessionOpt = extractSessionFromRequest(request);
        if (sessionOpt.isPresent()) {
            System.out.println("Session found in request cookies: " + sessionOpt.get());
            return null;
        }
        String sessionToken = sessionOpt.get();
        Optional<SamlAuthnRequest> samlAuthnRequestOpt = repository.findBySessionToken(sessionToken);
        if (samlAuthnRequestOpt.isEmpty()) {
            System.out.println("Saml Authentication request not found in database for session: " + sessionToken);
            return null;
        }

        SamlAuthnRequest samlAuthnRequest = samlAuthnRequestOpt.get();
//        repository.delete(samlAuthnRequest);

        RelyingPartyRegistration relyingPartyRegistration = relayingPartyRepository.findByRegistrationId(registrationId);
        if (samlAuthnRequest.getBinding() == Saml2MessageBinding.REDIRECT) {
            return Saml2RedirectAuthenticationRequest.withRelyingPartyRegistration(relyingPartyRegistration)
                    .samlRequest(samlAuthnRequest.getSamlRequest())
                    .id(samlAuthnRequest.getAuthnId())
                    .build();
        } else {
            return Saml2PostAuthenticationRequest.withRelyingPartyRegistration(relyingPartyRegistration)
                    .samlRequest(samlAuthnRequest.getSamlRequest())
                    .id(samlAuthnRequest.getAuthnId())
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

    private Optional<String> extractSessionFromRequest(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("session"))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void setSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie sessionCookie = new Cookie("session", sessionId);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(60 * 60); // 1 hour
        response.addCookie(sessionCookie);
    }

}
