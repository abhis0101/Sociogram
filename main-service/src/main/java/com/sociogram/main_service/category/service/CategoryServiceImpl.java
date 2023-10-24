package com.sociogram.main_service.category.service;

import com.sociogram.main_service.category.dto.CategoryDto;
import com.sociogram.main_service.category.mapper.CategoryMapper;
import com.sociogram.main_service.category.model.Category;
import com.sociogram.main_service.category.repository.CategoryRepository;
import com.sociogram.main_service.exception.DataConflictException;
import com.sociogram.main_service.exception.UserNotFoundException;
import com.sun.nio.sctp.IllegalReceiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.sociogram.main_service.category.mapper.CategoryMapper.toCategoriesListDto;


@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createNewCategory(CategoryDto categoryDto) {
        Optional<Category> optionalCategory = categoryRepository.findByName(categoryDto.getName());
        if (optionalCategory.isPresent()) {
            throw new DataConflictException("Name already taken");
        }
        log.info("Category saved.");
        return categoryRepository.save(CategoryMapper.toCategory(categoryDto));
    }

    @Override
    @Transactional
    public void removeCategory(Long id) {
        categoryRepository.deleteById(id);
        log.info("Category removed.");
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Категория не найдена"));
        Optional<Category> optionalCategory = categoryRepository.findByName(categoryDto.getName());
        if (optionalCategory.isPresent() && !Objects.equals(optionalCategory.get().getId(), category.getId())) {
            throw new DataConflictException("Name already taken");
        }
        category.setName(categoryDto.getName());
        log.info("Category data has been updated.");
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAllCategory(int from, int size) {
        if (size <= 0 || from < 0) {
            throw new IllegalReceiveException("Invalid parameter specified");
        }
        int page = 0;
        if (from != 0) {
            page = from / size;
        }
        log.info("Received a list of all categories.");
        return toCategoriesListDto(categoryRepository.findAll(PageRequest.of(page, size)));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Category not found"));
    }
}
