package ru.koshkin.PerfTestHelper.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.koshkin.PerfTestHelper.DTO.UserDTO;
import ru.koshkin.PerfTestHelper.Entities.User;
import ru.koshkin.PerfTestHelper.repositories.UserRepo;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    //    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    ;

    @Autowired
    private JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username Not Found"));
    }

    public String getJWTForUser(UserDTO userDTO) throws Exception {
        UserDetails user = loadUserByUsername(userDTO.getUsername());
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Invalid username/password");
        return jwtService.generateToken(user);
    }

    public String saveUserAndGetJWT(UserDTO userDTO) throws Exception {
        Optional<User> userOptional = userRepo.findByUsername(userDTO.getUsername());
        if (userOptional.isPresent()) {
            throw new Exception("User already exists");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user = userRepo.save(user);
        return jwtService.generateToken(user);
    }

    public String getUsernameById(Long id) {
        return userRepo.findById(id).orElseThrow().getUsername();
    }
}
