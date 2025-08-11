package explore.saml.saml_using_spring.authn_requests;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authn_request")
public class AuthnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationId;

    private String authnId;

    @Column(columnDefinition = "TEXT")
    private String samlRequest;

    @Enumerated(EnumType.STRING)
    private Saml2MessageBinding binding;

}
