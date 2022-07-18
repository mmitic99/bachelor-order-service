package bachelor.OrderService.service.impl;

import bachelor.InventoryService.api.InventoryServiceApi;
import bachelor.InventoryService.api.ProductDto;
import bachelor.OrderService.dto.OrderDto;
import bachelor.OrderService.exception.BadRequestException;
import bachelor.OrderService.model.Address;
import bachelor.OrderService.model.Order;
import bachelor.OrderService.model.Product;
import bachelor.OrderService.model.Purchaser;
import bachelor.OrderService.repository.OrderRepository;
import bachelor.OrderService.service.AwsKeyManagementService;
import bachelor.OrderService.service.OrderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final InventoryServiceApi inventoryServiceApi;
    private final ModelMapper mapper;
    private final Gson gson;
    private final AwsKeyManagementService awsKeyManagementService;


    @Override
    public Order createOrder(OrderDto orderDto) {

        if (orderDto.getProducts().isEmpty()) {
            throw new BadRequestException("Empty ordered products list");
        }
        if (orderDto.getCity() == null || orderDto.getCity().isEmpty() || orderDto.getStreetAndNumber() == null || orderDto.getStreetAndNumber().isEmpty() || orderDto.getPostalCode() == null || orderDto.getPostalCode().isEmpty() || orderDto.getName() == null || orderDto.getName().isEmpty() || orderDto.getPhoneNumber() == null || orderDto.getPhoneNumber().isEmpty()) {
            throw new BadRequestException("You don't entered required fields");
        }
        List<Product> products = removeProductFromInventory(orderDto.getProducts());

        Purchaser purchaser = Purchaser.builder()
                .name(orderDto.getName())
                .surname(orderDto.getSurname())
                .email(orderDto.getEmail())
                .phoneNumber(orderDto.getPhoneNumber())
                .build();

        Address address = Address.builder()
                .city(orderDto.getCity())
                .streetAndNumber(orderDto.getStreetAndNumber())
                .postalCode(orderDto.getPostalCode())
                .build();

        Order order = Order.builder()
                .key(awsKeyManagementService.GenerateDataKey().getCiphertext())
                .products(products)
                .purchaser(purchaser)
                .address(address)
                .note(orderDto.getNote())
                .build();

        return orderRepository.save(order);
    }

    @Override
    public OrderDto getById(String id) {
        if (ObjectId.isValid(id)) {
            ObjectId objectId = new ObjectId(id);
            Order order = orderRepository.findById(objectId).orElseThrow(() -> new BadRequestException("Product is not found"));

            Type listType = new TypeToken<ArrayList<ProductDto>>() {
            }.getType();
            return OrderDto.builder().
                    city(order.getAddress().getCity()).
                    streetAndNumber(order.getAddress().getStreetAndNumber()).
                    postalCode(order.getAddress().getPostalCode()).
                    name(order.getPurchaser().getName()).
                    surname(order.getPurchaser().getSurname()).
                    email(order.getPurchaser().getEmail()).
                    phoneNumber(order.getPurchaser().getPhoneNumber()).
                    products(mapper.map(order.getProducts(), listType)).
                    note(order.getNote()).
                    build();
        } else {
            throw new BadRequestException("Product id is invalid");
        }
    }

    private List<Product> removeProductFromInventory(List<ProductDto> productsDto) {
        if (productsDto.size() == 1) {
            String json = gson.toJson(productsDto.get(0));
            System.out.println("Method removeProductFromInventory - json param: " + json);

            String keyId = awsKeyManagementService.GetKeyByAlias("bachelor-order");
            byte[] encrypted = awsKeyManagementService.EncryptText(json, keyId);
            System.out.println("Method removeProductFromInventory - encrypted json param: " + Arrays.toString(encrypted));

            byte[] response = inventoryServiceApi.orderProduct(encrypted).getBody();
            System.out.println("Method removeProductFromInventory - response: " + Arrays.toString(response));

            String decrypted = awsKeyManagementService.DecryptText(response);
            System.out.println("Method removeProductFromInventory - decrypted response: " + decrypted);

            Product product = gson.fromJson(decrypted, Product.class);

            return new ArrayList<>() {{
                add(product);
            }};
        } else {

            String json = gson.toJson(productsDto);
            System.out.println("Method removeProductFromInventory - json param: " + json);

            String keyId = awsKeyManagementService.GetKeyByAlias("bachelor-order");
            byte[] encrypted = awsKeyManagementService.EncryptText(json, keyId);
            System.out.println("Method removeProductFromInventory - encrypted json param: " + Arrays.toString(encrypted));

            byte[] response = inventoryServiceApi.orderProducts(encrypted).getBody();
            System.out.println("Method removeProductFromInventory - response: " + Arrays.toString(response));
            Type listType = new TypeToken<ArrayList<Product>>() {
            }.getType();
            String decrypted = awsKeyManagementService.DecryptText(response);
            System.out.println("Method removeProductFromInventory - decrypted response: " + decrypted);
            List<Product> products = gson.fromJson(decrypted, listType);

            return products;
        }
    }
}
