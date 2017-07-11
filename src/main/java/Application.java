

import javax.annotation.PostConstruct;
import tasks.ImportJsonData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@EnableScheduling
public class Application {


    public static void main(String[] args) {
        // Start Springboot Application
        SpringApplication.run(Application.class, args);
        ImportJsonData jsonParser = new ImportJsonData();
            try {
                jsonParser.parseActivities();
                //jsonParser.parseBixies();
            } catch (Exception e) {
                System.out.println(e);
            }

    }
    
}
