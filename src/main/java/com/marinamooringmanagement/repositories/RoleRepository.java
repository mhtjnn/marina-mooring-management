package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Role}.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Find a role entity by its name.
     *
     * @param roleName the name of the role to search for
     * @return an optional containing the role entity corresponding to the given name, or empty if not found
     */
    Optional<Role> findByName(String roleName);
}
