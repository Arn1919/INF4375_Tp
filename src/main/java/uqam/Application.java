package uqam;



import uqam.tasks.ImportJsonData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        // Start Springboot Application
        SpringApplication.run(Application.class, args);


    }
    
}
