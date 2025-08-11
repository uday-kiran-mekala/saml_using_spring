package explore.saml.saml_using_spring;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public final class SPDetails {

    public static final String SP_ENTITY_ID = "https://tracxn.com/saml/sp";
    public static final String SP_ACS_URL = "https://tracxn.com/saml/sp/flock/acs";
    public static final String SP_AUTHENTICATE_URL = "/saml2/authenticate/{registrationId}";
    public static final String SP_AUTHRORIZE_URL = "/saml2/authorize/{registrationId}";
    public static final RequestMatcher AUTHENTICATE_REQUEST_MATCHER = new AntPathRequestMatcher(SPDetails.SP_AUTHENTICATE_URL);
    public static final RequestMatcher AUTHORIZE_REQUEST_MATCHER = new AntPathRequestMatcher("/**/{registrationId}");

    private SPDetails() {
    }

}
