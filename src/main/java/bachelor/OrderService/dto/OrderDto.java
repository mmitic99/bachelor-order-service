package bachelor.OrderService.dto;

import bachelor.InventoryService.api.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private List<ProductDto> products;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String streetAndNumber;
    private String postalCode;
    private String city;
    private String note;
}
