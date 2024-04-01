package ru.practicum.shareit.user;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "email", target = "email")
    })
    User toUser(UserCreateDto dto);

    UserDto toUserDto(User user);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "name", target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE),
            @Mapping(source = "email", target = "email", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    })
    User updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "email", target = "email"),
            @Mapping(target = "items", ignore = true) // Игнорируем, так как items не входит в DTO
    })
    User fromUserDto(UserDto dto);
}