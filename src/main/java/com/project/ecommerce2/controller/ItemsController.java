package com.project.ecommerce2.controller;

import com.project.ecommerce2.model.Items;
import com.project.ecommerce2.repository.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemsController {

    @Autowired
    private ItemsRepository itemsRepository;

    @GetMapping("/list")
    public List<Items> list() {

        List<Items> listItems = itemsRepository.findAll();
        return listItems;
    }
}


