package com.sociogram.main_service.category.controller;

import com.sociogram.main_service.category.model.Category;
import com.sociogram.main_service.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> findAllCategory(@RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                          @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        return categoryService.findAllCategory(from, size);
    }

    @GetMapping("/{catId}")
    public Category getCategoryById(@PathVariable Long catId) {
        return categoryService.getById(catId);
    }
}
