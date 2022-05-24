package bachelor.OrderService.service;

import bachelor.OrderService.dto.OrderDto;
import bachelor.OrderService.model.Order;

public interface OrderService {
    Order createOrder(OrderDto orderDto);
}
