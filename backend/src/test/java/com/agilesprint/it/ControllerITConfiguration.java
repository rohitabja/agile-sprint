package com.agilesprint.it;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.agilesprint.controllers.ActivityController;
import com.agilesprint.controllers.ApiExceptionHandler;
import com.agilesprint.controllers.ApiHelloController;
import com.agilesprint.controllers.AuthController;
import com.agilesprint.controllers.BoardController;
import com.agilesprint.controllers.CommentController;
import com.agilesprint.controllers.TaskController;
import com.agilesprint.controllers.WorkspaceController;

final class ControllerITConfiguration {
    private ControllerITConfiguration() {
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(excludeName = {
            "org.springframework.boot.mongodb.autoconfigure.MongoReactiveAutoConfiguration",
            "org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveAutoConfiguration",
            "org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveRepositoriesAutoConfiguration"
    })
    @Import({
            ActivityController.class,
            ApiExceptionHandler.class,
            ApiHelloController.class,
            AuthController.class,
            BoardController.class,
            CommentController.class,
            TaskController.class,
            WorkspaceController.class,
            TestSecurityConfiguration.class
    })
    static class TestApplication {
    }

    @TestConfiguration
    static class TestSecurityConfiguration {
        @Bean
        ReactiveUserDetailsService testUsers() {
            return new MapReactiveUserDetailsService(
                    User.withUsername("alice").password("{noop}password").roles("USER").build());
        }

        @Bean
        SecurityWebFilterChain controllerTestSecurityWebFilterChain(ServerHttpSecurity http) {
            return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .httpBasic(Customizer.withDefaults())
                    .authorizeExchange(exchange -> exchange
                            .pathMatchers(HttpMethod.GET, "/api/auth/status", "/api/hello").permitAll()
                            .anyExchange().authenticated())
                    .build();
        }
    }
}
