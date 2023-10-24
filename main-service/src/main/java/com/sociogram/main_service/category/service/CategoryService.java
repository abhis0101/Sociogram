package com.sociogram.main_service.category.service;

import com.sociogram.main_service.category.dto.CategoryDto;
import com.sociogram.main_service.category.model.Category;

import java.util.List;

public interface CategoryService {
    Category createNewCategory(CategoryDto categoryDto);

    void removeCategory(Long id);

    Category updateCategory(Long id, CategoryDto categoryDto);

    List<Category> findAllCategory(int from, int size);

    Category getById(Long id);
}
