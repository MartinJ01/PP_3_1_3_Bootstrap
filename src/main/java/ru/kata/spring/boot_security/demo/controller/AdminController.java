package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.naming.AuthenticationException;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String showAdminPanel(Model model, Principal principal) {
        User adminUser = userService.findByUsername(principal.getName());
        model.addAttribute("user", adminUser);
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }

    @GetMapping("/addUser")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getRolesList());
        return "admin";
    }

    @PostMapping("/addUser")
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam("authorities") List<String> values) throws AuthenticationException {
        Set<Role> roleSet = roleService.getSetOfRoles(values);
        user.setRoles(roleSet);
        userService.createUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/editUser")
    public String showEditUserForm(@RequestParam("id") long id, Model model) {
        User user = userService.getUser(id);
        if (user == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getRolesList());
        return "admin";
    }

    @PostMapping("/editUser")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam("authorities") List<String> values) throws AuthenticationException {
        Set<Role> roleSet = roleService.getSetOfRoles(values);
        user.setRoles(roleSet);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
