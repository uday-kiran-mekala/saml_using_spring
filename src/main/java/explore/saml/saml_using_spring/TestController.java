package explore.saml.saml_using_spring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin("*")
@RestController
@RequestMapping("/test")
public class TestController {


//    final TracxnSaml2AuthenticationRequestResolverImpl resolver;
//
//    public TestController(TracxnSaml2AuthenticationRequestResolverImpl resolver) {
//        this.resolver = resolver;
//    }

    @RequestMapping
    public String hello(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "Hello, World!";
    }

    @RequestMapping("/secure")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        return "Login successful!";
    }

}
