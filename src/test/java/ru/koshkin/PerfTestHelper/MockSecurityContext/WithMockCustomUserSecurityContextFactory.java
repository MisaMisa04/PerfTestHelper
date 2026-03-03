package ru.koshkin.PerfTestHelper.MockSecurityContext;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import ru.koshkin.PerfTestHelper.Entities.User;

import java.util.List;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockSecurityContext> {

    @Override
    public SecurityContext createSecurityContext(WithMockSecurityContext annotation) {
        User mockUser = new User();
        mockUser.setUsername(annotation.username());
        mockUser.setId(annotation.userId());
        mockUser.setPassword(annotation.password());

        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, annotation.password(), List.of());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
