package com.eduhelp.servicedisciplines.service;


import com.eduhelp.servicedisciplines.repo.DisciplineRepo;
import com.eduhelp.servicedisciplines.repo.model.Discipline;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public final class DisciplineService {

    private final DisciplineRepo disciplineRepo;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;


    public List<Discipline> fetchAll(){
        return disciplineRepo.findAll();
    }

    public Discipline fetchById(long id) throws IllegalArgumentException{
        final Optional<Discipline> maybeDiscipline = disciplineRepo.findById(id);

        if (maybeDiscipline.isEmpty()) throw new IllegalArgumentException("Discipline not found");
        else return maybeDiscipline.get();
    }

    public long create(String title, String description){
        final Discipline discipline = new Discipline(title, description);
        final Discipline savedDiscipline = disciplineRepo.save(discipline);
        return savedDiscipline.getId();
    }

    public void update(long id, String title, String description) throws IllegalArgumentException{
        final Optional<Discipline> maybeDiscipline = disciplineRepo.findById(id);
        if (maybeDiscipline.isEmpty()) throw new IllegalArgumentException("Discipline not found");

        final Discipline discipline = maybeDiscipline.get();
        if (title != null && !title.isBlank()) discipline.setTitle(title);
        if (description != null && !description.isBlank()) discipline.setDescription(description);
        disciplineRepo.save(discipline);
    }

    public void delete(long id) throws IllegalArgumentException {
        final Optional<Discipline> maybeDiscipline = disciplineRepo.findById(id);
        if (maybeDiscipline.isEmpty()) throw new IllegalArgumentException("No such discipline found");

        String disciplineUsers = getDisciplineUsers(id);
        JSONArray array = new JSONArray(disciplineUsers);
        for (int i =0;i < array.length(); i++ ){
            int relation_id = Integer.parseInt((array.getJSONObject(i).get("id")).toString());
            restTemplate.exchange(
                    String.format("http://members:8083/disciplines_users/%d", relation_id),
                    HttpMethod.DELETE,
                    null,
                    String.class);
        }

        disciplineRepo.deleteById(id);
    }

    public JSONObject getJsonRepresentation(com.eduhelp.servicedisciplines.repo.model.Discipline discipline) {
        String disciplineUsers = getDisciplineUsers(discipline.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", discipline.getId());
        jsonObject.put("title", discipline.getTitle());
        jsonObject.put("description", discipline.getDescription());
        jsonObject.put("users", new JSONArray(disciplineUsers));

        return jsonObject;
    }

    private String getDisciplineUsers(final long discipline_id) {
        ResponseEntity<String> disciplineInfo = null;
        try {
            disciplineInfo = restTemplate.exchange(
                    String.format("http://members:8083/disciplines_users?discipline_id=%d", discipline_id),
                    HttpMethod.GET,
                    null,
                    String.class);
            return disciplineInfo.getBody();

        } catch (final HttpClientErrorException.NotFound e) { return null;}}

}
