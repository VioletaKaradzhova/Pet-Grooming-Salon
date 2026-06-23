package com.grooming.salon.service;

import com.grooming.salon.exception.BusinessRuleException;
import com.grooming.salon.model.dto.EmployeeDto;
import com.grooming.salon.model.dto.LoginDto;
import com.grooming.salon.model.entity.Role;
import com.grooming.salon.model.entity.User;
import com.grooming.salon.repository.UserRepository;
import com.grooming.salon.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(LoginDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new BusinessRuleException("Username is already taken.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.hashPassword(dto.getPassword()));
        user.setRole(Role.CLIENT);

        return userRepository.save(user);
    }

    public User authenticateUser(LoginDto dto) {
        Optional<User> userOpt = userRepository.findByUsername(dto.getUsername());

        if (userOpt.isEmpty()) {
            throw new BusinessRuleException("Invalid username or password.");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(PasswordUtil.hashPassword(dto.getPassword()))) {
            throw new BusinessRuleException("Invalid username or password.");
        }

        return user;
    }

    public void createEmployee(EmployeeDto dto, String creatorRoleString) {
        Role creatorRole = Role.valueOf(creatorRoleString);

        if (creatorRole == Role.MANAGEMENT && dto.getRole() != Role.STAFF) {
            throw new BusinessRuleException("Management can only create Staff accounts.");
        } else if (creatorRole == Role.STAFF || creatorRole == Role.CLIENT) {
            throw new BusinessRuleException("You do not have permission to create employees.");
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new BusinessRuleException("Username is already taken.");
        }

        User employee = new User();
        employee.setUsername(dto.getUsername());
        employee.setPassword(PasswordUtil.hashPassword(dto.getPassword()));
        employee.setRole(dto.getRole());

        userRepository.save(employee);
    }
}