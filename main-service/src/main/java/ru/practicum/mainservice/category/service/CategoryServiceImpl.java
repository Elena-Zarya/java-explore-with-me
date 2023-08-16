package ru.practicum.mainservice.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.Pages;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @Override
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.newDtoToCategory(newCategoryDto);
        try {
            category = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Add category: {}", category.getName());
        return categoryMapper.categoryToDto(category);
    }

    @Transactional
    @Override
    public CategoryDto deleteCategory(long categoryId) {
        CategoryDto categoryDto = getCategoryById(categoryId);
        log.info("Deleted category with id = {}", categoryId);
        categoryRepository.deleteById(categoryId);
        return categoryDto;
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(long categoryId, NewCategoryDto newCategoryDto) {
        getCategoryById(categoryId);
        Category categoryUpdate = categoryMapper.newDtoToCategory(newCategoryDto);
        categoryUpdate = categoryRepository.save(categoryUpdate);
        log.info("Update category: {}", categoryUpdate.getName());
        return categoryMapper.categoryToDto(categoryUpdate);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = Pages.getPage(from, size);
        List<Category> categories;
        categories = categoryRepository.findAll(page).toList();
        log.info("Get all categories");
        return categories.stream()
                .map(categoryMapper::categoryToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            log.info("Category with id={} was not found ", categoryId);
            throw new NotFoundException("Category with id=" + categoryId + " was not found ");
        }
        return categoryMapper.categoryToDto(category.get());
    }
}
