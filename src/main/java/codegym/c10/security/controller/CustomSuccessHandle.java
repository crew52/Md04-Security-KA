package codegym.c10.security.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomSuccessHandle extends SimpleUrlAuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        if (response.isCommitted()) {
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        String url;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<>();

        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }

        if (isDba(roles)) {
            // Nếu là tài khoản đăng nhập có role là DBA
            // thì điều hướng đến /dba
            url = "/dba";
        } else if (isAdmin(roles)) {
            // Nếu là tài khoản đăng nhập có role là ADMIN
            // thì điều hướng đến /admin
            url = "/admin";
        } else if (isUser(roles)) {
            // Nếu là tài khoản đăng nhập có role là USER
            // thì điều hướng đến /home
            url = "/user";
        } else {
            // Nếu tài khoản đăng nhập không có quyền truy cập
            // sẽ điều hướng tới /accessDenied
            url = "/accessDenied";
        }

        return url;
    }

    private boolean isUser(List<String> roles) {
        return roles.contains("USER");
    }

    private boolean isAdmin(List<String> roles) {
        return roles.contains("ADMIN");
    }

    private boolean isDba(List<String> roles) {
        return roles.contains("DBA");
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
}
