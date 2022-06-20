package bachelor.OrderService.controller;

import bachelor.OrderService.dto.OrderDto;
import bachelor.OrderService.model.Order;
import bachelor.OrderService.service.OrderService;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.PathParam;

@RestController
@RequestMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final Gson gson;

    @PostMapping("")
    public ResponseEntity<Order> createOrder(@RequestBody OrderDto orderDto){
        return ResponseEntity.ok(orderService.createOrder(orderDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String id){
        return ResponseEntity.ok(orderService.getById(id));
    }

}
