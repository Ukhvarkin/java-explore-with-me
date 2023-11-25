package ru.practicum.main.compilation.service;

import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addByAdmin(NewCompilationDto newCompilationDto);

    void deleteByAdmin(Long compId);

    CompilationDto updateByAdmin(Long compId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> getByPublic(Boolean pinned, int from, int size);

    CompilationDto getByIdPublic(Long compId);
}
