package com.marinamooringmanagement.repositories.QBO;

import com.marinamooringmanagement.model.entity.QBO.QBOUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QBOUserRepository extends JpaRepository<QBOUser, Integer> {
    Optional<QBOUser> findQBOUserByEmail(String email);

    Optional<QBOUser> findQBOUserByCreatedBy(String email);
}
