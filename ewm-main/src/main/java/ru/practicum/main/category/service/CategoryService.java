package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addByAdmin(NewCategoryDto newCategoryDto);

    void deleteByAdmin(long catId);

    CategoryDto updateByAdmin(long catId, CategoryDto categoryDto);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(long catId);
}
