package bachelor.OrderService.config;

import bachelor.OrderService.config.mongoDB.MongoDBAfterLoadEventListener;
import bachelor.OrderService.config.mongoDB.MongoDBBeforeSaveEventListener;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

@Configuration
@AllArgsConstructor
public class ListenersConfig {

    @Bean
    public MongoDBBeforeSaveEventListener mongoDBBeforeSaveEventListener() {
        return new MongoDBBeforeSaveEventListener();
    }

    @Bean
    public MongoDBAfterLoadEventListener mongoDBAfterLoadEventListener() {
        return new MongoDBAfterLoadEventListener();
    }
}
