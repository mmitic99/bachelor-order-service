package bachelor.OrderService.config.mongoDB;

import bachelor.OrderService.model.Address;
import bachelor.OrderService.model.Product;
import bachelor.OrderService.model.Purchaser;
import bachelor.OrderService.service.AwsKeyManagementService;
import bachelor.OrderService.service.EncryptionService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class MongoDBAfterLoadEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private AwsKeyManagementService awsKeyManagementService;
    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private Gson gson;

    @SneakyThrows
    @Override
    public void onAfterLoad(AfterLoadEvent<Object> event) {

        Document eventObject = event.getDocument();

        String cipherKey = (String) eventObject.get("key");
        var decryptedKey = awsKeyManagementService.DecryptKey(Base64.getDecoder().decode(cipherKey));

        List<String> keysNotToEncrypt = Arrays.asList("_class", "_id", "key");

        for (String key :
                eventObject.keySet()) {
            if (!keysNotToEncrypt.contains(key)) {
                Binary bytes = (Binary) eventObject.get(key);

                String value = this.encryptionService.decrypt(bytes.getData(), decryptedKey);

                if(key.equals("products")){
                    Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
                    eventObject.put(key, gson.fromJson(value, listType));
                }
                else if(key.equals("purchaser")){
                    eventObject.put(key, gson.fromJson(value, Purchaser.class));
                }
                else if(key.equals("address")){
                    eventObject.put(key, gson.fromJson(value, Address.class));
                }

            }
        }

        super.onAfterLoad(event);

    }
}
