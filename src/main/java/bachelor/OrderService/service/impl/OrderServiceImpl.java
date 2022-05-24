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
import bachelor.OrderService.service.OrderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final InventoryServiceApi inventoryServiceApi;
    private final ModelMapper mapper;
    private final Gson gson;

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
                .products(products)
                .purchaser(purchaser)
                .address(address)
                .note(orderDto.getNote())
                .build();

        return orderRepository.save(order);
    }

    private List<Product> removeProductFromInventory(List<ProductDto> productsDto) {
        if (productsDto.size() == 1) {
            //TODO: add KMS encoding
            String json = gson.toJson(productsDto.get(0));
            byte[] bytes = Base64.getEncoder().encode(json.getBytes());
            ///////

            String response = inventoryServiceApi.orderProduct(new String(bytes)).getBody();

            //TODO: add KMS decoding
            byte[] responseBytes = Base64.getDecoder().decode(response.getBytes());
            Product product = gson.fromJson(new String(responseBytes), Product.class);
            ///////

            return new ArrayList<>() {{
                add(product);
            }};
        } else {

            //TODO: add KMS encoding
            String json = gson.toJson(productsDto);
            byte[] bytes = Base64.getEncoder().encode(json.getBytes());
            ///////

            String response = inventoryServiceApi.orderProducts(new String(bytes)).getBody();

            //TODO: add KMS decoding
            byte[] responseBytes = Base64.getDecoder().decode(response.getBytes());

            Type listType = new TypeToken<ArrayList<Product>>() {
            }.getType();
            List<Product> products = gson.fromJson(new String(responseBytes), listType);
            ///////
            return products;
        }
    }
}
