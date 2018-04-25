package com.twobomb.app.security;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.Discipline;
import com.twobomb.entity.Group;
import com.twobomb.entity.Role;
import com.twobomb.entity.User;
import com.twobomb.repository.UserRepository;
import com.twobomb.service.DisciplineService;
import com.twobomb.service.RoleService;
import com.twobomb.service.UserService;
import org.atmosphere.config.service.Post;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PostLoad;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.util.List;


@EnableWebSecurity
@Configuration
@EnableTransactionManagement
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
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

//    @Autowired
//    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;


//
//
//    @Bean
//    public HibernateTransactionManager transactionManager() {
//        SessionFactory sessionFactory = localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
//        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
//        transactionManager.setSessionFactory(sessionFactory);
//        return transactionManager;
//    }
    /**
     HIBERNATE CONFIGURATION
     */

//    @Bean
//    public static DataSource dataSource() {
//        DriverManagerDataSource driver = new DriverManagerDataSource();
//        driver.setDriverClassName("org.postgresql.Driver");
//        driver.setUrl("jdbc:postgresql://localhost:3333/diplom_bd?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
//        driver.setUsername("postgres");
//        driver.setPassword("123456");
//        return driver;
//    }

//    @Bean
//    SessionFactory sessionFactory(LocalContainerEntityManagerFactoryBean factoryBean){
//        EntityManagerFactory entityManagerFactory =factoryBean.getObject();
//        return entityManagerFactory.unwrap(SessionFactory.class);
//    }
//    @Bean
//    public static LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setDatabase(Database.POSTGRESQL);
//        vendorAdapter.setGenerateDdl(true);
//
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setJpaVendorAdapter(vendorAdapter);
//        factory.setPackagesToScan("com.twobomb.entity");
//        factory.setDataSource(dataSource());
//
//        return factory;
//    }
//    @Bean
//    public SessionFactory sessionFactory(@Qualifier("entityManagerFactory") EntityManagerFactory emf) {
//        return emf.unwrap(SessionFactory.class);
//    }

//    @Bean
//    public static SessionFactory sessionFactory(LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
//
//    }
/*    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.twobomb.entity");
        factory.setJpaDialect(new HibernateJpaDialect());
        factory.setDataSource(dataSource());
        factory.afterPropertiesSet();

        return factory.getObject();
    }


    @Bean
    public SessionFactory getSessionFactory() {
        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .configure( "hibernate.cfg.xml" )
                .build();

        Metadata metadata = new MetadataSources(standardRegistry)
                .getMetadataBuilder().build();

        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
                .build();

        return sessionFactory;
    }*/
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
                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())

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
                 "/images/**",
                "/frontend/**",
                "/webjars/**",
                "/icons/**",
                "/styles/**",
                "/h2-console/**",

                "/favicon.ico",
                "/manifest.json",
                "/sw.js",
                "/offline-page.html",
                "/frontend-es5/**", "/frontend-es6/**"
                    );

    }

}
