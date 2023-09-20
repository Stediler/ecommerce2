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
    @PostMapping("/create-cart-to-add-item")

    public ResponseEntity<String> addItemToCart(@RequestBody AddItemRequest request) {
        Long itemId = request.getItemId(); // Obtém o ID do item da solicitação

        // Verifique se o item existe no banco de dados
        Items itemToAdd = itemsRepository.findById(itemId).orElse(null);

        if (itemToAdd == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found with ID: " + itemId);
        }

        // Verifique se o item está disponível para adicionar ao carrinho
        if (itemToAdd.getAvailableQuantity() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item is not available.");
        }

        // Subtraia 1 da quantidade disponível do item
        itemToAdd.setAvailableQuantity(itemToAdd.getAvailableQuantity() - 1);
        itemsRepository.save(itemToAdd); // Salve a atualização no banco de dados

        // Gere um novo customerId automaticamente
        String generatedCustomerId = UUID.randomUUID().toString();
        Cart cart = new Cart();
        cart.setCustomerId(UUID.fromString(generatedCustomerId));

        // Adicione o item ao carrinho com quantidade fixa de 1
        cart.addItem(itemToAdd, 1);

        // Salve o carrinho no banco de dados
        cartRepository.save(cart);

        return ResponseEntity.status(HttpStatus.OK).body("Item added to cart successfully. Customer ID: " + generatedCustomerId);
    }

    //Rota para adicionar item em carrinho que já existe
    @PostMapping("/add-item-to-existing-cart")
    public ResponseEntity<String> addItemToExistingCart(@RequestBody AddItemRequest request) {
        Long itemId = request.getItemId(); // Obtém o ID do item da solicitação
        UUID customerId = request.getCustomerId(); // Obtém o ID do cliente da solicitação

        // Verifique se o item existe no banco de dados
        Items itemToAdd = itemsRepository.findById(itemId).orElse(null);

        if (itemToAdd == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found with ID: " + itemId);
        }

        // Verifique se o item está disponível para adicionar ao carrinho
        if (itemToAdd.getAvailableQuantity() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item is not available.");
        }

        // Verifique se o carrinho com o customerId existe no banco de dados
        Cart existingCart = cartRepository.findByCustomerId(customerId);

        if (existingCart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found for customer ID: " + customerId);
        }

        // Verifique se o cliente já atingiu o limite máximo para esse item
        int limit = itemToAdd.getLimitPerson();
        int existingQuantity = existingCart.getItemQuantity(itemToAdd);

        if (existingQuantity >= limit) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Você já atingiu o limite máximo desse item.");
        }

        // Subtraia 1 da quantidade disponível do item
        itemToAdd.setAvailableQuantity(itemToAdd.getAvailableQuantity() - 1);
        itemsRepository.save(itemToAdd); // Salve a atualização no banco de dados

        // Adicione o item ao carrinho com quantidade fixa de 1
        existingCart.addItem(itemToAdd, 1);

        // Atualize o carrinho no banco de dados
        cartRepository.save(existingCart);

        return ResponseEntity.status(HttpStatus.OK).body("Item added to existing cart successfully. Customer ID: " + customerId);
    }
}
