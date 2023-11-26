package ru.practicum.main.compilation.mapper;

import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.mapper.EventMapper;

import java.util.Collections;

public class CompilationMapper {
    public static Compilation toModel(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
            .events(Collections.emptyList())
            .pinned(newCompilationDto.isPinned())
            .title(newCompilationDto.getTitle())
            .build();
    }

    public static CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
            .id(compilation.getId())
            .events(EventMapper.toShortListDto((compilation.getEvents())))
            .pinned(compilation.getPinned())
            .title(compilation.getTitle())
            .build();
    }
}
