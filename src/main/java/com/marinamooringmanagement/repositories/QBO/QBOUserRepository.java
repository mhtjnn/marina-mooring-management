package com.marinamooringmanagement.repositories.QBO;

import com.marinamooringmanagement.model.entity.QBO.QBOUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QBOUserRepository extends JpaRepository<QBOUser, Integer> {
}
