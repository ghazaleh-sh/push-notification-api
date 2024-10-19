package ir.co.sadad.pushnotification.configs;

//import ir.bmi.identity.security.config.ModelKey;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * config for token authorization client
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private final Environment environment;

    public AuditorAwareImpl(Environment environment) {
        this.environment = environment;
    }

    /**
     * method for getting current auditor ,
     * <pre>
     *     POINT : in dev profile we bypass this method . and return mock clientId .
     * </pre>
     *
     * @return client Id of bmi Identity Token From Client.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        String auditId;

//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        HttpServletRequest httpServletRequest;
//        try {
//            assert requestAttributes != null;
//            httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
//        } catch (NullPointerException ex) {
//            // it doesn't come from http request
//            httpServletRequest = null;
//        }
//
//        assert httpServletRequest != null;
//        Object clientId = httpServletRequest.getAttribute(ModelKey.CLIENT_ID);
//        if (clientId != null) {
//            auditId = clientId.toString();
//        } else if (Arrays.stream(environment.getActiveProfiles()).anyMatch(
//                env -> (env.equalsIgnoreCase("qa")))) {
//            auditId = "qa";
//        } else
            auditId = "not-match-id";

        return Optional.of(auditId);

    }

}