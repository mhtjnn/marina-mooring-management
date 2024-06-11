package com.marinamooringmanagement.security.util;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.VendorRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorizationUtil {

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    public <T> User checkAuthority(final Integer customerOwnerId) {
        final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();

        User user;
        if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
            user = checksForAdministrator(customerOwnerId);
        } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
            user = checksForCustomerOwner(customerOwnerId);
        } else {
            throw new RuntimeException("Not Authorized");
        }
        return user;
    }

    public void checkAuthorityForUser(final Integer customerOwnerId, final String role) {
        final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();

        if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
            checkForAdministratorUser(customerOwnerId, role);
        } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
            checksForCustomerOwnerUser(customerOwnerId);
        } else {
            throw new RuntimeException("Not Authorized");
        }
    }

    public void checkAuthorityForDeleteUser(final Integer customerOwnerId, final User userToBeDeleted) {
        final Integer loggedInUserID = loggedInUserUtil.getLoggedInUserID();
        final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();

        if(loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
            if(customerOwnerId == -1 && (userToBeDeleted.getRole().getName().equals(AppConstants.Role.FINANCE) || userToBeDeleted.getRole().getName().equals(AppConstants.Role.TECHNICIAN))) throw new RuntimeException("Please select a customer owner");
            else if(customerOwnerId != -1 && !customerOwnerId.equals(userToBeDeleted.getCustomerOwnerId())) throw new RuntimeException("Cannot perform operations on user with different customer owner id");
        } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
            if(customerOwnerId != -1 && !loggedInUserID.equals(customerOwnerId)) throw new RuntimeException("Cannot perform operations on user with different customer owner id");
            if(null == userToBeDeleted.getCustomerOwnerId()) throw new RuntimeException(String.format("Not Authorized to perform operation on user with role as Administrator and Customer owner"));
            if (!userToBeDeleted.getCustomerOwnerId().equals(loggedInUserID))
                throw new RuntimeException("Not authorized to perform operations on user with different customer owner Id");
        } else{
            throw new RuntimeException("Not Authorized");
        }
    }

    public Vendor checkAuthorityForInventory(final Integer vendorId, final HttpServletRequest request) {
        Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

        final User user =  checkAuthority(customerOwnerId);

        Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);
        if(optionalVendor.isEmpty()) throw new ResourceNotFoundException(String.format("No vendor found with the given id: %1$s", vendorId));
        final Vendor vendor = optionalVendor.get();

        if(null == vendor.getUser()) throw new RuntimeException(String.format("Vendor with id: %1$s has no user", vendorId));

        if(!vendor.getUser().equals(user)) throw new RuntimeException(String.format("Vendor with the id: %1$s is associated with some other customer owner", vendorId));

        return vendor;
    }

    public <T> Predicate fetchPredicate(final Integer customerOwnerId, Root<T> root, CriteriaBuilder criteriaBuilder) {
        final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
        final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

        List<Predicate> predicates = new ArrayList<>();
        if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
            checksForAdministrator(customerOwnerId);
            predicates.add(criteriaBuilder.equal(root.join("user").get("id"), customerOwnerId));
        } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
            checksForCustomerOwner(customerOwnerId);
            predicates.add(criteriaBuilder.equal(root.join("user").get("id"), loggedInUserId));
        } else {
            throw new RuntimeException("Not Authorized");
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    public <T> Predicate fetchPredicateForUser(final Integer customerOwnerId, Root<T> user, CriteriaBuilder criteriaBuilder) {
        final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
        final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

        List<Predicate> predicates = new ArrayList<>();
        if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
            if(customerOwnerId == -1) {
                predicates.add(criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.CUSTOMER_OWNER));
            } else {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(user.get("customerOwnerId"), customerOwnerId)));
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.TECHNICIAN),
                        criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.FINANCE)
                ));
            }
        } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
            if (customerOwnerId != -1 && !customerOwnerId.equals(loggedInUserId))
                throw new RuntimeException("Difference in customer owner Id");
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.TECHNICIAN),
                    criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.FINANCE)
            ));
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(user.get("customerOwnerId"), loggedInUserId)));
        } else {
            throw new RuntimeException("Not Authorized");
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private User checksForAdministrator(final Integer customerOwnerId) {
        if (customerOwnerId == -1) throw new RuntimeException("Please select a customer owner");
        Optional<User> optionalUser = userRepository.findById(customerOwnerId);
        if (optionalUser.isEmpty())
            throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
        if (!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER))
            throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", customerOwnerId));
        return optionalUser.get();
    }

    private User checksForCustomerOwner(final Integer customerOwnerId) {
        final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();
        if (customerOwnerId != -1 && !loggedInUserId.equals(customerOwnerId))
            throw new RuntimeException(String.format("Cannot do operations with different customer owner Id"));
        Optional<User> optionalUser = userRepository.findById(loggedInUserId);
        if (optionalUser.isEmpty())
            throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", loggedInUserId));
        return optionalUser.get();
    }

    private void checkForAdministratorUser(final Integer customerOwnerId, final String role) {
        boolean roleTechnicianOrFinance = role.equals(AppConstants.Role.TECHNICIAN)
                || role.equals(AppConstants.Role.FINANCE);
        if(customerOwnerId == -1 && roleTechnicianOrFinance) throw new RuntimeException("Please select a customer owner");
        if(customerOwnerId != -1) {
            Optional<User> optionalUser = userRepository.findById(customerOwnerId);
            if (optionalUser.isEmpty())
                throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
            if (!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER))
                throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", customerOwnerId));
        }
    }

    private void checksForCustomerOwnerUser(final Integer customerOwnerId) {
        final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();
        if (customerOwnerId != -1 && !loggedInUserId.equals(customerOwnerId))
            throw new RuntimeException("Cannot do operations on user with different customer owner Id");
        Optional<User> optionalUser = userRepository.findById(loggedInUserId);
        if (optionalUser.isEmpty())
            throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
    }
}
