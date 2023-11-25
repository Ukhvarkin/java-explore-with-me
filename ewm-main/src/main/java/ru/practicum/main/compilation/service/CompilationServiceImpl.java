package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.mapper.CompilationMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addByAdmin(NewCompilationDto newCompilationDto) {
        log.info("[admin] : Создание подборки: {}", newCompilationDto);
        Compilation compilation = CompilationMapper.toModel(newCompilationDto);

        if (newCompilationDto.getEvents() != null) {
            List<Event> result = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(result);
        } else {
            compilation.setEvents(Collections.emptyList());
        }

        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteByAdmin(Long compId) {
        compilationRepository.findById(compId).orElseThrow(
            () -> new NotFoundException(String.format("Compilation with id=%d was not found", compId)));
        log.info("[admin] : Удаление подборки с id: {}", compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateByAdmin(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("[admin] : Модификация подборки c id: {}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
            () -> new NotFoundException(String.format("Compilation with id=%d was not found", compId)));

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> result = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(result);
            log.info("[admin] : Модификация событий подборки");
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
            log.info("[admin] : Модификация закрепления подборки");
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
            log.info("[admin] : Модификация заголовка подборки");
        }
        log.info("[admin] : Модификация подборки завершена");
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getByPublic(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        log.info("[public] : Запрос подборки");
        if (pinned != null) {
            log.info("[public] : Подборка с закрепом: {}", pinned);
            return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
        } else {
            log.info("[public] : Подборки без закрепа");
            return compilationRepository.findAll(pageable).stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationDto getByIdPublic(Long compId) {
        log.info("[public] : Запрос подборки c id: {}", compId);
        return CompilationMapper.toDto(compilationRepository.findById(compId).orElseThrow(
            () -> new NotFoundException(String.format("Compilation with id=%d was not found", compId))));
    }
}
