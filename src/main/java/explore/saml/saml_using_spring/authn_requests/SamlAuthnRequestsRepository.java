package explore.saml.saml_using_spring.authn_requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SamlAuthnRequestsRepository extends JpaRepository<SamlAuthnRequest, String> {

    Optional<SamlAuthnRequest> findBySessionToken(String sessionToken);

}
