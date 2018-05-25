package com.twobomb.app.security;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.Role;
import com.twobomb.entity.User;
import com.twobomb.service.RoleService;
import com.twobomb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Arrays;
import java.util.Collections;


@EnableWebSecurity
@Configuration
@EnableTransactionManagement
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    public static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/" + AppConst.PAGE_DEFAULT;

    @Autowired
    private UserDetailsService userDetailsService;


    @Autowired
    ApplicationContext ctx;

    @Autowired
    @Qualifier("RoleService")
    RoleService roleService;

    @Autowired
    @Qualifier("UserService")
    UserService userService;

    /**
     * The password encoder to use when encrypting passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean(name = "CurrentUser")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public User currentUser() {
        String username = SecurityUtils.getUsername();
        if(username == null){
            try {
                throw new Exception("No authorize user!");
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        User user = userService.getByLogin(username);
        return user;
    }

    /**
     * Registers our UserDetailsService and the password encoder to be used on login attempts.
     */
    @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    /**
     * Require login to access internal pages and configure login form.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()

                // Register our CustomRequestCache, that saves unauthorized access attempts, so
                // the user is redirected after login.
                .requestCache().requestCache(new CustomRequestCache())

                // Restrict access to our application.
                .and().authorizeRequests()

                // Allow all flow internal requests.
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()


                // Allow all requests by logged in users.
                .anyRequest().hasAnyAuthority(roleService.getAllRoles())

                // Configure the login pages.
                .and().formLogin().loginPage(LOGIN_URL).usernameParameter("username").passwordParameter("password").permitAll()
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .failureUrl(LOGIN_FAILURE_URL)

                // Register the success handler that redirects users to the pages they last tried
                // to access
//                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())

                // Configure logout
                .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/VAADIN/**",
                "/VAADIN/dynamic/resource/**",
                 "/vaadinServlet/**",
                 "/images/**",
                "/frontend/**",
                "/webjars/**",
                "/icons/**",
                "/styles/**",
                "/h2-console/**",

                "/manifest.json",
                "/service_worker.js",
                "/offline-page.html",
                "/frontend-es5/**", "/frontend-es6/**"
                    );

    }


}
