package com.eduhelp.servicedisciplines.api;


import com.eduhelp.servicedisciplines.api.dto.Discipline;
import com.eduhelp.servicedisciplines.service.DisciplineService ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import java.net.URI;
import java.util.List;
import org.json.JSONArray;


@RequiredArgsConstructor
@RestController
@RequestMapping("/disciplines")
public final class DisciplineController {


    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    private String getDisciplineUsers(final long discipline_id) {
        ResponseEntity<String> disciplineInfo = null;
        try {
            disciplineInfo = restTemplate.exchange(
                    String.format("http://localhost:8083/disciplines_users?discipline_id=%d", discipline_id),
                    HttpMethod.GET,
                    null,
                    String.class);
            return disciplineInfo.getBody();

        } catch (final HttpClientErrorException.NotFound e) { return null;}}


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
            String userDisciplines = getDisciplineUsers(id);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", discipline.getId());
            jsonObject.put("title", discipline.getTitle());
            jsonObject.put("description", discipline.getDescription());
            jsonObject.put("users", new JSONArray(userDisciplines));

            return new ResponseEntity(jsonObject.toString(), HttpStatus.OK);
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
            String disciplineUsers = getDisciplineUsers(id);
            JSONArray array = new JSONArray(disciplineUsers);
            for (int i =0;i < array.length(); i++ ){
                int relation_id = Integer.parseInt((array.getJSONObject(i).get("id")).toString());
                restTemplate.exchange(
                        String.format("http://localhost:8083/disciplines_users/%d", relation_id),
                        HttpMethod.DELETE,
                        null,
                        String.class);
            }
            disciplineService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (HttpClientErrorException e){
            return new ResponseEntity("No discipline with such id", HttpStatus.BAD_REQUEST);
        }
    }
}
