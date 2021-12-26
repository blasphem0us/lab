package com.eduhelp.serviceusers.api;

import com.eduhelp.serviceusers.service.UserService ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public final class UserController {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

   private String getUserDisciplines(final long user_id) {
        ResponseEntity<String> userInfo = null;
        try {
            userInfo = restTemplate.exchange(
                    String.format("http://localhost:8083/disciplines_users?user_id=%d", user_id),
                    HttpMethod.GET,
                    null,
                    String.class);
            return userInfo.getBody();

        } catch (final HttpClientErrorException.NotFound e) { return null;}}

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
            String userDisciplines = getUserDisciplines(id);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", user.getId());
            jsonObject.put("username", user.getUsername());
            jsonObject.put("name", user.getName());
            jsonObject.put("surname", user.getSurname());
            jsonObject.put("disciplines", new JSONArray(userDisciplines));
            return new ResponseEntity(jsonObject.toString(), HttpStatus.OK);
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
            String userDisciplines = getUserDisciplines(id);
            JSONArray array = new JSONArray(userDisciplines);
            for (int i =0;i < array.length(); i++ ){
                int relation_id = Integer.parseInt((array.getJSONObject(i).get("id")).toString());
                restTemplate.exchange(
                        String.format("http://localhost:8083/disciplines_users/%d", relation_id),
                        HttpMethod.DELETE,
                        null,
                        String.class);
            }
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (HttpClientErrorException e){
            return new ResponseEntity("No user with such id", HttpStatus.BAD_REQUEST);
        }
    }
}
