package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addByAdmin(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toModel(newCategoryDto);
        log.info("[admin] : Добавление категории: {}", category.getName());
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteByAdmin(long catId) {
        categoryRepository.findById(catId).orElseThrow(
            () -> new NotFoundException(String.format(String.format("Category with id=%d was not found", catId))));
        log.info("[admin] : Удаление категории с id: {}", catId);

        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            log.warn("Существует событие с данной категорией. Надо удалить событие");
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
        log.info("Удаление категории завершено");
    }

    @Override
    public CategoryDto updateByAdmin(long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(
            () -> new NotFoundException(String.format(String.format("Category with id=%d was not found", catId))));
        log.info("[admin] : Редактирование категории: {}", category.getName());
        if (category.getName() != null) {
            category.setName(categoryDto.getName());
        }
        log.info("Категория переименована на: {}", category.getName());
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        log.info("[public] : Получение всего списка категорий");
        return categoryRepository.findAll(pageable).stream()
            .map(CategoryMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(long catId) {
        log.info("[public] : Получение категории по id: {}", catId);
        return CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(
            () -> new NotFoundException(String.format(String.format("Category with id=%d was not found", catId)))));
    }
}
