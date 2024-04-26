package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Role}
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String roleName);
}