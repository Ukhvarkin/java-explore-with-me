package ru.practicum.main.category.mapper;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.model.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .build();

    }

    public static Category toModel(NewCategoryDto newCategoryDto) {
        return Category.builder()
            .name(newCategoryDto.getName())
            .build();
    }
}
