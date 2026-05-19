package com.volodymyrchikh.abitandstudhelp.controller;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.RegisterRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UpdateUserRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UserResponse;
import com.volodymyrchikh.abitandstudhelp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public User create(@RequestBody @Valid RegisterRequest request) {
        return userService.save(request);
    }

    @PreAuthorize("@authorizationService.isCurrentUserOrAdmin(#id)")
    @GetMapping("/{id}")
    public UserResponse getById(@P("id") @PathVariable Long id) {
        return userService.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<UserResponse> getAll(@PageableDefault Pageable pageable,
                                     @QuerydslPredicate(root = User.class) Predicate filter) {
        return userService.getAll(pageable, filter);
    }

    @PreAuthorize("@authorizationService.isCurrentUserOrAdmin(#id)")
    @PutMapping("/{id}")
    public UserResponse update(@P("id") @PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        userService.delete(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/me")
    public UserResponse getAuthenticatedUser() {
        SecurityContextHolder.getContext().getAuthentication();
        return userService.getAuthenticatedUser();
    }

    @PreAuthorize("@authorizationService.isCurrentUserOrAdmin(#id)")
    @PostMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse uploadAvatar(@P("id") @PathVariable Long id,
                                     @RequestParam("avatar") MultipartFile avatar) throws java.io.IOException {
        return userService.uploadUserAvatar(id, avatar);
    }

    @PreAuthorize("@authorizationService.isCurrentUserOrAdmin(#id)")
    @DeleteMapping("/{id}/avatar")
    public UserResponse deleteAvatar(@P("id") @PathVariable Long id) {
        return userService.deleteUserAvatar(id);
    }
}
