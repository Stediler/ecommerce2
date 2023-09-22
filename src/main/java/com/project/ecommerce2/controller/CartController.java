package com.project.ecommerce2.controller;

import com.project.ecommerce2.model.AddItemRequest;
import com.project.ecommerce2.model.Cart;
import com.project.ecommerce2.model.Items;
import com.project.ecommerce2.repository.CartRepository;
import com.project.ecommerce2.repository.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private CartRepository cartRepository;

    //Rota para adicionar item em carrinho
    @PostMapping("/add-items-to-cart")
    public ResponseEntity<String> addItemsToCart(@RequestBody AddItemRequest request) {
        Map<Long, Integer> itemQuantities = request.getItemQuantities();
        UUID customerId = request.getCustomerId();

        List<String> exceededItems = new ArrayList<>(); // Para rastrear quais itens excederam o limite

        Cart cart = null; // Crie um carrinho apenas se pelo menos um item estiver dentro do limite
        boolean itemsAddedToCart = false; // Variável para rastrear se pelo menos um item foi adicionado com sucesso ao carrinho

        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();

            Items itemToAdd = itemsRepository.findById(itemId).orElse(null);

            if (itemToAdd == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found with ID: " + itemId);
            }

            if (itemToAdd.getAvailableQuantity() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item with ID " + itemId + " is not available.");
            }

            int limit = itemToAdd.getLimitPerson();

            if (quantity > limit) {
                exceededItems.add(itemToAdd.getName()); // Item excedeu o limite
            } else {

                if (customerId == null) {
                    customerId = UUID.randomUUID();
                }

                if (cart == null) { // Verifique se o carrinho existe e, se não, crie-o
                    cart = cartRepository.findByCustomerId(customerId);

                    if (cart == null) {
                        cart = new Cart();
                        cart.setCustomerId(customerId);
                    }
                }

                int existingQuantity = cart.getItemQuantity(itemToAdd); // Verifique se a quantidade total de itens no carrinho não ultrapassa o limite

                if (existingQuantity + quantity <= limit) {
                    itemToAdd.setAvailableQuantity(itemToAdd.getAvailableQuantity() - quantity);
                    itemsRepository.save(itemToAdd);

                    cart.addItem(itemToAdd, quantity);
                    itemsAddedToCart = true;
                } else {
                    exceededItems.add(itemToAdd.getName());
                }
            }
        }

        // Verifique se pelo menos um item foi adicionado ao carrinho antes de exibir a mensagem
        if (itemsAddedToCart) {
            // Salve o carrinho dentro da transação
            cartRepository.save(cart);

            // Verifique se todos os itens excederam o limite
            if (exceededItems.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("Items added to cart successfully. Customer ID: " + customerId);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("Items added to cart successfully. Customer ID: " + customerId
                        + "\nThe following items exceeded the maximum limit per person and were not added to the cart: " + String.join(", ", exceededItems));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All items exceed the maximum limit.");
        }
    }
}












