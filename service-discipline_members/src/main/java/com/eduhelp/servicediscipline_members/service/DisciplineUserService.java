package com.eduhelp.servicediscipline_members.service;



import com.eduhelp.servicediscipline_members.repo.DisciplineUserRepo;
import com.eduhelp.servicediscipline_members.repo.model.DisciplineUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public final class DisciplineUserService {

    private final DisciplineUserRepo disciplineUserRepo;

    public List<DisciplineUser> fetchAll(){
        return disciplineUserRepo.findAll();
    }

//    public List<DisciplineUser> fetchByUserId(long user_id){
//        return disciplineUserRepo.findByUser_id(user_id);
//    }

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


