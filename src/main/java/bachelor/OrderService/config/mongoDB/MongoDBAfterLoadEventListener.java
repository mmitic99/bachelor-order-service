package bachelor.OrderService.config.mongoDB;

import bachelor.OrderService.model.Address;
import bachelor.OrderService.model.Product;
import bachelor.OrderService.model.Purchaser;
import bachelor.OrderService.service.AwsKeyManagementService;
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
import java.util.List;

public class MongoDBAfterLoadEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private AwsKeyManagementService awsKeyManagementService;
    @Autowired
    private Gson gson;

    @SneakyThrows
    @Override
    public void onAfterLoad(AfterLoadEvent<Object> event) {

        Document eventObject = event.getDocument();

        List<String> keysNotToEncrypt = Arrays.asList("_class", "_id");

        for (String key :
                eventObject.keySet()) {
            if (!keysNotToEncrypt.contains(key)) {
                Binary bytes = (Binary) eventObject.get(key);

                String json = this.awsKeyManagementService.DecryptText(bytes.getData());

                if(key.equals("products")){
                    Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
                    eventObject.put(key, gson.fromJson(json, listType));
                }
                else if(key.equals("purchaser")){
                    eventObject.put(key, gson.fromJson(json, Purchaser.class));
                }
                else if(key.equals("address")){
                    eventObject.put(key, gson.fromJson(json, Address.class));
                }

            }
        }

        super.onAfterLoad(event);

    }
}
