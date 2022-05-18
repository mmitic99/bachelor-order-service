package bachelor.OrderService;

import bachelor.InventoryService.api.InventoryServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@EnableFeignClients(clients = {InventoryServiceApi.class})
@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
@RestController
@RequestMapping("order")
class SimpleBookRestController {

	@Autowired
	private InventoryServiceApi inventoryServiceApi;

	@GetMapping(value = "", produces = "application/json")
	public String get() {
		return inventoryServiceApi.get("order");
	}
}