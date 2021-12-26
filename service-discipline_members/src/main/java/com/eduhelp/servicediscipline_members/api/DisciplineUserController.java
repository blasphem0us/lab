package com.eduhelp.servicediscipline_members.api;

import com.eduhelp.servicediscipline_members.api.dto.DisciplineUser;
import com.eduhelp.servicediscipline_members.service.DisciplineUserService ;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;


@RequiredArgsConstructor
@RestController
@RequestMapping("/disciplines_users")
public final class DisciplineUserController {

    private final DisciplineUserService disciplineUserService;

    private String getUser(final long user_id) {
        ResponseEntity<String> userInfo = null;
        try {
            userInfo = restTemplate.exchange(
                    String.format("http://localhost:8080/users/%d", user_id),
                    HttpMethod.GET,
                    null,
                    String.class);
            return userInfo.getBody();

        } catch (final HttpClientErrorException.NotFound e) { return null;}
    }

    private String getDiscipline(final long discipline_id) {
        ResponseEntity<String> disciplineInfo = null;
        try {
            disciplineInfo = restTemplate.exchange(
                    String.format("http://localhost:8084/disciplines/%d", discipline_id),
                    HttpMethod.GET,
                    null,
                    String.class);
            return disciplineInfo.getBody();

        } catch (final HttpClientErrorException.NotFound e) { return null;}
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<JSONArray> index(@RequestParam Optional<Long> user_id, Optional<Long> discipline_id) {
        if (user_id.isPresent() && getUser(user_id.get()) == null) {
            return new ResponseEntity ( "No user with such id", HttpStatus.BAD_REQUEST);}

        if (discipline_id.isPresent() && getDiscipline(discipline_id.get()) == null) {
            return new ResponseEntity("No discipline with such id", HttpStatus.BAD_REQUEST);
        }
        final List<com.eduhelp.servicediscipline_members.repo.model.DisciplineUser> disciplines_users = disciplineUserService.fetchAll();
        List<com.eduhelp.servicediscipline_members.repo.model.DisciplineUser> filteredRelations =  new ArrayList();

        if (user_id.isPresent() || discipline_id.isPresent()) {
            for (com.eduhelp.servicediscipline_members.repo.model.DisciplineUser disciplineUser : disciplines_users) {
                if (user_id.isPresent() && user_id.get() != disciplineUser.getUser_id()) {
                    continue;
                }

                if (discipline_id.isPresent() && discipline_id.get() != disciplineUser.getDiscipline_id()) {
                    continue;
                }

                filteredRelations.add(disciplineUser);
            }
        } else {
            filteredRelations = disciplines_users;
        }
        
        JSONArray responseBody = new JSONArray();
        for (com.eduhelp.servicediscipline_members.repo.model.DisciplineUser disciplineUser : filteredRelations) {
            JSONObject entry = new JSONObject();
            entry.put("id", disciplineUser.getId());
            entry.put("user", new JSONObject(getUser(disciplineUser.getUser_id())));
            entry.put("discipline", new JSONObject( getDiscipline(disciplineUser.getDiscipline_id())));
            responseBody.put(entry);
        }

        return new ResponseEntity (responseBody.toList(), HttpStatus.OK);
    }



    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> show(@PathVariable long id){
        try {
            final com.eduhelp.servicediscipline_members.repo.model.DisciplineUser disciplineUser = disciplineUserService.fetchById(id);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", disciplineUser.getId());
            jsonObject.put("user", new JSONObject(getUser(disciplineUser.getUser_id())));
            jsonObject.put("discipline", new JSONObject( getDiscipline(disciplineUser.getDiscipline_id())));
            return new ResponseEntity(jsonObject.toString(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<JSONObject> create(@RequestBody DisciplineUser disciplineUser){
        final long user_id = disciplineUser.getUser_id();
        JSONObject jsonObject = new JSONObject();
        var userInfo = getUser(disciplineUser.getUser_id());
        if (userInfo == null) {
            return new ResponseEntity ( "No user with such id",HttpStatus.BAD_REQUEST);}
        jsonObject.put("user", new JSONObject(userInfo));
        final long discipline_id = disciplineUser.getDiscipline_id();
        var disciplineInfo = getDiscipline(disciplineUser.getDiscipline_id());
        if (disciplineInfo == null) {
            return new ResponseEntity ( "No discipline with such id",HttpStatus.BAD_REQUEST);
        }
        jsonObject.put("discipline", new JSONObject(disciplineInfo));
        try{
            final long id = disciplineUserService.create(user_id, discipline_id);
            jsonObject.put("id", id);
            return new ResponseEntity(jsonObject.toString(), HttpStatus.CREATED);
        } catch (Exception e)
        {
            return new ResponseEntity ("User with such discipline already exist",HttpStatus.BAD_REQUEST);
        }


    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        try {
            disciplineUserService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }
}