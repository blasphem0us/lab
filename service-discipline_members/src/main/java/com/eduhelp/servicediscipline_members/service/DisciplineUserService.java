package com.eduhelp.servicediscipline_members.service;



import com.eduhelp.servicediscipline_members.repo.DisciplineUserRepo;
import com.eduhelp.servicediscipline_members.repo.model.DisciplineUser;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public final class DisciplineUserService {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    @Autowired
    private RestTemplate restTemplate;

    public String getUser(final long user_id) {
        ResponseEntity<String> userInfo = null;
        try {
            userInfo = restTemplate.exchange(
                    String.format("http://users:8080/users/%d", user_id),
                    HttpMethod.GET,
                    null,
                    String.class);
            return userInfo.getBody();

        } catch (final HttpClientErrorException.NotFound e) { return null;}
    }

    public String getDiscipline(final long discipline_id) {
        ResponseEntity<String> disciplineInfo = null;
        try {
            disciplineInfo = restTemplate.exchange(
                    String.format("http://disciplines:8084/disciplines/%d", discipline_id),
                    HttpMethod.GET,
                    null,
                    String.class);
            return disciplineInfo.getBody();

        } catch (final HttpClientErrorException.NotFound e) { return null;}
    }

    public List<com.eduhelp.servicediscipline_members.repo.model.DisciplineUser> getFilteredRelations(Optional<Long> user_id, Optional<Long> discipline_id) {
        final List<com.eduhelp.servicediscipline_members.repo.model.DisciplineUser> disciplines_users = fetchAll();
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
            return disciplines_users;
        }
        return filteredRelations;
    }

    public JSONObject getJson(DisciplineUser disciplineUser) {
        JSONObject entry = new JSONObject();
        entry.put("id", disciplineUser.getId());
        entry.put("user", new JSONObject(getUser(disciplineUser.getUser_id())));
        entry.put("discipline", new JSONObject(getDiscipline(disciplineUser.getDiscipline_id())));
        return entry;
    }

    private final DisciplineUserRepo disciplineUserRepo;

    public List<DisciplineUser> fetchAll(){
        return disciplineUserRepo.findAll();
    }

    public List<DisciplineUser> fetchByDisciplineId(){
        return disciplineUserRepo.findAll();
    }

    public DisciplineUser fetchById(long id) throws IllegalArgumentException{
        final Optional<DisciplineUser> maybeDisciplineUser = disciplineUserRepo.findById(id);

        if (maybeDisciplineUser.isEmpty()) throw new IllegalArgumentException("Discipline not found");
        else return maybeDisciplineUser.get();
    }

    public long create(long user_id, long discipline_id){
        final DisciplineUser disciplineUser = new DisciplineUser(user_id, discipline_id);
        final DisciplineUser savedDisciplineUser = disciplineUserRepo.save(disciplineUser);
        return savedDisciplineUser.getId();
    }

    public void delete(long id) throws IllegalArgumentException {
        final Optional<DisciplineUser> maybeDisciplineUser = disciplineUserRepo.findById(id);
        if (maybeDisciplineUser.isEmpty()) throw new IllegalArgumentException("No such discipline found");
        disciplineUserRepo.deleteById(id);
    }

}


