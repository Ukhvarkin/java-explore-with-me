package ru.practicum.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.service.CompilationService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    List<CompilationDto> getAll(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("GET : Запрос на получение списка подборок, pinned: {}", pinned);
        return compilationService.getByPublic(pinned, from, size);
    }

    @GetMapping("/{compId}")
    CompilationDto getById(@PositiveOrZero @PathVariable(name = "compId") Long compId) {
        log.info("GET : Запрос на получение подборки c id: {}", compId);
        return compilationService.getByIdPublic(compId);
    }
}
