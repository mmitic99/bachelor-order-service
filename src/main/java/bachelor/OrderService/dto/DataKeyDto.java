package bachelor.OrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataKeyDto {
    private String plaintext;   //Base64Encoded
    private String ciphertext;  //Base64Encoded
}
