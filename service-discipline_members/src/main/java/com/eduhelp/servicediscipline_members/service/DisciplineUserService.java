package com.eduhelp.servicediscipline_members.service;



import com.eduhelp.servicediscipline_members.repo.DisciplineUserRepo;
import com.eduhelp.servicediscipline_members.repo.model.DisciplineUser;
import lombok.RequiredArgsConstructor;
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
public final class DisciplineUserService {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    private String getUser(final long user_id) {
        ResponseEntity<String> userInfo = null;
        try {
            userInfo = restTemplate.exchange(
                    String.format("http://users/users/%d", user_id),
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
                    String.format("http://disciplines/disciplines/%d", discipline_id),
                    HttpMethod.GET,
                    null,
                    String.class);
            return disciplineInfo.getBody();

        } catch (final HttpClientErrorException.NotFound e) { return null;}
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


