package explore.saml.saml_using_spring.ssocredentials.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sso_credentials")
@NoArgsConstructor
@AllArgsConstructor
public class SSOCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "provider", nullable = false)
    public String provider;

    @Column(name = "idp_id", nullable = true)
    public String idpId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public Status status = Status.ENABLED;

    @Column(name = "sso_type", nullable = false)
    public SSOType ssoType;

    @Column(name = "metadata_url", nullable = true)
    public String metadataUrl;

    @Column(name = "metadata", nullable = true, length = 10000)
    public String metadata;

}
