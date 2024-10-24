package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.MathException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.exception.ResourceNotProvidedException;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.mapper.metadata.WorkOrderStatusMapper;
import com.marinamooringmanagement.model.dto.metadata.WorkOrderStatusDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderStatus;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.EstimateRequestDto;
import com.marinamooringmanagement.model.request.InventoryRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.repositories.metadata.WorkOrderStatusRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.EstimateService;
import com.marinamooringmanagement.utils.DateUtil;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private VendorMapper vendorMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BoatyardRepository boatyardRepository;

    private static final Logger log = LoggerFactory.getLogger(EstimateServiceImpl.class);

    @Override
    @Transactional
    public BasicRestResponse fetchEstimates(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user;

            if(StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.TECHNICIAN)) {
                final User technicianUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No technician user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(technicianUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No customer owner user found with the given id: %1$s", technicianUser.getCustomerOwnerId())));
            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

            Specification<Estimate> spec = new Specification<Estimate>() {
                @Override
                public Predicate toPredicate(Root<Estimate> estimate, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText) {
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";
                        Join<Estimate, Mooring> mooringJoin = estimate.join("mooring", JoinType.LEFT);
                        Join<Mooring, Customer> customerJoin = mooringJoin.join("customer", JoinType.LEFT);
                        Join<Mooring, Boatyard> boatyardJoin = mooringJoin.join("boatyard", JoinType.LEFT);
                        Join<Estimate, User> technicianUserJoin = estimate.join("technicianUser", JoinType.LEFT);
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(estimate.get("problem")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("firstName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("lastName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(mooringJoin.get("mooringNumber")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(boatyardJoin.get("boatyardName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(technicianUserJoin.get("firstName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(technicianUserJoin.get("lastName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.concat(criteriaBuilder.lower(customerJoin.get("firstName")),
                                        criteriaBuilder.concat(" ", criteriaBuilder.lower(customerJoin.get("lastName")))), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.concat(criteriaBuilder.lower(technicianUserJoin.get("firstName")),
                                        criteriaBuilder.concat(" ", criteriaBuilder.lower(technicianUserJoin.get("lastName")))), lowerCaseSearchText)
                        ));
                    }

                    predicates.add(authorizationUtil.fetchPredicateForEstimate(customerOwnerId, estimate, criteriaBuilder));

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
                    .map(estimate -> {
                        EstimateResponseDto estimateResponseDto = estimateMapper.mapToEstimateResponseDto(EstimateResponseDto.builder().build(), estimate);
                        if (null != estimate.getMooring())
                            estimateResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), estimate.getMooring()));
                        if (null != estimate.getCustomer())
                            estimateResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), estimate.getCustomer()));
                        if (null != estimate.getBoatyard())
                            estimateResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), estimate.getBoatyard()));
                        if (null != estimate.getCustomerOwnerUser())
                            estimateResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), estimate.getCustomerOwnerUser()));
                        if (null != estimate.getTechnicianUser())
                            estimateResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), estimate.getTechnicianUser()));
                        if (null != estimate.getWorkOrderStatus())
                            estimateResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), estimate.getWorkOrderStatus()));
                        if (null != estimate.getDueDate())
                            estimateResponseDto.setDueDate(DateUtil.dateToString(estimate.getDueDate()));
                        if (null != estimate.getScheduledDate())
                            estimateResponseDto.setScheduledDate(DateUtil.dateToString(estimate.getScheduledDate()));

                        List<Inventory> inventoryList = inventoryRepository.findInventoriesByEstimate(estimate.getId());

                        if (null != inventoryList && !inventoryList.isEmpty()) {
                            estimateResponseDto.setInventoryResponseDtoList(inventoryList
                                    .stream()
                                    .map(inventory -> {
                                        final InventoryResponseDto inventoryResponseDto = inventoryMapper.mapToInventoryResponseDto(InventoryResponseDto.builder().build(), inventory);
                                        VendorResponseDto vendorResponseDto = vendorMapper.mapToVendorResponseDto(VendorResponseDto.builder().build(), inventory.getVendor());
                                        inventoryResponseDto.setVendorResponseDto(vendorResponseDto);
                                        return inventoryResponseDto;
                                    })
                                    .toList());
                        }

                        return estimateResponseDto;
                    })
                    .toList();

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

            if (null == estimateRequestDto.getCustomerId()) throw new RuntimeException("Customer Id cannot be null");

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
            if (-1 == customerOwnerId && null != request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID))
                customerOwnerId = (Integer) request.getAttribute("CUSTOMER_OWNER_ID");

            Optional<Estimate> optionalEstimate = estimateRepository.findById(estimateId);
            if (optionalEstimate.isEmpty())
                throw new RuntimeException(String.format("No estimate exists with %1$s", estimateId));
            final Estimate savedEstimate = optionalEstimate.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if (null != savedEstimate.getCustomerOwnerUser()) {
                if (!savedEstimate.getCustomerOwnerUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("Estimate with the id: %1$s is associated with some other customer owner", estimateId));
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
    @Transactional
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

            if (null != estimate.getTechnicianUser()) {
                workOrder.setTechnicianUser(estimate.getTechnicianUser());
            } else {
                throw new RuntimeException(String.format("No technician found for this estimate"));
            }

            if (null != estimate.getMooring()) {
                workOrder.setMooring(estimate.getMooring());
            } else {
                throw new RuntimeException(String.format("No mooring found for this estimate"));
            }

            if (null != estimate.getCustomerOwnerUser()) {
                if (!user.getId().equals(estimate.getCustomerOwnerUser().getId()))
                    throw new RuntimeException(String.format("Estimate with the id: %1$s is associated with other customer owner", estimate.getId()));
                workOrder.setCustomerOwnerUser(estimate.getCustomerOwnerUser());
            } else {
                throw new RuntimeException(String.format("No customer owner found for this estimate"));
            }

            if (null != estimate.getWorkOrderStatus()) {

                if (StringUtils.equals(estimate.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.WAITING_ON_PARTS)) {
                    if (null == estimate.getInventoryList() || estimate.getInventoryList().isEmpty()) {
                        throw new ResourceNotProvidedException("No inventory item/s provided!!!");
                    }

                    List<Inventory> inventoryList = new ArrayList<>();

                    for (Inventory inventory : estimate.getInventoryList()) {

                        final Inventory parentInventory = inventoryRepository.findById(inventory.getParentInventoryId())
                                .orElseThrow(() -> new ResourceNotFoundException(String.format("No inventory found with the given id: %1$s", inventory.getParentInventoryId())));

                        if (parentInventory.getQuantity() < inventory.getQuantity()) {
                            throw new MathException("Given quantity is greater than available quantity");
                        }

                        parentInventory.setQuantity(parentInventory.getQuantity() - inventory.getQuantity());
                        inventoryRepository.save(parentInventory);

                        String originalItemName = inventory.getItemName().split("_")[0];
                        String workOrderNumber = workOrder.getWorkOrderNumber();

                        inventory.setItemName(originalItemName + "_WOR_" + workOrderNumber);
                        inventory.setEstimate(null);
                        inventory.setWorkOrder(workOrder);

                        inventoryRepository.save(inventory);

                        inventoryList.add(inventory);
                    }

                    workOrder.setInventoryList(inventoryList);
                }

                // Set work order status from estimate
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
            if (null == estimateId) estimate.setLastModifiedDate(new Date(System.currentTimeMillis()));

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            estimate.setCustomerOwnerUser(user);

            estimateMapper.mapToEstimate(estimate, estimateRequestDto);

            if(estimateId != null) {
                estimate.setCreationDate(new Date(System.currentTimeMillis()));
                estimate.setLastModifiedDate(new Date(System.currentTimeMillis()));
            } else {
                estimate.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }

            final LocalDate currentDate = LocalDate.now();

            if (null == estimateRequestDto.getScheduledDate() && null == estimateRequestDto.getDueDate()) {

            } else if (null == estimateRequestDto.getDueDate()) {

                final Date givenScheduleDate = DateUtil.stringToDate(estimateRequestDto.getScheduledDate());

                LocalDate localGivenScheduleDate = givenScheduleDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate localSavedDueDate = estimate.getDueDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (localGivenScheduleDate.isAfter(localSavedDueDate))
                    throw new RuntimeException(String.format("Given schedule date: %1$s is after saved due date: %2$s", localGivenScheduleDate, localSavedDueDate));

                if (null != estimate.getScheduledDate()) {
                    final Date savedScheduleDate = estimate.getScheduledDate();
                    LocalDate localSavedScheduleDate = savedScheduleDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    if (localGivenScheduleDate.isBefore(localSavedScheduleDate))
                        throw new RuntimeException(String.format("Given schedule date: %1$s is before saved schedule date: %2$s", localGivenScheduleDate, localSavedScheduleDate));
                }

                estimate.setScheduledDate(givenScheduleDate);
            } else if (null == estimateRequestDto.getScheduledDate()) {

                final Date givenDueDate = DateUtil.stringToDate(estimateRequestDto.getDueDate());

                LocalDate localGivenDueDate = givenDueDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (null != estimate.getScheduledDate()) {

                    LocalDate localSavedScheduleDate = estimate.getScheduledDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    if (localGivenDueDate.isBefore(localSavedScheduleDate))
                        throw new RuntimeException(String.format("Given due date: %1$s is before saved schedule date: %2$s", localGivenDueDate, localSavedScheduleDate));
                }

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
                    if (estimate.getScheduledDate() != null) {
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
                }
                estimate.setScheduledDate(givenScheduleDate);
                estimate.setDueDate(givenDueDate);
            }

            if (null != estimateRequestDto.getWorkOrderStatusId()) {
                final Optional<WorkOrderStatus> optionalWorkOrderStatus = workOrderStatusRepository.findById(estimateRequestDto.getWorkOrderStatusId());
                if (optionalWorkOrderStatus.isEmpty())
                    throw new RuntimeException(String.format("No estimate status found with the given id: %1$s", estimateRequestDto.getWorkOrderStatusId()));

                final WorkOrderStatus workOrderStatus = optionalWorkOrderStatus.get();

                if (workOrderStatus.getStatus().equals(AppConstants.WorkOrderStatusConstants.WAITING_ON_PARTS)) {

                    List<Integer> toDelete;
                    List<Integer> savedInventoryIds;
                    List<Inventory> inventoryList = new ArrayList<>();
                    if (null != estimate.getInventoryList() && !estimate.getInventoryList().isEmpty()) {
                        inventoryList = estimate.getInventoryList();

                        savedInventoryIds = estimate.getInventoryList().stream().map(Inventory::getId).toList();

                        toDelete = savedInventoryIds
                                .stream()
                                .filter(id -> estimateRequestDto.getInventoryRequestDtoList().stream().noneMatch(inventoryRequestDto -> null != inventoryRequestDto.getId() && inventoryRequestDto.getId().equals(id)))
                                .toList();

                        inventoryRepository.deleteAllById(toDelete);
                    }

                    for (InventoryRequestDto inventoryRequestDto : estimateRequestDto.getInventoryRequestDtoList()) {
                        final Inventory inventory = inventoryRepository.findById(inventoryRequestDto.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(String.format("No inventory found with the given id: %1$s", inventoryRequestDto.getId())));

                        if (null == inventory.getParentInventoryId()) {
                            final Inventory childInventory = inventoryMapper.mapToInventory(Inventory.builder().build(), inventory);
                            childInventory.setParentInventoryId(inventory.getId());
                            childInventory.setItemName(String.format(inventory.getItemName() + "_EST"));

                            int parentInventoryCount = inventory.getQuantity();
                            int childInventoryCount = inventoryRequestDto.getQuantity();

                            if (childInventoryCount > parentInventoryCount) {
                                throw new MathException("Given quantity is greater than available quantity");
                            }

                            childInventory.setQuantity(inventoryRequestDto.getQuantity());
                            childInventory.setEstimate(estimate);
                            inventoryList.add(childInventory);

                        } else {
                            final Inventory parentInventory = inventoryRepository.findById(inventory.getParentInventoryId())
                                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No inventory found with the given id: %1$s", inventory.getParentInventoryId())));

                            int parentInventoryCount = parentInventory.getQuantity();
                            int childInventoryCount = inventory.getQuantity();

                            if (childInventoryCount > parentInventoryCount) {
                                throw new MathException("Given quantity is greater than available quantity");
                            }

                            inventory.setQuantity(childInventoryCount);

                            if (inventoryRequestDto.getQuantity() == 0) {
                                inventory.setWorkOrder(null);
                                inventoryList.remove(inventory);
                                inventoryRepository.delete(inventory);
                            }
                        }

                    }

                    estimate.setInventoryList(inventoryList);
                }

                estimate.setWorkOrderStatus(workOrderStatus);
            } else {
                if (null == estimateId)
                    throw new RuntimeException(String.format("While saving estimate status cannot be null"));
            }

            if (null != estimateRequestDto.getMooringId()) {
                Optional<Mooring> optionalMooring = mooringRepository.findById(estimateRequestDto.getMooringId());
                if (optionalMooring.isEmpty())
                    throw new RuntimeException(String.format("No mooring found with the given id: %1$s", estimateRequestDto.getMooringId()));

                final Mooring mooring = optionalMooring.get();

                estimate.setMooring(mooring);
            }

            if (null != estimateRequestDto.getCustomerId()) {
                Optional<Customer> optionalCustomer = customerRepository.findById(estimateRequestDto.getCustomerId());
                if (optionalCustomer.isEmpty())
                    throw new RuntimeException(String.format("No customer found with the given id: %1$s", estimateRequestDto.getCustomerId()));

                final Customer customer = optionalCustomer.get();

                estimate.setCustomer(customer);
            } else {
                if (null == estimateId)
                    throw new RuntimeException("While saving estimate customer is required");
            }

            if (null != estimateRequestDto.getBoatyardId()) {
                Optional<Boatyard> optionalBoatyard = boatyardRepository.findById(estimateRequestDto.getBoatyardId());
                if (optionalBoatyard.isEmpty())
                    throw new RuntimeException(String.format("No boatyard found with the given id: %1$s", estimateRequestDto.getBoatyardId()));

                final Boatyard boatyard = optionalBoatyard.get();

                estimate.setBoatyard(boatyard);
            }

            if (null != estimateRequestDto.getTechnicianId()) {
                Optional<User> optionalTechnician = userRepository.findById(estimateRequestDto.getTechnicianId());
                if (optionalTechnician.isEmpty())
                    throw new RuntimeException(String.format("No technician found with the given id: %1$s", estimateRequestDto.getTechnicianId()));

                final User technician = optionalTechnician.get();

                if (null == technician.getRole())
                    throw new RuntimeException(String.format("User with id: %1$s is not assigned to any role", technician.getId()));

                if (!technician.getRole().getName().equals(AppConstants.Role.TECHNICIAN))
                    throw new RuntimeException(String.format("User with the id: %1$s is not of technician role", technician.getId()));

                estimate.setTechnicianUser(technician);
            }

            estimateRepository.save(estimate);
        } catch (Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException(e.getMessage(), e);
        }
    }

}
