package com.orders;

import com.orders.application.dto.OrderRequestDTO;
import com.orders.application.dto.OrderResponseDTO;
import com.orders.application.mapper.OrderMapper;
import com.orders.application.service.OrderService;
import com.orders.domain.entity.Order;
import com.orders.domain.enums.OrderStatus;
import com.orders.domain.repository.OrderRepository;
import com.orders.infrastructure.client.UserClient;
import com.orders.infrastructure.exception.CommandCancelleOrDeliveredException;
import com.orders.infrastructure.exception.ResourceNotFoundException;
import com.orders.infrastructure.metrics.OrderMetrics;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class MsOrdersApplicationTests {


	@InjectMocks
	private OrderService orderService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderMapper orderMapper;

	@Mock
	private OrderMetrics orderMetrics;

	@Mock
	private UserClient userClient;

	private Order order;
	private OrderRequestDTO orderRequestDTO;
	private OrderResponseDTO orderResponseDTO;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		orderRequestDTO = OrderRequestDTO.builder()
				.userId(1L)
				.status(OrderStatus.PENDING)
				.shippingAddress("Rue des lilas")
				.orderItems(List.of())
				.build();

		order = Order.builder()
				.id(1L)
				.userId(1L)
				.status(OrderStatus.PENDING)
				.shippingAddress("Rue des lilas")
				.build();

		orderResponseDTO = OrderResponseDTO.builder()
				.id(1L)
				.userId(1L)
				.status(OrderStatus.PENDING)
				.shippingAddress("Rue des lilas")
				.build();
	}

	@Test
	void createOrder_shouldSaveOrder_whenUserExists() {
		log.info("Début test: createOrder_shouldSaveOrder_whenUserExists");
		when(userClient.isUserExist(1L)).thenReturn(true);
		when(orderMapper.toEntity(orderRequestDTO)).thenReturn(order);
		when(orderRepository.save(order)).thenReturn(order);
		when(orderMapper.toDTO(order)).thenReturn(orderResponseDTO);

		OrderResponseDTO response = orderService.createOrder(orderRequestDTO);

		assertNotNull(response);
		assertEquals(1L, response.getId());
		verify(orderMetrics, times(1)).incrementCreatedOrders(OrderStatus.PENDING);
		verify(orderRepository, times(1)).save(order);
		log.info("Fin test: createOrder_shouldSaveOrder_whenUserExists\n\n\n");
	}

	@Test
	void createOrder_shouldThrowException_whenUserNotFound() {
		log.info("Début test: createOrder_shouldThrowException_whenUserNotFound");
		when(userClient.isUserExist(1L)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderRequestDTO));
		verify(orderRepository, never()).save(any());
		log.info("Fin test: createOrder_shouldThrowException_whenUserNotFound\n\n\n");
	}

	@Test
	void getOrderById_shouldReturnOrder_whenFound() {
		log.info("Début test: getOrderById_shouldReturnOrder_whenFound");
		when(userClient.isUserExist(1L)).thenReturn(true);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(orderMapper.toDTO(order)).thenReturn(orderResponseDTO);

		OrderResponseDTO response = orderService.getOrderById(1L);

		assertEquals(OrderStatus.PENDING, response.getStatus());
		log.info("Fin test: getOrderById_shouldReturnOrder_whenFound\n\n\n");
	}

	@Test
	void getOrderById_shouldThrowException_whenUserNotFound() {
		log.info("Début test: getOrderById_shouldThrowException_whenUserNotFound");
		when(userClient.isUserExist(1L)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
		log.info("Fin test: getOrderById_shouldThrowException_whenUserNotFound\n\n\n");
	}

	@Test
	void updateOrderStatus_shouldUpdateStatus_whenOrderValid() {
		log.info("Début test: updateOrderStatus_shouldUpdateStatus_whenOrderValid");

		when(userClient.isUserExist(1L)).thenReturn(true);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		// Simule la sauvegarde et met à jour le statut de l'entité
		when(orderRepository.save(order)).thenAnswer(invocation -> {
			order.setStatus(OrderStatus.DELIVERED);
			return order;
		});

		// Mapper renvoie un DTO avec le nouveau statut
		when(orderMapper.toDTO(order)).thenAnswer(invocation -> OrderResponseDTO.builder()
				.id(order.getId())
				.userId(order.getUserId())
				.status(order.getStatus())
				.shippingAddress(order.getShippingAddress())
				.build());

		OrderResponseDTO response = orderService.updateOrderStatus(1L, "DELIVERED");

		assertEquals(OrderStatus.DELIVERED, response.getStatus());
		verify(orderRepository, times(1)).save(order);
		log.info("Fin test: updateOrderStatus_shouldUpdateStatus_whenOrderValid");
	}

	@Test
	void updateOrderStatus_shouldThrowException_whenOrderCancelledOrDelivered() {
		log.info("Début test: updateOrderStatus_shouldThrowException_whenOrderCancelledOrDelivered");
		order.setStatus(OrderStatus.CANCELLED);
		when(userClient.isUserExist(1L)).thenReturn(true);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		assertThrows(CommandCancelleOrDeliveredException.class, () -> orderService.updateOrderStatus(1L, "PENDING"));
		log.info("Fin test: updateOrderStatus_shouldThrowException_whenOrderCancelledOrDelivered\n\n\n");
	}

	@Test
	void deleteOrder_shouldDelete_whenOrderExists() {
		log.info("Début test: deleteOrder_shouldDelete_whenOrderExists");
		when(userClient.isUserExist(1L)).thenReturn(true);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		orderService.deleteOrder(1L);

		verify(orderRepository, times(1)).delete(order);
		log.info("Fin test: deleteOrder_shouldDelete_whenOrderExists\n\n\n");
	}
}
