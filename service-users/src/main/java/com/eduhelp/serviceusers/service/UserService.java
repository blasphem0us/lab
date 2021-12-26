package com.eduhelp.serviceusers.service;

import com.eduhelp.serviceusers.repo.UserRepo;
import com.eduhelp.serviceusers.repo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public final class UserService {

    private final UserRepo userRepo;

    public List<User> fetchAll(){
        return userRepo.findAll();
    }

    public User fetchById(long id) throws IllegalArgumentException{
        final Optional<User> maybeUser = userRepo.findById(id);

        if (maybeUser.isEmpty()) throw new IllegalArgumentException("User not found");
        else return maybeUser.get();
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
        userRepo.deleteById(id);
    }

}
