package com.eduhelp.servicediscipline_members.repo;

import com.eduhelp.servicediscipline_members.repo.model.DisciplineUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisciplineUserRepo extends JpaRepository<DisciplineUser, Long> {
   //    List<DisciplineUser> findByUser_id(long user_id);
}
