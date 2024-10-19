package ir.co.sadad.pushnotification.common.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * aspect for logging
 * <pre>
 *   this aspect will triggers before and after in services
 * </pre>
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

  @Before("execution(* ir.co.sadad.pushnotification.services.*.*(..))")
  public void beforeAllServiceMethods(JoinPoint joinPoint) {
    log.info("********** started executing: " + joinPoint.getSignature().getName() +
        " with method param: " + Arrays.toString(joinPoint.getArgs()));
  }

  @AfterReturning(pointcut = "execution(* ir.co.sadad.pushnotification.services..*(..))", returning = "result")
  public void afterAllServiceMethods(JoinPoint joinPoint, Object result) {
    log.info("********** completed executing: " + joinPoint.getSignature().getName() +
        " with return value: " + result);
  }


}
