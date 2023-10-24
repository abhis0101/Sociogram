package com.sociogram.main_service.category.controller;

import com.sociogram.main_service.category.dto.CategoryDto;
import com.sociogram.main_service.category.model.Category;
import com.sociogram.main_service.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Category createNewCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.createNewCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Long catId) {
        categoryService.removeCategory(catId);
    }

    @PatchMapping("/{catId}")
    public Category updateCategory(@PathVariable Long catId, @Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }
}
