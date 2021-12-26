package com.eduhelp.serviceusers.api;

import com.eduhelp.serviceusers.service.UserService ;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public final class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<com.eduhelp.serviceusers.repo.model.User>> index() {
        final List<com.eduhelp.serviceusers.repo.model.User> users = userService.fetchAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("user_disciplines/{id}")
    public ResponseEntity<JSONObject> show_disciplines(@PathVariable long id){
        try {
            final com.eduhelp.serviceusers.repo.model.User user = userService.fetchById(id);
            JSONObject data = userService.getJsonRepresentation(user);
            return new ResponseEntity(data.toString(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.eduhelp.serviceusers.repo.model.User> show(@PathVariable long id){
        try {
            final com.eduhelp.serviceusers.repo.model.User user = userService.fetchById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody com.eduhelp.serviceusers.api.dto.User user){
        final String username = user.getUsername();
        final String name = user.getName();
        final String surname = user.getSurname();
        final long id = userService.create(username, name, surname);
        final String location = String.format("/users/%d", id);

        return ResponseEntity.created(URI.create(location)).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody com.eduhelp.serviceusers.api.dto.User user) {
        final String username = user.getUsername();
        final String name = user.getName();
        final String surname = user.getSurname();

        try {
            userService.update(id, username, name, surname);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e){
            return new ResponseEntity("No user with such id", HttpStatus.BAD_REQUEST);
        }
    }
}
