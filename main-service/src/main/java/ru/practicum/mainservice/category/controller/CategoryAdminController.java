package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> addNewCategory(@Valid @RequestBody(required = false) NewCategoryDto newCategoryDto) {
        log.info("Received POST request: new category");
        return new ResponseEntity<>(categoryService.addNewCategory(newCategoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable("id") Long categoryId) {
        log.info("Received DELETE request: delete category {}", categoryId);
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable("id") Long categoryId,
                                                      @Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Received PATCH request: update category {}", categoryId);
        return new ResponseEntity<>(categoryService.updateCategory(categoryId, newCategoryDto), HttpStatus.OK);
    }
}
