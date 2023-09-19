package com.project.ecommerce2.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", unique = true)
    private UUID customerId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItems> cartItems = new ArrayList<>();

    // Construtores, getters e setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public List<CartItems> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItems> cartItems) {
        this.cartItems = cartItems;
    }

    public void addItem(Items itemToAdd, int quantity) {
        if (itemToAdd == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid item or quantity.");
        }

        // Verifique se o item já está no carrinho
        for (CartItems cartItem : cartItems) {
            if (cartItem.getItem().getId().equals(itemToAdd.getId())) {
                // Item já está no carrinho, aumente a quantidade
                int newQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(newQuantity);
                return;
            }
        }

        // Se o item não estiver no carrinho, crie um novo CartItem
        CartItems newCartItem = new CartItems();
        newCartItem.setCart(this);
        newCartItem.setItem(itemToAdd);
        newCartItem.setQuantity(quantity);
        cartItems.add(newCartItem);
    }
}