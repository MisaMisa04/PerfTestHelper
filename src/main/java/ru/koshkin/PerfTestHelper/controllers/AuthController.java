package ru.koshkin.PerfTestHelper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.koshkin.PerfTestHelper.DTO.JWTDTO;
import ru.koshkin.PerfTestHelper.DTO.UserDTO;
import ru.koshkin.PerfTestHelper.Entities.User;
import ru.koshkin.PerfTestHelper.services.UserService;

@RestController
@RequestMapping("/api/v1/")
public class AuthController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST, path = "login")
    public JWTDTO login(@RequestBody UserDTO userDTO) throws Exception {
        return new JWTDTO(userService.getJWTForUser(userDTO));
    }

    @RequestMapping(method = RequestMethod.POST, path = "register")
    public JWTDTO registerUser(@RequestBody UserDTO userDTO) throws Exception {
        return new JWTDTO(userService.saveUserAndGetJWT(userDTO));
    }

    @RequestMapping(method = RequestMethod.GET, path = "getUsername")
    public String getUsername() {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return userService.getUsernameById(userId);
    }
}
