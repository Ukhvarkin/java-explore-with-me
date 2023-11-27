package ru.practicum.main.user.mapper;

import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.model.User;


public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
            .email(user.getEmail())
            .id(user.getId())
            .name(user.getName())
            .build();
    }

    public static UserShortDto toShortDto(User user) {
        return UserShortDto.builder()
            .id(user.getId())
            .name(user.getName())
            .build();
    }

    public static User toModel(NewUserRequest newUserRequest) {
        return User.builder()
            .name(newUserRequest.getName())
            .email(newUserRequest.getEmail())
            .build();
    }

}
