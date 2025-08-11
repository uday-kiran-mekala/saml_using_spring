package explore.saml.saml_using_spring;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.saml2.core.NameIDPolicy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static org.opensaml.saml.saml2.core.NameIDType.EMAIL;

public final class Util {
    private Util() {
    }


    public static PrivateKey loadPrivateKeyFromPkcs8PemFile(File pkcs8PrivateKeyFile) throws Exception {
        String keyContent = Files.readString(pkcs8PrivateKeyFile.toPath(), Charset.defaultCharset());

        String privateKeyPEM = keyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

        return keyFactory.generatePrivate(keySpec);
    }

    public static X509Certificate loadX509CertificateFromPemFile(File pemFile) throws Exception {
        String certContent = Files.readString(pemFile.toPath(), Charset.defaultCharset());

        String certPEM = certContent
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END CERTIFICATE-----", "");
        byte[] encoded = Base64.getDecoder().decode(certPEM);

        return (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(encoded));
    }

    public static NameIDPolicy getEmailNameIDPolicy() {
        NameIDPolicy nameIDPolicy = XMLObjectProviderRegistrySupport.getBuilderFactory()
                .<NameIDPolicy>getBuilderOrThrow(NameIDPolicy.DEFAULT_ELEMENT_NAME)
                .buildObject(NameIDPolicy.DEFAULT_ELEMENT_NAME);
        nameIDPolicy.setFormat(EMAIL);
        nameIDPolicy.setAllowCreate(true);
        return nameIDPolicy;
    }


}
