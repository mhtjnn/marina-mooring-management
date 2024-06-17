package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.model.dto.WorkOrderStatusDto;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.Estimate;
import com.marinamooringmanagement.model.entity.WorkOrderStatus;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.EstimateRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.MooringRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.EstimateRepository;
import com.marinamooringmanagement.repositories.WorkOrderStatusRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.service.EstimateService;
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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstimateServiceImpl implements EstimateService {

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private SortUtils sortUtils;

    @Autowired
    private EstimateRepository estimateRepository;

    @Autowired
    private EstimateMapper workOrderMapper;

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private BoatyardMapper boatyardMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkOrderStatusRepository workOrderStatusRepository;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkOrderStatusMapper workOrderStatusMapper;

    private static final Logger log = LoggerFactory.getLogger(EstimateServiceImpl.class);

    @Override
    public BasicRestResponse fetchEstimates(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            Specification<Estimate> spec = new Specification<Estimate>() {
                @Override
                public Predicate toPredicate(Root<Estimate> workOrder, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText) {
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(workOrder.get("problem")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(workOrder.join("mooring").join("customer").get("firstName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(workOrder.join("mooring").join("customer").get("lastName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(workOrder.join("mooring").get("mooringId")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(workOrder.join("mooring").join("boatyard").get("boatyardName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(workOrder.join("technicianUser").get("name")), lowerCaseSearchText)
                        ));
                    }

                    predicates.add(authorizationUtil.fetchPredicateForEstimate(customerOwnerId, workOrder, criteriaBuilder));

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            final Page<Estimate> estimateList = estimateRepository.findAll(spec, pageable);

            final List<EstimateResponseDto> workOrderResponseDtoList = estimateList
                    .getContent()
                    .stream()
                    .map(workOrder -> {
                        EstimateResponseDto workOrderResponseDto = workOrderMapper.mapToEstimateResponseDto(EstimateResponseDto.builder().build(), workOrder);
                        if(null != workOrder.getMooring()) workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer()) workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard()) workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                        if(null != workOrder.getCustomerOwnerUser()) workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                        if(null != workOrder.getTechnicianUser()) workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                        if(null != workOrder.getWorkOrderStatus()) workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                        if(null != workOrder.getDueDate()) {
                            LocalDate dueDate = workOrder.getDueDate()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String dueDateStr = dueDate.format(dateTimeFormatter);
                            workOrderResponseDto.setDueDate(dueDateStr);
                        }

                        if(null != workOrder.getScheduledDate()) {
                            LocalDate scheduledDate = workOrder.getScheduledDate()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String scheduleDateStr = scheduledDate.format(dateTimeFormatter);
                            workOrderResponseDto.setScheduledDate(scheduleDateStr);
                        }

                        return workOrderResponseDto;
                    })
                    .collect(Collectors.toList());

            response.setMessage("All estimates fetched successfully.");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderResponseDtoList);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse saveEstimate(EstimateRequestDto workOrderRequestDto, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to save the estimate in the database");
            final Estimate workOrder = Estimate.builder().build();

            if(null == workOrderRequestDto.getTechnicianId()) throw new RuntimeException("Technician Id cannot be null");
            if(null == workOrderRequestDto.getMooringId()) throw new RuntimeException("Mooring Id cannot be null");

            performSave(workOrderRequestDto, workOrder, null, request);

            response.setMessage("Estimate saved successfully.");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("Error occurred while saving the estimate in the database {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse updateEstimate(EstimateRequestDto workOrderRequestDto, Integer estimateId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to update the estimate with the given mooring ID");
            if (estimateId == null) {
                throw new IllegalArgumentException("Estimate Id not provided for update request");
            }
            Optional<Estimate> optionalEstimate = estimateRepository.findById(estimateId);
            final Estimate workOrder = optionalEstimate.orElseThrow(() -> new ResourceNotFoundException(String.format("Estimate not found with id: %1$s", estimateId)));
            performSave(workOrderRequestDto, workOrder, estimateId, request);
            response.setMessage("Estimate updated successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while updating estimate {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse deleteEstimate(final Integer estimateId, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            if(-1 == customerOwnerId && null != request.getAttribute("CUSTOMER_OWNER_ID")) customerOwnerId = (Integer) request.getAttribute("CUSTOMER_OWNER_ID");

            Optional<Estimate> optionalEstimate = estimateRepository.findById(estimateId);
            if (optionalEstimate.isEmpty()) throw new RuntimeException(String.format("No estimate exists with %1$s", estimateId));
            final Estimate savedEstimate = optionalEstimate.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(null != savedEstimate.getCustomerOwnerUser()) {
                if(!savedEstimate.getCustomerOwnerUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Estimate with the id: %1$s is associated with some other customer owner", estimateId));
            } else {
                throw new RuntimeException(String.format("Estimate with the id: %1$s is not associated with any customer owner", estimateId));
            }

            estimateRepository.delete(savedEstimate);

            Optional<Estimate> optionalEstimateAfterDeletion = estimateRepository.findById(estimateId);

            final String message = optionalEstimateAfterDeletion.isEmpty() ? String.format("Estimate with id %1$s deleted successfully", estimateId) : String.format("Failed to delete estimate with the given id %1$s", estimateId);
            response.setMessage(message);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while deleting estimate with id " + estimateId, e);
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private void performSave(final EstimateRequestDto estimateRequestDto, final Estimate estimate, final Integer estimateId, final HttpServletRequest request) {
        try {
            if(null == estimateId) estimate.setLastModifiedDate(new Date(System.currentTimeMillis()));

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            estimate.setCustomerOwnerUser(user);

            workOrderMapper.mapToEstimate(estimate, estimateRequestDto);

            if(null != estimateRequestDto.getDueDate()) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate localDate = LocalDate.parse(estimateRequestDto.getDueDate(), dateTimeFormatter);
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                estimate.setDueDate(date);
            } else {
                if(estimateId == null) throw new RuntimeException(String.format("Due date cannot be null"));
            }

            if(null != estimateRequestDto.getScheduledDate()) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate localDate = LocalDate.parse(estimateRequestDto.getScheduledDate(), dateTimeFormatter);
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                estimate.setScheduledDate(date);
            } else {
                if(estimateId == null) throw new RuntimeException(String.format("Due date cannot be null"));
            }

            if(null != estimateRequestDto.getWorkOrderStatusId()) {
                final Optional<WorkOrderStatus> optionalWorkOrderStatus = workOrderStatusRepository.findById(estimateRequestDto.getWorkOrderStatusId());
                if(optionalWorkOrderStatus.isEmpty()) throw new RuntimeException(String.format("No work order status found with the given id: %1$s", estimateRequestDto.getWorkOrderStatusId()));

                final WorkOrderStatus workOrderStatus = optionalWorkOrderStatus.get();
                estimate.setWorkOrderStatus(workOrderStatus);
            } else {
                if(null == estimateId) throw new RuntimeException(String.format("While saving work order status cannot be null"));
            }

            if(null != estimateRequestDto.getMooringId()) {
                Optional<Mooring> optionalMooring = mooringRepository.findById(estimateRequestDto.getMooringId());
                if(optionalMooring.isEmpty()) throw new RuntimeException(String.format("No mooring found with the given id: %1$s", estimateRequestDto.getMooringId()));

                final Mooring mooring = optionalMooring.get();

                if(null == estimateRequestDto.getCustomerId()) throw new RuntimeException("Customer Id cannot be null during saving/updating work order");
                if(null == estimateRequestDto.getBoatyardId()) throw new RuntimeException("Boatyard Customer Id cannot be null during saving/updating work order");
                if(null == mooring.getCustomer()) throw new RuntimeException(String.format("No customer found for the mooring with the id: %1$s", estimateRequestDto.getMooringId()));
                if(null == mooring.getBoatyard()) throw new RuntimeException(String.format("No boatyard found for the mooring with the id: %1$s", estimateRequestDto.getBoatyardId()));
                if(!mooring.getCustomer().getId().equals(estimateRequestDto.getCustomerId())) throw new RuntimeException(String.format("Customer Id is different in mooring with given id: %1$s", mooring.getId()));
                if(!mooring.getBoatyard().getId().equals(estimateRequestDto.getBoatyardId())) throw new RuntimeException(String.format("Boatyard Id is different in mooring with given id: %1$s", mooring.getId()));

                estimate.setMooring(mooring);
            } else {
                if(null == estimateId) throw new RuntimeException(String.format("While saving work order mooring id cannot be null"));
            }

            if(null != estimateRequestDto.getTechnicianId()) {
                Optional<User> optionalTechnician = userRepository.findById(estimateRequestDto.getTechnicianId());
                if(optionalTechnician.isEmpty()) throw new RuntimeException(String.format("No technician found with the given id: %1$s", estimateRequestDto.getTechnicianId()));

                final User technician = optionalTechnician.get();

                if(null == technician.getRole()) throw new RuntimeException(String.format("User with id: %1$s is not assigned to any role", technician.getId()));

                if(!technician.getRole().getName().equals(AppConstants.Role.TECHNICIAN)) throw new RuntimeException(String.format("User with the id: %1$s is not of technician role", technician.getId()));

                estimate.setTechnicianUser(technician);
            } else {
                if(null == estimateId) throw new RuntimeException(String.format("Technician Id cannot be null during saving/updating work order"));
            }

            estimateRepository.save(estimate);
        } catch (Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException(e.getMessage(), e);
        }
    }

}