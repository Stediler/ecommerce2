package com.project.ecommerce2.controller;

import com.project.ecommerce2.model.AddItemRequest;
import com.project.ecommerce2.model.Cart;
import com.project.ecommerce2.model.CartItems;
import com.project.ecommerce2.model.Items;
import com.project.ecommerce2.repository.CartRepository;
import com.project.ecommerce2.repository.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private CartRepository cartRepository;

    //Rota para adicionar item em carrinho que não existe
    @PostMapping("/add-items-to-cart")
    public ResponseEntity<String> addItemsToCart(@RequestBody AddItemRequest request) {
        List<Long> itemIds = request.getItemIds(); // Obtém a lista de IDs de itens da solicitação
        UUID customerId = request.getCustomerId(); // Obtém o ID do cliente da solicitação (pode ser nulo)

        // Verifique se o cliente foi especificado e, se não, gere um novo customerId automaticamente
        if (customerId == null) {
            customerId = UUID.randomUUID();
        }

        // Crie um novo carrinho para o cliente (ou obtenha um carrinho existente)
        Cart cart = cartRepository.findByCustomerId(customerId);
        if (cart == null) {
            cart = new Cart();
            cart.setCustomerId(customerId);
        }

        // Processar cada ID de item e adicioná-los ao carrinho
        for (Long itemId : itemIds) {
            // Verifique se o item existe no banco de dados
            Items itemToAdd = itemsRepository.findById(itemId).orElse(null);

            if (itemToAdd == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found with ID: " + itemId);
            }

            // Verifique se o item está disponível para adicionar ao carrinho
            if (itemToAdd.getAvailableQuantity() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item with ID " + itemId + " is not available.");
            }

            // Verifique se o cliente já atingiu o limite máximo para esse item
            int limit = itemToAdd.getLimitPerson();
            int existingQuantity = cart.getItemQuantity(itemToAdd);

            if (existingQuantity >= limit) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Você atingiu o limite máximo para esse item");
            }

            // Subtraia 1 da quantidade disponível do item
            itemToAdd.setAvailableQuantity(itemToAdd.getAvailableQuantity() - 1);
            itemsRepository.save(itemToAdd); // Salve a atualização no banco de dados

            // Adicione o item ao carrinho com quantidade fixa de 1
            cart.addItem(itemToAdd, 1);
        }

        // Salve o carrinho no banco de dados
        cartRepository.save(cart);

        return ResponseEntity.status(HttpStatus.OK).body("Items added to cart successfully. Customer ID: " + customerId);
    }
}







