package explore.saml.saml_using_spring.ssocredentials.repository;

import explore.saml.saml_using_spring.ssocredentials.domain.SSOCredentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SSOCredentialsRepository extends CrudRepository<SSOCredentials, Long> {
}
