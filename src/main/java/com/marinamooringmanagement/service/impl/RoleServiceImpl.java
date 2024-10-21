package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.mapper.RoleMapper;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.RoleResponseDto;
import com.marinamooringmanagement.repositories.RoleRepository;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.RoleService;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class for Role Service Interface.
 * This class provides implementation for managing role-related operations.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    /**
     * Fetches a list of roles based on the provided search request parameters.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @return a BasicRestResponse containing the results of the role search.
     */
    @Override
    public BasicRestResponse fetchRoles(final BaseSearchRequest baseSearchRequest) {
        final String roleStr = loggedInUserUtil.getLoggedInUserRole();
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );

            final Specification<Role> spec = new Specification<Role>() {
                @Override
                public Predicate toPredicate(Root<Role> role, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (StringUtils.equals(roleStr, AppConstants.Role.CUSTOMER_OWNER)) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.equal(role.get("name"), AppConstants.Role.FINANCE),
                                criteriaBuilder.equal(role.get("name"), AppConstants.Role.TECHNICIAN)
                                )
                        );
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            List<RoleResponseDto> roleResponseDtoList = new ArrayList<>();

            if(!StringUtils.equals(roleStr, AppConstants.Role.FINANCE) && !StringUtils.equals(roleStr, AppConstants.Role.TECHNICIAN)) {
                final Page<Role> roleList = roleRepository.findAll(spec, p);
                roleResponseDtoList = roleList.stream().map(role -> roleMapper.mapToRoleResponseDto(RoleResponseDto.builder().build(), role)).toList();
            }

            response.setMessage("Roles fetched successfully");
            response.setContent(roleResponseDtoList);
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }
}
