package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.mapper.metadata.WorkOrderStatusMapper;
import com.marinamooringmanagement.model.dto.metadata.WorkOrderStatusDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderStatus;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.EstimateRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.repositories.metadata.WorkOrderStatusRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.service.EstimateService;
import com.marinamooringmanagement.utils.DateUtil;
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
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private EstimateRepository estimateRepository;

    @Autowired
    private EstimateMapper estimateMapper;

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

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private WorkOrderServiceImpl workOrderServiceImpl;

    private static final Logger log = LoggerFactory.getLogger(EstimateServiceImpl.class);

    @Override
    @Transactional
    public BasicRestResponse fetchEstimates(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            authorizationUtil.checkAuthorityForTechnician(customerOwnerId);

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
                                criteriaBuilder.like(criteriaBuilder.lower(workOrder.join("mooring").get("mooringNumber")), lowerCaseSearchText),
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
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            final Page<Estimate> estimateList = estimateRepository.findAll(spec, pageable);

            final List<EstimateResponseDto> estimateResponseDtoList = estimateList
                    .getContent()
                    .stream()
                    .map(workOrder -> {
                        EstimateResponseDto workOrderResponseDto = estimateMapper.mapToEstimateResponseDto(EstimateResponseDto.builder().build(), workOrder);
                        if(null != workOrder.getMooring()) workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer()) workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard()) workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                        if(null != workOrder.getCustomerOwnerUser()) workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                        if(null != workOrder.getTechnicianUser()) workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                        if(null != workOrder.getWorkOrderStatus()) workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                        if(null != workOrder.getDueDate()) workOrderResponseDto.setDueDate(DateUtil.dateToString(workOrder.getDueDate()));
                        if(null != workOrder.getScheduledDate()) workOrderResponseDto.setScheduledDate(DateUtil.dateToString(workOrder.getScheduledDate()));

                        return workOrderResponseDto;
                    })
                    .collect(Collectors.toList());

            response.setMessage("All estimates fetched successfully.");
            response.setTotalSize(estimateRepository.findAll(spec).size());
            response.setCurrentSize(estimateResponseDtoList.size());
            response.setStatus(HttpStatus.OK.value());
            response.setContent(estimateResponseDtoList);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse saveEstimate(EstimateRequestDto estimateRequestDto, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to save the estimate in the database");
            final Estimate workOrder = Estimate.builder().build();

            if(null == estimateRequestDto.getTechnicianId()) throw new RuntimeException("Technician Id cannot be null");
            if(null == estimateRequestDto.getMooringId()) throw new RuntimeException("Mooring Id cannot be null");

            performSave(estimateRequestDto, workOrder, null, request);

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
    public BasicRestResponse updateEstimate(EstimateRequestDto estimateRequestDto, Integer estimateId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to update the estimate with the given mooring ID");
            if (estimateId == null) {
                throw new IllegalArgumentException("Estimate Id not provided for update request");
            }
            Optional<Estimate> optionalEstimate = estimateRepository.findById(estimateId);
            final Estimate workOrder = optionalEstimate.orElseThrow(() -> new ResourceNotFoundException(String.format("Estimate not found with id: %1$s", estimateId)));
            performSave(estimateRequestDto, workOrder, estimateId, request);
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
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            if(-1 == customerOwnerId && null != request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID)) customerOwnerId = (Integer) request.getAttribute("CUSTOMER_OWNER_ID");

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

    @Override
    public BasicRestResponse convertEstimateToWorkOrder(final Integer id, final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            final Estimate estimate = estimateRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No estimate found with the given id: %1$s", id)));

            final WorkOrder workOrder = estimateMapper.mapToWorkOrder(WorkOrder.builder().build(), estimate);
            workOrder.setCreationDate(new Date(System.currentTimeMillis()));
            workOrder.setLastModifiedDate(new Date(System.currentTimeMillis()));

            workOrder.setWorkOrderNumber(workOrderServiceImpl.createWorkOrderNumber());

            if(null != estimate.getTechnicianUser()) {
                workOrder.setTechnicianUser(estimate.getTechnicianUser());
            } else {
                throw new RuntimeException(String.format("No technician found for this estimate"));
            }

            if(null != estimate.getMooring()) {
                workOrder.setMooring(estimate.getMooring());
            } else {
                throw new RuntimeException(String.format("No mooring found for this estimate"));
            }

            if(null != estimate.getCustomerOwnerUser()) {
                if(!user.getId().equals(estimate.getCustomerOwnerUser().getId())) throw new RuntimeException(String.format("Estimate with the id: %1$s is associated with other customer owner", estimate.getId()));
                workOrder.setCustomerOwnerUser(estimate.getCustomerOwnerUser());
            } else {
                throw new RuntimeException(String.format("No customer owner found for this estimate"));
            }

            if(null != estimate.getWorkOrderStatus()) {
                workOrder.setWorkOrderStatus(estimate.getWorkOrderStatus());
            } else {
                throw new RuntimeException(String.format("No status found for this estimate"));
            }

            Integer estimateId = estimate.getId();
            estimateRepository.delete(estimate);
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

            response.setMessage(String.format("Estimate with the id: %1$s is successfully converted to work order with id: %2$s", estimateId, savedWorkOrder.getId()));
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    private void performSave(final EstimateRequestDto estimateRequestDto, final Estimate estimate, final Integer estimateId, final HttpServletRequest request) {
        try {
            if(null == estimateId) estimate.setLastModifiedDate(new Date(System.currentTimeMillis()));

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            estimate.setCustomerOwnerUser(user);

            estimateMapper.mapToEstimate(estimate, estimateRequestDto);

            final LocalDate currentDate = LocalDate.now();

            if (null == estimateRequestDto.getScheduledDate() && null == estimateRequestDto.getDueDate()) {
                if (null == estimateId)
                    throw new RuntimeException(String.format("Due date and Schedule date cannot be null during save"));
            } else if (null == estimateRequestDto.getDueDate()) {
                if (null == estimateId)
                    throw new RuntimeException(String.format("Due date cannot be null during saved"));
                final Date savedScheduleDate = estimate.getScheduledDate();
                final Date givenScheduleDate = DateUtil.stringToDate(estimateRequestDto.getScheduledDate());

                LocalDate localSavedScheduleDate = savedScheduleDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate localGivenScheduleDate = givenScheduleDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate localSavedDueDate = estimate.getDueDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (localGivenScheduleDate.isBefore(localSavedScheduleDate))
                    throw new RuntimeException(String.format("Given schedule date: %1$s is before saved schedule date: %2$s", localGivenScheduleDate, localSavedScheduleDate));
                if (localGivenScheduleDate.isAfter(localSavedDueDate))
                    throw new RuntimeException(String.format("Given schedule date: %1$s is after saved due date: %2$s", localGivenScheduleDate, localSavedDueDate));

                estimate.setScheduledDate(givenScheduleDate);
            } else if (null == estimateRequestDto.getScheduledDate()) {
                if (null == estimateId)
                    throw new RuntimeException(String.format("Schedule date cannot be null during save"));
                final Date givenDueDate = DateUtil.stringToDate(estimateRequestDto.getDueDate());

                LocalDate localGivenDueDate = givenDueDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                LocalDate localSavedScheduleDate = estimate.getScheduledDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (localGivenDueDate.isBefore(localSavedScheduleDate))
                    throw new RuntimeException(String.format("Given due date: %1$s is before saved schedule date: %2$s", localGivenDueDate, localSavedScheduleDate));
                estimate.setDueDate(givenDueDate);
            } else {

                final Date givenScheduleDate = DateUtil.stringToDate(estimateRequestDto.getScheduledDate());
                final Date givenDueDate = DateUtil.stringToDate(estimateRequestDto.getDueDate());

                if (null == estimateId) {
                    LocalDate localScheduleDate = givenScheduleDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    LocalDate localDueDate = givenDueDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    if (localDueDate.isBefore(currentDate))
                        throw new RuntimeException(String.format("Given due date: %1$s is before current date: %2$s", localDueDate, new Date(System.currentTimeMillis())));
                    if (localScheduleDate.isBefore(currentDate))
                        throw new RuntimeException(String.format("Given schedule date: %1$s is before current date: %2$s", localScheduleDate, new Date(System.currentTimeMillis())));
                    if (localScheduleDate.isAfter(localDueDate))
                        throw new RuntimeException(String.format("Given schedule date: %1$s is after given due date: %2$s", localScheduleDate, localDueDate));
                } else {

                    final Date savedScheduleDate = estimate.getScheduledDate();

                    LocalDate localGivenScheduleDate = givenScheduleDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    LocalDate localGivenDueDate = givenDueDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    LocalDate localSavedScheduleDate = savedScheduleDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    if (localGivenScheduleDate.isBefore(localSavedScheduleDate))
                        throw new RuntimeException(String.format("Given schedule date: %1$s is before saved schedule date: %2$s", localGivenScheduleDate, localSavedScheduleDate));
                    if (localGivenScheduleDate.isAfter(localGivenDueDate))
                        throw new RuntimeException(String.format("Given schedule date: %1$s is after given due date: %2$s", localGivenScheduleDate, localGivenDueDate));
                }

                estimate.setScheduledDate(givenScheduleDate);
                estimate.setDueDate(givenDueDate);
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
