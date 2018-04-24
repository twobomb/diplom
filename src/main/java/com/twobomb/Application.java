package com.twobomb;

import com.twobomb.app.security.SecurityConfiguration;
import com.twobomb.entity.User;
import com.twobomb.repository.UserRepository;
import com.twobomb.service.ThemeService;
import com.twobomb.service.UserService;
import com.twobomb.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * Spring boot web application initializer.
 */
@SpringBootApplication
        (scanBasePackageClasses = { SecurityConfiguration.class, MainView.class, Application.class,
        UserService.class })

@EnableJpaRepositories(basePackageClasses = { UserRepository.class })
@EntityScan(basePackageClasses = { User.class })

public class Application  {




    public static void main(String[] args) {


        ApplicationContext ctx = SpringApplication.run(Application.class, args);
//        ctx.getBean("mytest");

    }



    @Bean(name = "mytest")
    public boolean test(Environment env){


        return true;
    }

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(Application.class);
//    }
}
