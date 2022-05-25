package bachelor.OrderService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;

@Configuration
public class KmsClientConfig {

    @Bean
    public KmsClient KmsClient() {
        AwsCredentials awsCredentials = AwsBasicCredentials.create("AKIAWGYYFVFV3Y27FYF2", "ePji4x5ZTFP0RYEskjXNa+F2/hmfupAOdnd28JHF");
        return KmsClient.builder().credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).region(Region.US_EAST_1).build();
    }
}
