package org.github.myibu.httpclient.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author myibu
 * @since 1.0
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import({HttpClientRegistrar.class})
public @interface EnableHttpClient {
    String[] basePackages() default {};
}
