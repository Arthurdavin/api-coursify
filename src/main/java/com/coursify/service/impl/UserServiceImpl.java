package com.coursify.service.impl;

import com.coursify.domain.Role;
import com.coursify.domain.User;
import com.coursify.dto.request.UserUpdateRequest;
import com.coursify.dto.response.UserResponse;
import com.coursify.exception.ResourceNotFoundException;
import com.coursify.repository.UserRepository;
import com.coursify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getUserById(Long id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id)));
    }

    @Override
    public UserResponse getCurrentUser(Long id) {
        return getUserById(id);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(String role) {
        Role r = Role.valueOf(role.toUpperCase());
        return userRepository.findAllByRole(r).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User", id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // update fields if not null
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getImageUrl() != null) user.setImageUrl(request.getImageUrl());
        if (request.getGender() != null) user.setGender(request.getGender());

        return toResponse(userRepository.save(user));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(), u.getUsername(), u.getEmail(),
                u.getFirstName(), u.getLastName(), u.getGender(),
                u.getRole(), u.getImageUrl(), u.getCreatedAt()
        );
    }
}
