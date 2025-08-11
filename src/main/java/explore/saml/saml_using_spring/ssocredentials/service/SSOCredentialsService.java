package explore.saml.saml_using_spring.ssocredentials.service;

import explore.saml.saml_using_spring.ssocredentials.domain.SSOCredentials;
import explore.saml.saml_using_spring.ssocredentials.domain.SSOType;
import explore.saml.saml_using_spring.ssocredentials.domain.Status;
import explore.saml.saml_using_spring.ssocredentials.repository.SSOCredentialsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SSOCredentialsService {

    private final SSOCredentialsRepository repository;

    public SSOCredentialsService(SSOCredentialsRepository repository) {
        this.repository = repository;
    }


    @PostConstruct
    public void loadSSOCredentials() {
        System.out.println("SSOCredentialsService.loadSSOCredentials---------------------------------------------------------");

        var ssoCredentials1 = samlApp1();
        var ssoCredentials2 = samlApp2();

        Iterable<SSOCredentials> saved = repository.saveAll(List.of(ssoCredentials1, ssoCredentials2));
        System.out.println("Loaded SSO Credentials.");
    }

    private static SSOCredentials samlApp1() {
        String provider = "OKTA";
        String idpId = "http://www.okta.com/exkt6gni11q7R95Oa697";
        Status status = Status.ENABLED;
        SSOType ssoType = SSOType.SAML2;
        String metadataUrl = "https://trial-3578031.okta.com/app/exkt6gni11q7R95Oa697/sso/saml/metadata";
        String metadata = """
                <?xml version="1.0" encoding="UTF-8"?>
                <md:EntityDescriptor entityID="http://www.okta.com/exkt6gni11q7R95Oa697" xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata">
                    <md:IDPSSODescriptor WantAuthnRequestsSigned="false" protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
                        <md:KeyDescriptor use="signing">
                            <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                                <ds:X509Data>
                                    <ds:X509Certificate>MIIDqjCCApKgAwIBAgIGAZgEngBfMA0GCSqGSIb3DQEBCwUAMIGVMQswCQYDVQQGEwJVUzETMBEG
                A1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU
                MBIGA1UECwwLU1NPUHJvdmlkZXIxFjAUBgNVBAMMDXRyaWFsLTM1NzgwMzExHDAaBgkqhkiG9w0B
                CQEWDWluZm9Ab2t0YS5jb20wHhcNMjUwNzEzMTYyNzQwWhcNMzUwNzEzMTYyODQwWjCBlTELMAkG
                A1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTAL
                BgNVBAoMBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRYwFAYDVQQDDA10cmlhbC0zNTc4MDMx
                MRwwGgYJKoZIhvcNAQkBFg1pbmZvQG9rdGEuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB
                CgKCAQEAvU/02sPL6B3fUlkr7rhvHQVv0+LrfgQg8Zm1236FGKp38Fi+u7q/YrtFdoB/1ZicR6DY
                C9bEalKLra+LFNfHSOci0Avi2qzx/ht3Dlz9seQ7iHJIQS7XKZuQq2U/C6qyypW/Sx9/iCKWyU3G
                oq82FKAHLCCZqUDhm+ac2dYjhbCNaw00B2kHsGN99NCr2o0onzW6SPoVHs6u+zjzgO5HDlGDtxz4
                2+44h7n0L408IfLIxw4HZfEWNHWYmLh0tb/YXFPvzuL4XX6VWZv0m4cYAH4jLRBfbEBXOvalTA2R
                7EZWtBT85umYA8UD0Ig5iQ55CJTJ3asJIaF3i1Qa0x71EQIDAQABMA0GCSqGSIb3DQEBCwUAA4IB
                AQBuLA6rJ4zQD4W+rcAWuNNL7E3DYai5wEwwOINsMVX3oyNUTKleYAFOVCXIoRq3ktM4Kp5mj/2q
                +8MS42r4p+22kQRfr34pGBVMgG0aV2lB1qXjK4NEupfUn65BB8P94F9T0pQRDEx3Qi9iwMUvypw8
                /TjkKP3ofuWxORk2bKGMVDta5Ymi3fPosOAAdxvejBb6FahxlCTpDSP9U6waWGWeeyqxrzcpt8ec
                QR41kp7C4pqry6FAkyv2Rs0sAe/ZQb4rn+rRqxA2idnpaqk7iYjDIsDPX0ohEbfoSUq/W42gkv2g
                HcpS0YyjGfbZCIiDdmBbC/0VYBmc0e8zXz10gEFW</ds:X509Certificate>
                                </ds:X509Data>
                            </ds:KeyInfo>
                        </md:KeyDescriptor>
                        <md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress</md:NameIDFormat>
                        <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="https://trial-3578031.okta.com/app/trial-3578031_samltest_1/exkt6gni11q7R95Oa697/sso/saml"/>
                        <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="https://trial-3578031.okta.com/app/trial-3578031_samltest_1/exkt6gni11q7R95Oa697/sso/saml"/>
                    </md:IDPSSODescriptor>
                </md:EntityDescriptor>
                """;

        return new SSOCredentials(
                null,
                provider,
                idpId,
                status,
                ssoType,
                metadataUrl,
                metadata
        );
    }

    private static SSOCredentials samlApp2() {
        String provider = "OKTA";
        String idpId = "http://www.okta.com/exkt6gni11q7R95Oa697";
        Status status = Status.ENABLED;
        SSOType ssoType = SSOType.SAML2;
        String metadataUrl = "https://trial-3578031.okta.com/app/exkt6x91l2lSGNrM4697/sso/saml/metadata";
        String metadata = """
                <?xml version="1.0" encoding="UTF-8"?>
                <md:EntityDescriptor entityID="http://www.okta.com/exkt6x91l2lSGNrM4697" xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata">
                    <md:IDPSSODescriptor WantAuthnRequestsSigned="false" protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
                        <md:KeyDescriptor use="signing">
                            <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                                <ds:X509Data>
                                    <ds:X509Certificate>MIIDqjCCApKgAwIBAgIGAZgH1+4uMA0GCSqGSIb3DQEBCwUAMIGVMQswCQYDVQQGEwJVUzETMBEG
                A1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU
                MBIGA1UECwwLU1NPUHJvdmlkZXIxFjAUBgNVBAMMDXRyaWFsLTM1NzgwMzExHDAaBgkqhkiG9w0B
                CQEWDWluZm9Ab2t0YS5jb20wHhcNMjUwNzE0MDcyOTQ4WhcNMzUwNzE0MDczMDQ4WjCBlTELMAkG
                A1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTAL
                BgNVBAoMBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRYwFAYDVQQDDA10cmlhbC0zNTc4MDMx
                MRwwGgYJKoZIhvcNAQkBFg1pbmZvQG9rdGEuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB
                CgKCAQEApopUFZVr8WQ1XYySc0O+25JSg5Yc1pGnAsPE3IsirPnVy4D5mfpNqG3tIOgtf/bKdLxw
                FwhyA4l+3j7XCypaxz8LTM8eePOXT3RsnA8OvMRiTLOwkDLf5BKeiq5Nxra7u88Ss017CpSg7De8
                Iev59fWlVZxH2VLcnlI691R+A7sljP2hqI/gX5Q0JlYMZcQzg24h2VQ6exYa/0XQ28wbpSz9m2y+
                0xUSeepWu5dG0BwKuBvHjG08pnCXnrnZ6sVYRhFkmSg6TIMI7n/9fuYN+peSlSDzBjuMUlr/SAs1
                nOYPBcygWHpyXL2uU873e0c6olO9cseDx3yl+lR3xiLmmQIDAQABMA0GCSqGSIb3DQEBCwUAA4IB
                AQCQ3Sja9Giz2T59ydreUqb0xLtJSdBynWm5tGhEY6I076vvEJLdPYGgYzj7R1VQbD8AkZlcUlj8
                bWp1IgATMQLkKK981TsqGUHHRswhdg5q0nT42PA6JIoBcQehduQTccyqMz3cnz0XxnNMQpPKA3rz
                y62/Y8/4xxXnBr/SPJZoSV/6yTuhcYycI4S3TOjvcqrQxYweOgAgKa9A6p07D9FuuBjfsDVynPly
                SIuXJxu8CXKbRGeT9crtnjNgAc7CrdjCB0yKeuzLMeZM2GNnmcOgDzuIj0q0xZ7/AXJPjRjuMJsL
                EXrH4YDcHPnb4BsWPWti+dzjm08l3bsPIAJYXqn/</ds:X509Certificate>
                                </ds:X509Data>
                            </ds:KeyInfo>
                        </md:KeyDescriptor>
                        <md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress</md:NameIDFormat>
                        <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="https://trial-3578031.okta.com/app/trial-3578031_saml2_1/exkt6x91l2lSGNrM4697/sso/saml"/>
                        <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="https://trial-3578031.okta.com/app/trial-3578031_saml2_1/exkt6x91l2lSGNrM4697/sso/saml"/>
                    </md:IDPSSODescriptor>
                </md:EntityDescriptor>
                """;

        return new SSOCredentials(
                null,
                provider,
                idpId,
                status,
                ssoType,
                metadataUrl,
                metadata
        );
    }

}
