package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.QuickbookCustomerMapper;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.entity.QuickbookCustomer;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.QuickbookCustomerRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.QuickbookCustomerResponseDto;
import com.marinamooringmanagement.repositories.QuickbookCustomerRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.service.QuickbookCustomerService;
import com.marinamooringmanagement.utils.ConversionUtils;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class QuickbookCustomerServiceImpl implements QuickbookCustomerService {

    @Autowired
    private ConversionUtils conversionUtils;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private SortUtils sortUtils;

    @Autowired
    private QuickbookCustomerRepository quickbookCustomerRepository;

    @Autowired
    private QuickbookCustomerMapper quickbookCustomerMapper;

    private static final Logger log = LoggerFactory.getLogger(QuickbookCustomerServiceImpl.class);

    @Override
    public BasicRestResponse fetchQuickbookCustomers(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {
        final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            Specification<QuickbookCustomer> spec = new Specification<QuickbookCustomer>() {
                @Override
                public Predicate toPredicate(Root<QuickbookCustomer> quickbookCustomer, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                    List<Predicate> predicates = new ArrayList<>();

                    /*
                     * If the search text is Integer then we are checking if it matches any ID or email(since email consists of numerical values)
                     * else we are checking name, email and phone number(saved as string).
                     */
                    if (null != searchText) {
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(quickbookCustomer.get("customerName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(quickbookCustomer.get("customerId")), lowerCaseSearchText)
                        ));

                    }

                    /*
                     * If the logged-in quickbookCustomer is of ADMINISTRATOR role then if the customerAdminId is not provided then
                     * it will add those quickBookCustomers with CUSTOMER_OWNER role.
                     * and if customerAdminId is provided then it will add those quickBookCustomers which are of TECHNICIAN
                     * and FINANCE role having customerAdminId as given customerAdminId.
                     *
                     * If the logged-in quickbookCustomer if of role as CUSTOMER_OWNER then
                     * it will add those quickBookCustomers which are of TECHNICIAN and FINANCE role having customerAdminId
                     * as logged-in quickbookCustomer ID.
                     */
                    predicates.add(authorizationUtil.fetchPredicate(customerOwnerId, quickbookCustomer, criteriaBuilder));

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );

            // Fetching the roles based on the specifications.
            Page<QuickbookCustomer> filteredQuickbookCustomer = quickbookCustomerRepository.findAll(spec, p);
            response.setTotalSize(quickbookCustomerRepository.findAll(spec).size());

            // Convert the filtered quickBookCustomers to QuickbookCustomerResponseDto
            List<QuickbookCustomerResponseDto> filteredQuickbookCustomersResponseDtoList = filteredQuickbookCustomer
                    .getContent()
                    .stream()
                    .map(quickbookCustomer -> {
                        QuickbookCustomerResponseDto quickbookCustomerResponseDto = QuickbookCustomerResponseDto.builder().build();
                        quickbookCustomerMapper.mapToResponseDto(quickbookCustomerResponseDto, quickbookCustomer);
                        if (null != quickbookCustomer.getUser()) quickbookCustomerResponseDto.setUserId(quickbookCustomer.getUser().getId());
                        return quickbookCustomerResponseDto;
                    }).toList();


            response.setContent(filteredQuickbookCustomersResponseDtoList);
            response.setMessage("Quickbook customers fetched successfully");
            response.setStatus(HttpStatus.OK.value());
            if (filteredQuickbookCustomersResponseDtoList.isEmpty()) response.setCurrentSize(0);
            else response.setCurrentSize(filteredQuickbookCustomersResponseDtoList.size());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    @Override
    public BasicRestResponse saveQuickbookCustomer(QuickbookCustomerRequestDto quickbookCustomerRequestDto, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            final QuickbookCustomer quickbookCustomer = QuickbookCustomer.builder().build();

            log.info(String.format("saving quickbook customer in DB"));

            /*
             * Calling the function which saves user to database with modification if required.
             */
            performSave(quickbookCustomerRequestDto, quickbookCustomer, null, request);

            response.setMessage("Quickbook customer saved successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse updateQuickbookCustomer(QuickbookCustomerRequestDto quickbookCustomerRequestDto, Integer quickbookCustomerId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            //Fetching the optional of user from the database
            Optional<QuickbookCustomer> optionalQuickbookCustomer = quickbookCustomerRepository.findById(quickbookCustomerId);

            if (optionalQuickbookCustomer.isPresent()) {
                // Getting the user
                final QuickbookCustomer quickbookCustomer = optionalQuickbookCustomer.get();

                log.info(String.format("update user"));

                //calling the performSave() function to update the changes and save the user.
                performSave(quickbookCustomerRequestDto, quickbookCustomer, quickbookCustomerId, request);
                response.setMessage("Quickbook Customer updated successfully!!!");
                response.setStatus(HttpStatus.OK.value());
            } else {
                // This will throw error as user(to be updated) doesn't exist in the database.
                throw new DBOperationException(String.format("No Quickbook Customer found with given id: %1$s", quickbookCustomerId));
            }
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse deleteQuickbookCustomer(Integer quickbookCustomerId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            if (-1 == customerOwnerId && null != request.getAttribute("CUSTOMER_OWNER_ID"))
                customerOwnerId = (Integer) request.getAttribute("CUSTOMER_OWNER_ID");

            Optional<QuickbookCustomer> optionalQuickbookCustomer = quickbookCustomerRepository.findById(quickbookCustomerId);
            if (optionalQuickbookCustomer.isEmpty())
                throw new ResourceNotFoundException(String.format("No quickbook customer found with the given id: %1$s", quickbookCustomerId));
            QuickbookCustomer quickbookCustomer = optionalQuickbookCustomer.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if (null != quickbookCustomer.getUser()) {
                if (!quickbookCustomer.getUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("Quickbook customer with the id: %1$s is associated with some other user", quickbookCustomerId));
            } else {
                throw new RuntimeException(String.format("Quickbook customer with the id: %1$s is not associated with any User", quickbookCustomerId));
            }

            quickbookCustomerRepository.deleteById(quickbookCustomerId);

            response.setMessage(String.format("Quickbook customer with ID %d deleted successfully", quickbookCustomerId));
            response.setStatus(HttpStatus.OK.value());

            log.info(String.format("Quickbook customer with ID %d deleted successfully", quickbookCustomerId));

        } catch (Exception e) {
            log.error(String.format("Error occurred while deleting Quickbook customer with ID %d", quickbookCustomerId));

            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return response;
    }

    private void performSave(QuickbookCustomerRequestDto quickbookCustomerRequestDto, QuickbookCustomer quickbookCustomer, Integer quickbookCustomerId, HttpServletRequest request) {
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(null == quickbookCustomerId) quickbookCustomer.setCreationDate(new Date(System.currentTimeMillis()));
            quickbookCustomer.setLastModifiedDate(new Date(System.currentTimeMillis()));

            quickbookCustomerMapper.mapToEntity(quickbookCustomer, quickbookCustomerRequestDto);

            if(null != quickbookCustomerRequestDto.getQuickbookCustomerId()) {
                final Optional<QuickbookCustomer> optionalQuickbookCustomer= quickbookCustomerRepository.findByQuickbookCustomerId(quickbookCustomerRequestDto.getQuickbookCustomerId());

                if(null != quickbookCustomerId) {
                    if(optionalQuickbookCustomer.isPresent() && ObjectUtils.nullSafeEquals(optionalQuickbookCustomer.get().getId(), quickbookCustomerId)) throw new RuntimeException(String.format("Given quickbook customer Id already exists for other quickbook customer"));
                } else {
                    throw new RuntimeException(String.format("Given quickbook customer Id already exists for other quickbook customer"));
                }

                quickbookCustomer.setQuickbookCustomerId(quickbookCustomerRequestDto.getQuickbookCustomerId());
            } else {
                if(null == quickbookCustomerId) throw new RuntimeException(String.format("Quickbook customer Id cannot be null during save operation"));
            }

            quickbookCustomer.setUser(user);

            quickbookCustomerRepository.save(quickbookCustomer);

        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
}
