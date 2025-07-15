package com.cargo.services;

import com.cargo.models.dtos.UserDto;
import com.cargo.models.dtos.UserRegisterRequestDto;
import com.cargo.models.entities.Role;
import com.cargo.models.entities.User;
import com.cargo.models.mappers.UserMapper;
import com.cargo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND = "Kullanıcı bulunamadı: %s";
    private static final String EMAIL_ALREADY_EXISTS = "Bu e-posta zaten kayıtlı: %s";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return (UserDetails) userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    @Transactional
    public UserDto register(UserRegisterRequestDto dto) {
        validateNewUser(dto.getEmail());
        User user = createUserFromRegisterDto(dto); // dto → entity
        User savedUser = userRepository.save(user); // entity persisted
        return userMapper.toDto(savedUser);         // entity → dto
    }

    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto) // Optional<User> → Optional<UserDto>
                .orElseThrow(() -> new NoSuchElementException("Kullanıcı bulunamadı: " + id));
    }

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }


    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, id));
        }
        userRepository.deleteById(id);
    }

    public List<UserDto> getByRole(Role role) {
        return userMapper.toDtoList(userRepository.findByRole(role));
    }

    private void validateNewUser(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(String.format(EMAIL_ALREADY_EXISTS, email));
        }
    }

    private User createUserFromRegisterDto(UserRegisterRequestDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.CUSTOMER)  // Sabit olarak CUSTOMER atanıyor, gerekirse parametre ekle
                .enabled(true)
                .build();
    }

    /**
     * Belirli bir ID'ye sahip kullanıcı entity'sini döndürür.
     * @param id Kullanıcı ID'si
     * @return Kullanıcı entity'sini içeren Optional<User>
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}