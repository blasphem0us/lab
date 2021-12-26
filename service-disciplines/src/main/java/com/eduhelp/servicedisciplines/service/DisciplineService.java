package com.eduhelp.servicedisciplines.service;


import com.eduhelp.servicedisciplines.repo.DisciplineRepo;
import com.eduhelp.servicedisciplines.repo.model.Discipline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public final class DisciplineService {

    private final DisciplineRepo disciplineRepo;

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
        disciplineRepo.deleteById(id);
    }

}
