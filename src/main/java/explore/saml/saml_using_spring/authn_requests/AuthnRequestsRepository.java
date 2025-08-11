package explore.saml.saml_using_spring.authn_requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthnRequestsRepository extends JpaRepository<AuthnRequest, Long> {

    Optional<AuthnRequest> findByRegistrationId(Long registrationId);

}
