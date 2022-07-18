package bachelor.OrderService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("orders")
public class Order {
    @Id
    private ObjectId id;
    private List<Product> products;
    private Purchaser purchaser;
    private Address address;
    private String note;
    private String key;
}
