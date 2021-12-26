package com.eduhelp.serviceusers.service;

import com.eduhelp.serviceusers.repo.UserRepo;
import com.eduhelp.serviceusers.repo.model.User;
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
public final class UserService {

    private final UserRepo userRepo;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    public List<User> fetchAll(){
        return userRepo.findAll();
    }

    public User fetchById(long id) throws IllegalArgumentException{
        final Optional<User> maybeUser = userRepo.findById(id);

        if (maybeUser.isEmpty()) throw new IllegalArgumentException("User not found");
        else return maybeUser.get();
    }
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


    public JSONObject getJsonRepresentation(com.eduhelp.serviceusers.repo.model.User user) {
        String userDisciplines = getUserDisciplines(user.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", user.getId());
        jsonObject.put("username", user.getUsername());
        jsonObject.put("name", user.getName());
        jsonObject.put("surname", user.getSurname());
        jsonObject.put("disciplines", new JSONArray(userDisciplines));

        return jsonObject;
    }




    public long create(String username, String name, String surname){
        final User user = new User(username, name, surname);
        final User savedUser = userRepo.save(user);
        return savedUser.getId();
    }

    public void update(long id, String username, String name, String surname) throws IllegalArgumentException{
        final Optional<User> maybeUser = userRepo.findById(id);
        if (maybeUser.isEmpty()) throw new IllegalArgumentException("User not found");

        final User user = maybeUser.get();
        if (username != null && !username.isBlank()) user.setUsername(username);
        if (name != null && !name.isBlank()) user.setName(name);
        if (surname != null && !surname.isBlank()) user.setSurname(surname);
        userRepo.save(user);
    }

    public void delete(long id) throws IllegalArgumentException {
        final Optional<User> maybeUser = userRepo.findById(id);
        if (maybeUser.isEmpty()) throw new IllegalArgumentException("No such user found");

        String userDisciplines = getUserDisciplines(id);
        JSONArray array = new JSONArray(userDisciplines);
        for (int i =0; i < array.length(); i++ ){
            int relation_id = Integer.parseInt((array.getJSONObject(i).get("id")).toString());
            restTemplate.exchange(
                    String.format("http://localhost:8083/disciplines_users/%d", relation_id),
                    HttpMethod.DELETE,
                    null,
                    String.class);
        }
        userRepo.deleteById(id);
    }

}
