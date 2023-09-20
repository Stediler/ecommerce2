package com.project.ecommerce2.controller;

import com.project.ecommerce2.model.Cart;
import com.project.ecommerce2.model.CartItems;
import com.project.ecommerce2.model.Items;
import com.project.ecommerce2.repository.CartRepository;
import com.project.ecommerce2.repository.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
public class ItemsController {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private CartRepository cartRepository;

    @GetMapping("/list")
    public List<Items> list() {

        List<Items> listItems = itemsRepository.findAll();
        return listItems;
    }

    // Rota para visualizar os itens no carrinho do cliente com base no customerId
    @GetMapping("/view-cart-items")
    public ResponseEntity<List<CartItems>> viewCartItems(@RequestParam(name = "customerId") UUID customerId) {
        // Verifique se o carrinho com o customerId existe no banco de dados
        Cart existingCart = cartRepository.findByCustomerId(customerId);

        if (existingCart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Carrinho não encontrado
        }

        List<CartItems> cartItems = existingCart.getCartItems();

        return ResponseEntity.status(HttpStatus.OK).body(cartItems); // Retorna a lista de itens no carrinho do cliente
    }
}


