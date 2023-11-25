package ru.practicum.main.compilation.controller;

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
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto add(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("POST : Создание новой подборки: {}", newCompilationDto);
        return compilationService.addByAdmin(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PositiveOrZero @PathVariable(name = "compId") Long compId) {
        log.info("DELETE : Удаление подборки с id:{}", compId);
        compilationService.deleteByAdmin(compId);
    }

    @PatchMapping("/{compId}")
    CompilationDto update(@PositiveOrZero @PathVariable(name = "compId") Long compId,
                          @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("PATCH : Модификация подборки с id: {}", compId);
        return compilationService.updateByAdmin(compId, updateCompilationRequest);
    }

}
