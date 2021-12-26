package com.eduhelp.servicedisciplines.repo;


import com.eduhelp.servicedisciplines.repo.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplineRepo extends JpaRepository<Discipline, Long> {

}
