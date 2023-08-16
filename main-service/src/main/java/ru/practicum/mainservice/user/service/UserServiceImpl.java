package ru.practicum.mainservice.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.Pages;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        User user = userMapper.newUserRequestToUser(newUserRequest);
        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Add user: {}", user.getEmail());
        return userMapper.userToUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        getUserById(userId);
        log.info("Deleted user with id = {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        Pageable page = Pages.getPage(from, size);
        if (ids == null) {
            users = userRepository.findAll(page).toList();
        } else {
            users = userRepository.findAllByIdIn(ids, page);
        }
        log.info("Number of users: {}", users.size());
        return users.stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Category with id={} was not found ", userId);
            throw new NotFoundException("Category with id=" + userId + " was not found ");
        }
        return userMapper.userToUserDto(user.get());
    }
}
