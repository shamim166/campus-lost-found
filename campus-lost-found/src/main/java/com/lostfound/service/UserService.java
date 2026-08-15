package com.lostfound.service;

import com.lostfound.model.Role;
import com.lostfound.model.User;
import com.lostfound.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // Spring Security: Load user by email (used as username)
    // =========================================================
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("No account found with email: " + email));

        Collection<? extends GrantedAuthority> authorities = getAuthorities(user.getRole());

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            authorities
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // =========================================================
    // Registration
    // =========================================================
    public User registerUser(String name, String email, String rawPassword, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account already exists with this email.");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhone(phone);
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    // =========================================================
    // Find current logged-in user from security context
    // =========================================================
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    // =========================================================
    // Admin
    // =========================================================
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // =========================================================
    // Profile Update
    // =========================================================
    public User updateProfile(Long userId, String name, String phone) {
        User user = findById(userId);
        user.setName(name);
        user.setPhone(phone);
        return userRepository.save(user);
    }

    public long countUsers() {
        return userRepository.count();
    }

}
