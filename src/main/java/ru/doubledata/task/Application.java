package ru.doubledata.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.doubledata.task.config.ApplicationConfig;
import ru.doubledata.task.config.WebMvcConfig;

@ComponentScan
@SpringBootApplication
@Import({ApplicationConfig.class, WebMvcConfig.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }
}

