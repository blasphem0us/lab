package com.eduhelp.servicedisciplines.api;


import com.eduhelp.servicedisciplines.api.dto.Discipline;
import com.eduhelp.servicedisciplines.service.DisciplineService ;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import org.json.JSONObject;
import java.net.URI;
import java.util.List;



@RequiredArgsConstructor
@RestController
@RequestMapping("/disciplines")
public final class DisciplineController {

    private final DisciplineService disciplineService;

    @GetMapping
    public ResponseEntity<List<com.eduhelp.servicedisciplines.repo.model.Discipline>> index() {
        final List<com.eduhelp.servicedisciplines.repo.model.Discipline> disciplines = disciplineService.fetchAll();
        return ResponseEntity.ok(disciplines);
    }

    @GetMapping("discipline_users/{id}")
    public ResponseEntity<JSONObject> show_users(@PathVariable long id){
        try {
            final com.eduhelp.servicedisciplines.repo.model.Discipline discipline = disciplineService.fetchById(id);
            JSONObject data = disciplineService.getJsonRepresentation(discipline);
            return new ResponseEntity(data.toString(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<com.eduhelp.servicedisciplines.repo.model.Discipline> show(@PathVariable long id){
        try {
            final com.eduhelp.servicedisciplines.repo.model.Discipline discipline = disciplineService.fetchById(id);
            return ResponseEntity.ok(discipline);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Discipline discipline){
        final String title = discipline.getTitle();
        final String description = discipline.getDescription();
        final long id = disciplineService.create(title, description);
        final String location = String.format("/disciplines/%d", id);

        return ResponseEntity.created(URI.create(location)).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody Discipline discipline) {
        final String title = discipline.getTitle();
        final String description = discipline.getDescription();

        try {
            disciplineService.update(id, title, description);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        try {
            disciplineService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (HttpClientErrorException e){
            return new ResponseEntity("No discipline with such id", HttpStatus.BAD_REQUEST);
        }
    }
}
