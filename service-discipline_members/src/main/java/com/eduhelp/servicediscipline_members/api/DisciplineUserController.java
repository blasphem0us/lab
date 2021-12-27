package com.eduhelp.servicediscipline_members.api;

import com.eduhelp.servicediscipline_members.api.dto.DisciplineUser;
import com.eduhelp.servicediscipline_members.service.DisciplineUserService ;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

import org.json.JSONObject;


@RequiredArgsConstructor
@RestController
@RequestMapping("/disciplines_users")
public final class DisciplineUserController {

    private final DisciplineUserService disciplineUserService;


    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<JSONArray> index(@RequestParam Optional<Long> user_id, Optional<Long> discipline_id) {
        if (user_id.isPresent() && disciplineUserService.getUser(user_id.get()) == null) {
            return new ResponseEntity ( "No user with such id", HttpStatus.BAD_REQUEST);}

        if (discipline_id.isPresent() && disciplineUserService.getDiscipline(discipline_id.get()) == null) {
            return new ResponseEntity("No discipline with such id", HttpStatus.BAD_REQUEST);
        }
        var filteredRelations = disciplineUserService.getFilteredRelations(user_id, discipline_id);
        JSONArray responseBody = new JSONArray();
        for (com.eduhelp.servicediscipline_members.repo.model.DisciplineUser disciplineUser : filteredRelations) {
            responseBody.put(disciplineUserService.getJson(disciplineUser));
        }

        return new ResponseEntity (responseBody.toList(), HttpStatus.OK);
    }



    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> show(@PathVariable long id){
        try {
            final com.eduhelp.servicediscipline_members.repo.model.DisciplineUser disciplineUser = disciplineUserService.fetchById(id);
            return new ResponseEntity(disciplineUserService.getJson(disciplineUser).toString(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<JSONObject> create(@RequestBody DisciplineUser disciplineUser){
        final long user_id = disciplineUser.getUser_id();
        JSONObject jsonObject = new JSONObject();
        var userInfo = disciplineUserService.getUser(disciplineUser.getUser_id());
        if (userInfo == null) {
            return new ResponseEntity ( "No user with such id",HttpStatus.BAD_REQUEST);}
        jsonObject.put("user", new JSONObject(userInfo));
        final long discipline_id = disciplineUser.getDiscipline_id();
        var disciplineInfo = disciplineUserService.getDiscipline(disciplineUser.getDiscipline_id());
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