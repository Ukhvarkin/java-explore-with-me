package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> get(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<UserDto> result;

        if (ids == null) {
            log.info("Получение списка всех пользователей");
            result = userRepository.findAll(pageable).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
        } else {
            log.info("Получение списка пользователей по списку: {}", ids);
            result = userRepository.findAllByIds(ids, pageable).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        log.info("Добавление нового пользователя");
        return UserMapper.toDto(userRepository.save(UserMapper.toModel(newUserRequest)));
    }

    @Override
    public void delete(Long userId) {
        log.info("Удаление пользователя с id: {}", userId);
        userRepository.deleteById(userId);
    }
}
