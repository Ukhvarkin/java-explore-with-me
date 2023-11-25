package ru.practicum.main.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto add(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("POST : Создание категории с параметрами: {}", newCategoryDto);
        return categoryService.addByAdmin(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@Positive @PathVariable(name = "catId") long catId) {
        log.info("DELETE : Удаление категории с id: {}", catId);
        categoryService.deleteByAdmin(catId);
    }

    @PatchMapping("/{catId}")
    CategoryDto update(
        @Positive @PathVariable(name = "catId") long catId,
        @Valid @RequestBody CategoryDto categoryDto
    ) {
        log.info("PATCH : Модификация категории c id: {}", catId);
        return categoryService.updateByAdmin(catId, categoryDto);
    }
}
