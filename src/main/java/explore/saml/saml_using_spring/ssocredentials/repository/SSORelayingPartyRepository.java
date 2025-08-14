package explore.saml.saml_using_spring.ssocredentials.repository;

import explore.saml.saml_using_spring.SPDetails;
import explore.saml.saml_using_spring.Util;
import explore.saml.saml_using_spring.ssocredentials.domain.SSOCredentials;
import org.opensaml.saml.common.SAMLRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

@Repository
public class SSORelayingPartyRepository implements RelyingPartyRegistrationRepository, Iterable<RelyingPartyRegistration> {

    @Value("classpath:keys/signing_private_key.pem")
    Resource signingPrivateKey;

    @Value("classpath:keys/signing_public_key.pem")
    Resource signingPublicKey;

    private final SSOCredentialsRepository ssoCredentialsRepository;

    public SSORelayingPartyRepository(SSOCredentialsRepository ssoCredentialsRepository) {
        this.ssoCredentialsRepository = ssoCredentialsRepository;
    }

    public RelyingPartyRegistration findByRegistrationId(Long id) {
        return findByRegistrationId(String.valueOf(id));
    }

    public RelyingPartyRegistration findByRegistrationId(String registrationId) {
        try {
            SSOCredentials credentials = ssoCredentialsRepository
                    .findById(Long.parseLong(registrationId))
                    .orElseThrow();

            ByteArrayInputStream metadata = new ByteArrayInputStream(credentials.metadata.getBytes(StandardCharsets.UTF_8));

            return RelyingPartyRegistrations
                    .fromMetadata(metadata)
                    .assertingPartyDetails(this::customizeAssertingParty)
                    .registrationId(registrationId)

                    .entityId(SPDetails.SP_ENTITY_ID)
//                    .assertionConsumerServiceLocation(SPDetails.SP_ACS_URL)
//                    .assertionConsumerServiceBinding(Saml2MessageBinding.POST)
                    .signingX509Credentials(this::addSigningCredentials)
                    .build();
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void customizeAssertingParty(RelyingPartyRegistration.AssertingPartyDetails.Builder assertingParty) {
        assertingParty
//                .singleSignOnServiceBinding(Saml2MessageBinding.REDIRECT)
//                .signingAlgorithms()
                .wantAuthnRequestsSigned(true)
        ;
    }

    private void addSigningCredentials(Collection<Saml2X509Credential> signingCredentials) {
        try {
            PrivateKey privateKey = Util.loadPrivateKeyFromPkcs8PemFile(signingPrivateKey.getFile());
            X509Certificate x509Certificate = Util.loadX509CertificateFromPemFile(signingPublicKey.getFile());
            signingCredentials.add(Saml2X509Credential.signing(privateKey, x509Certificate));
        } catch (Exception e) {
            throw new SAMLRuntimeException("Failed to load signing credentials", e);
        }
    }

    @Override
    public Iterator<RelyingPartyRegistration> iterator() {
        Iterable<SSOCredentials> all = ssoCredentialsRepository.findAll();
        List<RelyingPartyRegistration> relyingPartyRegistrations = StreamSupport.stream(all.spliterator(), false)
                .map(sso -> sso.id)
                .map(String::valueOf)
                .map(this::findByRegistrationId)
                .toList();
        return relyingPartyRegistrations.iterator();
    }
}
