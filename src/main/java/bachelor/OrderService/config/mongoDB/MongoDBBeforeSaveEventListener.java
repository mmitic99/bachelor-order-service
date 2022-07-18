package bachelor.OrderService.config.mongoDB;

import bachelor.OrderService.service.AwsKeyManagementService;
import bachelor.OrderService.service.EncryptionService;
import com.google.gson.Gson;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class MongoDBBeforeSaveEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private AwsKeyManagementService awsKeyManagementService;

    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private Gson gson;

    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {

        String keyId = awsKeyManagementService.GetKeyByAlias("bachelor-order");

        Document eventObject = event.getDocument();

        String cipherKey = (String) eventObject.get("key");
        var decryptedKey = awsKeyManagementService.DecryptKey(Base64.getDecoder().decode(cipherKey));

        List<String> keysNotToEncrypt = Arrays.asList("_class", "_id", "key");
        for (String key :
                eventObject.keySet()) {
            if(!keysNotToEncrypt.contains(key)){
                eventObject.put(key, encryptionService.encrypt(gson.toJson(eventObject.get(key)), decryptedKey));
            }
        }

        super.onBeforeSave(event);
    }
}
