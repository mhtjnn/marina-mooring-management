package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.model.dto.ImageDto;
import com.marinamooringmanagement.model.dto.MooringDueServiceStatusDto;
import com.marinamooringmanagement.model.dto.WorkOrderStatusDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.service.WorkOrderService;
import com.marinamooringmanagement.utils.DateUtil;
import com.marinamooringmanagement.utils.ImageUtils;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private SortUtils sortUtils;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private WorkOrderMapper workOrderMapper;

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
    private DateUtil dateUtil;

    @Autowired
    private ImageUtils imageUtils;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MooringDueServiceStatusMapper mooringDueServiceStatusMapper;

    @Autowired
    private MooringDueServiceStatusRepository mooringDueServiceStatusRepository;

    private static final Logger log = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

    @Override
    public BasicRestResponse fetchWorkOrders(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            authorizationUtil.checkAuthorityForTechnician(customerOwnerId);

            Specification<WorkOrder> spec = new Specification<WorkOrder>() {
                @Override
                public Predicate toPredicate(Root<WorkOrder> workOrder, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText && !searchText.isEmpty()) {
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

                    predicates.add(authorizationUtil.fetchPredicateForWorkOrder(customerOwnerId, workOrder, criteriaBuilder));

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            final Page<WorkOrder> workOrderList = workOrderRepository.findAll(spec, pageable);

            final List<WorkOrderResponseDto> workOrderResponseDtoList = workOrderList
                    .getContent()
                    .stream()
                    .map(workOrder -> {
                        WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                        if (null != workOrder.getMooring())
                            workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                        if (null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer())
                            workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                        if (null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard())
                            workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                        if (null != workOrder.getCustomerOwnerUser())
                            workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                        if (null != workOrder.getTechnicianUser())
                            workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                        if (null != workOrder.getWorkOrderStatus())
                            workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                        if (null != workOrder.getDueDate())
                            workOrderResponseDto.setDueDate(dateUtil.dateToString(workOrder.getDueDate()));
                        if (null != workOrder.getScheduledDate())
                            workOrderResponseDto.setScheduledDate(dateUtil.dateToString(workOrder.getScheduledDate()));
                        if (null != workOrder.getImageList() && !workOrder.getImageList().isEmpty()) {
                            workOrderResponseDto.setImageDtoList(workOrder.getImageList()
                                    .stream()
                                    .map(image -> imageMapper.toDto(ImageDto.builder().build(), image))
                                    .toList());
                        }
                        return workOrderResponseDto;
                    })
                    .collect(Collectors.toList());

            response.setTotalSize(workOrderRepository.findAll(spec).size());
            response.setCurrentSize(workOrderResponseDtoList.size());
            response.setMessage("All work orders fetched successfully.");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderResponseDtoList);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse saveWorkOrder(WorkOrderRequestDto workOrderRequestDto, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to save the work order in the database");
            final WorkOrder workOrder = WorkOrder.builder().build();

            if (null == workOrderRequestDto.getTechnicianId())
                throw new RuntimeException("Technician Id cannot be null");
            if (null == workOrderRequestDto.getMooringId()) throw new RuntimeException("Mooring Id cannot be null");

            performSave(workOrderRequestDto, workOrder, null, request);

            response.setMessage("Work order saved successfully.");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("Error occurred while saving the work order in the database {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse updateWorkOrder(WorkOrderRequestDto workOrderRequestDto, Integer workOrderId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to update the mooring with the given mooring ID");
            if (workOrderId == null) {
                throw new IllegalArgumentException("Work order Id not provided for update request");
            }
            Optional<WorkOrder> optionalWorkOrder = workOrderRepository.findById(workOrderId);
            final WorkOrder workOrder = optionalWorkOrder.orElseThrow(() -> new ResourceNotFoundException(String.format("Work order not found with id: %1$s", workOrderId)));
            performSave(workOrderRequestDto, workOrder, workOrderId, request);
            response.setMessage("Work order updated successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while updating work order {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    @Transactional
    public BasicRestResponse deleteWorkOrder(final Integer workOrderId, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            if (-1 == customerOwnerId && null != request.getAttribute("CUSTOMER_OWNER_ID"))
                customerOwnerId = (Integer) request.getAttribute("CUSTOMER_OWNER_ID");

            Optional<WorkOrder> optionalWorkOrder = workOrderRepository.findById(workOrderId);
            if (optionalWorkOrder.isEmpty())
                throw new RuntimeException(String.format("No work order exists with %1$s", workOrderId));
            final WorkOrder savedWorkOrder = optionalWorkOrder.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if (null != savedWorkOrder.getCustomerOwnerUser()) {
                if (!savedWorkOrder.getCustomerOwnerUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("Work order with the id: %1$s is associated with some other customer owner", workOrderId));
            } else {
                throw new RuntimeException(String.format("Work order with the id: %1$s is not associated with any customer owner", workOrderId));
            }

            workOrderRepository.delete(savedWorkOrder);

            Optional<WorkOrder> optionalWorkOrderAfterDeletion = workOrderRepository.findById(workOrderId);

            final String message = optionalWorkOrderAfterDeletion.isEmpty() ? String.format("Work order with id %1$s deleted successfully", workOrderId) : String.format("Failed to delete work order with the given id %1$s", workOrderId);
            response.setMessage(message);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while deleting work order with id " + workOrderId, e);
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    @Transactional
    public BasicRestResponse fetchOpenWorkOrders(final BaseSearchRequest baseSearchRequest, final Integer technicianId, final HttpServletRequest request, final String filterDateFrom, final String filterDateTo) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User technicianUser = authorizationUtil.checkForTechnician(technicianId, customerOwnerId);

            Date filterFromDate;
            Date filterToDate;

            List<WorkOrderResponseDto> workOrderResponseDtoList = null;

            if (null != filterDateFrom && null != filterDateTo) {
                filterFromDate = dateUtil.stringToDate(filterDateFrom);
                filterToDate = dateUtil.stringToDate(filterDateTo);

                if (null == filterFromDate) throw new RuntimeException(String.format("From Date is null"));
                if (null == filterToDate) throw new RuntimeException(String.format("To Date is null"));

                if (filterToDate.before(filterFromDate))
                    throw new RuntimeException(String.format("Invalid date range: %1$s cannot be earlier than %2$s.", filterDateTo, filterDateFrom));

                LocalDate localGivenScheduleDate = filterFromDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate localGivenDueDate = filterToDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                workOrderResponseDtoList = workOrderRepository.findAll()
                        .stream()
                        .filter(
                                workOrder -> {
                                    if (null != workOrder.getTechnicianUser()
                                            && null != workOrder.getCustomerOwnerUser()
                                            && null != workOrder.getWorkOrderStatus()
                                            && null != workOrder.getWorkOrderStatus().getStatus()
                                            && !StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.CLOSE)
                                            && workOrder.getTechnicianUser().getId().equals(technicianUser.getId())
                                            && workOrder.getCustomerOwnerUser().getId().equals(technicianUser.getCustomerOwnerId())
                                            && null != workOrder.getScheduledDate()
                                            && null != workOrder.getDueDate()) {

                                        LocalDate localSavedScheduleDate = workOrder.getScheduledDate().toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate();
                                        LocalDate localSavedDueDate = workOrder.getDueDate().toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate();

                                        return localSavedScheduleDate.isAfter(localGivenScheduleDate)
                                                && localSavedDueDate.isBefore(localGivenDueDate)
                                                && !localGivenScheduleDate.isAfter(localSavedDueDate)
                                                && !localGivenDueDate.isBefore(localSavedScheduleDate);
                                    }
                                    return false;
                                }
                        )
                        .map(workOrder -> {

                            WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                            if (null != workOrder.getMooring())
                                workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                            if (null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer())
                                workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                            if (null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard())
                                workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                            if (null != workOrder.getCustomerOwnerUser())
                                workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                            if (null != workOrder.getTechnicianUser())
                                workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                            if (null != workOrder.getWorkOrderStatus())
                                workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                            if (null != workOrder.getDueDate())
                                workOrderResponseDto.setDueDate(dateUtil.dateToString(workOrder.getDueDate()));
                            if (null != workOrder.getScheduledDate())
                                workOrderResponseDto.setScheduledDate(dateUtil.dateToString(workOrder.getScheduledDate()));

                            return workOrderResponseDto;
                        })
                        .toList();

            } else {
                workOrderResponseDtoList = workOrderRepository.findAll()
                        .stream()
                        .filter(
                                workOrder -> null != workOrder.getTechnicianUser()
                                        && null != workOrder.getCustomerOwnerUser()
                                        && null != workOrder.getWorkOrderStatus()
                                        && null != workOrder.getWorkOrderStatus().getStatus()
                                        && !StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.CLOSE)
                                        && workOrder.getTechnicianUser().getId().equals(technicianUser.getId())
                                        && workOrder.getCustomerOwnerUser().getId().equals(technicianUser.getCustomerOwnerId())
                        )
                        .map(workOrder -> {
                            WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                            if (null != workOrder.getMooring())
                                workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                            if (null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer())
                                workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                            if (null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard())
                                workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                            if (null != workOrder.getCustomerOwnerUser())
                                workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                            if (null != workOrder.getTechnicianUser())
                                workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                            if (null != workOrder.getWorkOrderStatus())
                                workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                            if (null != workOrder.getDueDate())
                                workOrderResponseDto.setDueDate(dateUtil.dateToString(workOrder.getDueDate()));
                            if (null != workOrder.getScheduledDate())
                                workOrderResponseDto.setScheduledDate(dateUtil.dateToString(workOrder.getScheduledDate()));

                            return workOrderResponseDto;
                        })
                        .toList();
            }

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), workOrderResponseDtoList.size());

            List<WorkOrderResponseDto> paginatedWorkOrder;
            if (start > workOrderResponseDtoList.size()) {
                paginatedWorkOrder = new ArrayList<>();
            } else {
                paginatedWorkOrder = workOrderResponseDtoList.subList(start, end);
            }

            response.setCurrentSize(paginatedWorkOrder.size());
            response.setTotalSize(workOrderResponseDtoList.size());
            response.setMessage(String.format("Work orders with technician of given id: %1$s fetched successfully", technicianId));
            response.setStatus(HttpStatus.OK.value());
            response.setContent(paginatedWorkOrder);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    @Transactional
    public BasicRestResponse fetchCloseWorkOrders(final BaseSearchRequest baseSearchRequest, final Integer technicianId, final HttpServletRequest request, final String filterDateFrom, final String filterDateTo) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User technicianUser = authorizationUtil.checkForTechnician(technicianId, customerOwnerId);

            Date filterFromDate;
            Date filterToDate;

            List<WorkOrderResponseDto> workOrderResponseDtoList = null;

            if (null != filterDateFrom && null != filterDateTo) {
                filterFromDate = dateUtil.stringToDate(filterDateFrom);
                filterToDate = dateUtil.stringToDate(filterDateTo);

                if (null == filterFromDate) throw new RuntimeException(String.format("From Date is null"));
                if (null == filterToDate) throw new RuntimeException(String.format("To Date is null"));

                if (filterToDate.before(filterFromDate))
                    throw new RuntimeException(String.format("Invalid date range: %1$s cannot be earlier than %2$s.", filterDateTo, filterDateFrom));

                LocalDate localGivenScheduleDate = filterFromDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate localGivenDueDate = filterToDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                workOrderResponseDtoList = workOrderRepository.findAll()
                        .stream()
                        .filter(
                                workOrder -> {
                                    if (null != workOrder.getTechnicianUser()
                                            && null != workOrder.getCustomerOwnerUser()
                                            && null != workOrder.getWorkOrderStatus()
                                            && null != workOrder.getWorkOrderStatus().getStatus()
                                            && StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.CLOSE)
                                            && workOrder.getTechnicianUser().getId().equals(technicianUser.getId())
                                            && workOrder.getCustomerOwnerUser().getId().equals(technicianUser.getCustomerOwnerId())
                                            && null != workOrder.getScheduledDate()
                                            && null != workOrder.getDueDate()) {

                                        LocalDate localSavedScheduleDate = workOrder.getScheduledDate().toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate();
                                        LocalDate localSavedDueDate = workOrder.getDueDate().toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate();

                                        return localSavedScheduleDate.isAfter(localGivenScheduleDate)
                                                && localSavedDueDate.isBefore(localGivenDueDate)
                                                && !localGivenScheduleDate.isAfter(localSavedDueDate)
                                                && !localGivenDueDate.isBefore(localSavedScheduleDate);
                                    }
                                    return false;
                                }
                        )
                        .map(workOrder -> {

                            WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                            if (null != workOrder.getMooring())
                                workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                            if (null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer())
                                workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                            if (null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard())
                                workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                            if (null != workOrder.getCustomerOwnerUser())
                                workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                            if (null != workOrder.getTechnicianUser())
                                workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                            if (null != workOrder.getWorkOrderStatus())
                                workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                            if (null != workOrder.getDueDate())
                                workOrderResponseDto.setDueDate(dateUtil.dateToString(workOrder.getDueDate()));
                            if (null != workOrder.getScheduledDate())
                                workOrderResponseDto.setScheduledDate(dateUtil.dateToString(workOrder.getScheduledDate()));

                            return workOrderResponseDto;
                        })
                        .toList();

            } else {
                workOrderResponseDtoList = workOrderRepository.findAll()
                        .stream()
                        .filter(
                                workOrder -> null != workOrder.getTechnicianUser()
                                        && null != workOrder.getCustomerOwnerUser()
                                        && null != workOrder.getWorkOrderStatus()
                                        && null != workOrder.getWorkOrderStatus().getStatus()
                                        && StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.CLOSE)
                                        && workOrder.getTechnicianUser().getId().equals(technicianUser.getId())
                                        && workOrder.getCustomerOwnerUser().getId().equals(technicianUser.getCustomerOwnerId())
                        )
                        .map(workOrder -> {
                            WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                            if (null != workOrder.getMooring())
                                workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                            if (null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer())
                                workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                            if (null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard())
                                workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                            if (null != workOrder.getCustomerOwnerUser())
                                workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                            if (null != workOrder.getTechnicianUser())
                                workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                            if (null != workOrder.getWorkOrderStatus())
                                workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                            if (null != workOrder.getDueDate())
                                workOrderResponseDto.setDueDate(dateUtil.dateToString(workOrder.getDueDate()));
                            if (null != workOrder.getScheduledDate())
                                workOrderResponseDto.setScheduledDate(dateUtil.dateToString(workOrder.getScheduledDate()));

                            return workOrderResponseDto;
                        })
                        .toList();
            }

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), workOrderResponseDtoList.size());

            List<WorkOrderResponseDto> paginatedWorkOrder;
            if (start > workOrderResponseDtoList.size()) {
                paginatedWorkOrder = new ArrayList<>();
            } else {
                paginatedWorkOrder = workOrderResponseDtoList.subList(start, end);
            }

            response.setCurrentSize(paginatedWorkOrder.size());
            response.setTotalSize(workOrderResponseDtoList.size());
            response.setMessage(String.format("Work orders with technician of given id: %1$s fetched successfully", technicianId));
            response.setStatus(HttpStatus.OK.value());
            response.setContent(paginatedWorkOrder);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchAllOpenWorkOrdersAndMooringDueForService(BaseSearchRequest baseSearchRequest, HttpServletRequest request, String filterDateFrom, String filterDateTo) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            final AllWorkOrdersAndMooringDueForServiceResponse allWorkOrdersAndMooringDueForServiceResponse = AllWorkOrdersAndMooringDueForServiceResponse.builder().build();

            final Date filterFromDate = dateUtil.stringToDate(filterDateFrom);
            final Date filterToDate = dateUtil.stringToDate(filterDateTo);

            List<WorkOrderResponseDto> workOrderResponseDtoList = null;

            if (null == filterFromDate) throw new RuntimeException(String.format("From Date cannot be null"));
            if (null == filterToDate) throw new RuntimeException(String.format("To Date cannot be null"));

            if (filterToDate.before(filterFromDate))
                throw new RuntimeException(String.format("Invalid date range: %1$s cannot be earlier than %2$s.", filterDateTo, filterDateFrom));

            LocalDate localGivenScheduleDate = filterFromDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate localGivenDueDate = filterToDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            workOrderResponseDtoList = workOrderRepository.findAll()
                    .stream()
                    .filter(
                            workOrder -> {
                                if (null != workOrder.getTechnicianUser()
                                        && null != workOrder.getCustomerOwnerUser()
                                        && null != workOrder.getWorkOrderStatus()
                                        && null != workOrder.getWorkOrderStatus().getStatus()
                                        && workOrder.getCustomerOwnerUser().getId().equals(user.getId())
                                        && !StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.CLOSE)
                                        && null != workOrder.getScheduledDate()
                                        && null != workOrder.getDueDate()) {

                                    LocalDate localSavedScheduleDate = workOrder.getScheduledDate().toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
                                    LocalDate localSavedDueDate = workOrder.getDueDate().toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();

                                    return localSavedScheduleDate.isAfter(localGivenScheduleDate)
                                            && localSavedDueDate.isBefore(localGivenDueDate)
                                            && !localGivenScheduleDate.isAfter(localSavedDueDate)
                                            && !localGivenDueDate.isBefore(localSavedScheduleDate);
                                }
                                return false;
                            }
                    )
                    .map(workOrder -> {

                        WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                        if (null != workOrder.getMooring())
                            workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                        if (null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer())
                            workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                        if (null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard())
                            workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                        if (null != workOrder.getCustomerOwnerUser())
                            workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                        if (null != workOrder.getTechnicianUser())
                            workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                        if (null != workOrder.getWorkOrderStatus())
                            workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                        if (null != workOrder.getDueDate())
                            workOrderResponseDto.setDueDate(dateUtil.dateToString(workOrder.getDueDate()));
                        if (null != workOrder.getScheduledDate())
                            workOrderResponseDto.setScheduledDate(dateUtil.dateToString(workOrder.getScheduledDate()));

                        return workOrderResponseDto;
                    })
                    .toList();


            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), workOrderResponseDtoList.size());

            List<WorkOrderResponseDto> paginatedWorkOrder;
            if (start > workOrderResponseDtoList.size()) {
                paginatedWorkOrder = new ArrayList<>();
            } else {
                paginatedWorkOrder = workOrderResponseDtoList.subList(start, end);
            }

            allWorkOrdersAndMooringDueForServiceResponse.setWorkOrderResponseDtoList(paginatedWorkOrder);
            allWorkOrdersAndMooringDueForServiceResponse.setMooringDueServiceResponseDtoList(getMooringDueServiceResponseDtoList(workOrderResponseDtoList));

            response.setCurrentSize(paginatedWorkOrder.size());
            response.setTotalSize(workOrderResponseDtoList.size());
            response.setMessage(String.format("All open work orders and mooring due for service with customer owner of given id: %1$s fetched successfully", user.getId()));
            response.setStatus(HttpStatus.OK.value());
            response.setContent(allWorkOrdersAndMooringDueForServiceResponse);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private void performSave(final WorkOrderRequestDto workOrderRequestDto, final WorkOrder workOrder, final Integer workOrderId, final HttpServletRequest request) {
        try {
            if (null == workOrderId) workOrder.setLastModifiedDate(new Date(System.currentTimeMillis()));

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            workOrder.setCustomerOwnerUser(user);

            workOrderMapper.mapToWorkOrder(workOrder, workOrderRequestDto);

            if (null != workOrderRequestDto.getEncodedImages() && !workOrderRequestDto.getEncodedImages().isEmpty()) {
                List<Image> imageList = new ArrayList<>();
                if (null != workOrder.getImageList() && !workOrder.getImageList().isEmpty())
                    imageList = workOrder.getImageList();
                for (String endcodedImageString : workOrderRequestDto.getEncodedImages()) {
                    Image image = Image.builder().build();
                    image.setImageData(imageUtils.validateEncodedString(endcodedImageString));
                    image.setCreationDate(new Date(System.currentTimeMillis()));
                    image.setLastModifiedDate(new Date(System.currentTimeMillis()));
                    imageList.add(image);
                }

                workOrder.setImageList(imageList);
            }

            final LocalDate currentDate = LocalDate.now();

            if (null == workOrderRequestDto.getScheduledDate() && null == workOrderRequestDto.getDueDate()) {
                if (null == workOrderId)
                    throw new RuntimeException(String.format("Due date and Schedule date cannot be null during save"));
            } else if (null == workOrderRequestDto.getDueDate()) {
                if (null == workOrderId)
                    throw new RuntimeException(String.format("Due date cannot be null during saved"));
                final Date savedScheduleDate = workOrder.getScheduledDate();
                final Date givenScheduleDate = dateUtil.stringToDate(workOrderRequestDto.getScheduledDate());

                LocalDate localSavedScheduleDate = savedScheduleDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate localGivenScheduleDate = givenScheduleDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate localSavedDueDate = workOrder.getDueDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (localGivenScheduleDate.isBefore(localSavedScheduleDate))
                    throw new RuntimeException(String.format("Given schedule date: %1$s is before saved schedule date: %2$s", localGivenScheduleDate, localSavedScheduleDate));
                if (localGivenScheduleDate.isAfter(localSavedDueDate))
                    throw new RuntimeException(String.format("Given schedule date: %1$s is after saved due date: %2$s", localGivenScheduleDate, localSavedDueDate));

                workOrder.setScheduledDate(givenScheduleDate);
            } else if (null == workOrderRequestDto.getScheduledDate()) {
                if (null == workOrderId)
                    throw new RuntimeException(String.format("Schedule date cannot be null during save"));
                final Date givenDueDate = dateUtil.stringToDate(workOrderRequestDto.getDueDate());

                LocalDate localGivenDueDate = givenDueDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                LocalDate localSavedScheduleDate = workOrder.getScheduledDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (localGivenDueDate.isBefore(localSavedScheduleDate))
                    throw new RuntimeException(String.format("Given due date: %1$s is before saved schedule date: %2$s", localGivenDueDate, localSavedScheduleDate));
                workOrder.setDueDate(givenDueDate);
            } else {

                final Date givenScheduleDate = dateUtil.stringToDate(workOrderRequestDto.getScheduledDate());
                final Date givenDueDate = dateUtil.stringToDate(workOrderRequestDto.getDueDate());

                if (null == workOrderId) {
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

                    final Date savedScheduleDate = workOrder.getScheduledDate();

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

                workOrder.setScheduledDate(givenScheduleDate);
                workOrder.setDueDate(givenDueDate);
            }

            if (null != workOrderRequestDto.getWorkOrderStatusId()) {
                final Optional<WorkOrderStatus> optionalWorkOrderStatus = workOrderStatusRepository.findById(workOrderRequestDto.getWorkOrderStatusId());
                if (optionalWorkOrderStatus.isEmpty())
                    throw new RuntimeException(String.format("No work order status found with the given id: %1$s", workOrderRequestDto.getWorkOrderStatusId()));

                final WorkOrderStatus workOrderStatus = optionalWorkOrderStatus.get();
                workOrder.setWorkOrderStatus(workOrderStatus);
            } else {
                if (null == workOrderId)
                    throw new RuntimeException(String.format("While saving work order status cannot be null"));
            }

            if (null != workOrderRequestDto.getMooringId()) {
                Optional<Mooring> optionalMooring = mooringRepository.findById(workOrderRequestDto.getMooringId());
                if (optionalMooring.isEmpty())
                    throw new RuntimeException(String.format("No mooring found with the given id: %1$s", workOrderRequestDto.getMooringId()));

                final Mooring mooring = optionalMooring.get();

                if (null == workOrderRequestDto.getCustomerId())
                    throw new RuntimeException("Customer Id cannot be null during saving/updating work order");
                if (null == workOrderRequestDto.getBoatyardId())
                    throw new RuntimeException("Boatyard Customer Id cannot be null during saving/updating work order");
                if (null == mooring.getCustomer())
                    throw new RuntimeException(String.format("No customer found for the mooring with the id: %1$s", workOrderRequestDto.getMooringId()));
                if (null == mooring.getBoatyard())
                    throw new RuntimeException(String.format("No boatyard found for the mooring with the id: %1$s", workOrderRequestDto.getMooringId()));
                if (null == mooring.getUser())
                    throw new RuntimeException(String.format("No user found for the mooring with the id: %1$s", workOrderRequestDto.getMooringId()));
                if (!mooring.getCustomer().getId().equals(workOrderRequestDto.getCustomerId()))
                    throw new RuntimeException(String.format("Customer Id is different in mooring with given id: %1$s", mooring.getId()));
                if (!mooring.getBoatyard().getId().equals(workOrderRequestDto.getBoatyardId()))
                    throw new RuntimeException(String.format("Boatyard Id is different in mooring with given id: %1$s", mooring.getId()));
                if (!mooring.getUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("Customer owner is different in mooring with given id: %1$s", mooring.getId()));

                workOrder.setMooring(mooring);
            } else {
                if (null == workOrderId)
                    throw new RuntimeException(String.format("While saving work order mooring id cannot be null"));
            }

            if (null != workOrderRequestDto.getTechnicianId()) {
                Optional<User> optionalTechnician = userRepository.findById(workOrderRequestDto.getTechnicianId());
                if (optionalTechnician.isEmpty())
                    throw new RuntimeException(String.format("No technician found with the given id: %1$s", workOrderRequestDto.getTechnicianId()));

                final User technician = optionalTechnician.get();

                if (null == technician.getRole())
                    throw new RuntimeException(String.format("User with id: %1$s is not assigned to any role", technician.getId()));
                if (!technician.getRole().getName().equals(AppConstants.Role.TECHNICIAN))
                    throw new RuntimeException(String.format("User with the id: %1$s is not of technician role", technician.getId()));
                if (null == technician.getCustomerOwnerId())
                    throw new RuntimeException(String.format("Technician with the id: %1$s is not associated with any customer owner", workOrderRequestDto.getTechnicianId()));
                if (!technician.getCustomerOwnerId().equals(user.getId()))
                    throw new RuntimeException(String.format("Customer owner is different in technician of id: %1$s", workOrderRequestDto.getTechnicianId()));

                workOrder.setTechnicianUser(technician);
            } else {
                if (null == workOrderId)
                    throw new RuntimeException(String.format("Technician Id cannot be null during saving/updating work order"));
            }

            workOrderRepository.save(workOrder);
        } catch (Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException(e.getMessage(), e);
        }
    }

    private List<MooringDueServiceResponseDto> getMooringDueServiceResponseDtoList(final List<WorkOrderResponseDto> workOrderResponseDtoList) {

        HashMap<String, MooringDueServiceResponseDto> mooringDueServiceResponseDtoHashMap = new HashMap<>();
        for(WorkOrderResponseDto workOrderResponseDto: workOrderResponseDtoList) {
            if (null == workOrderResponseDto.getMooringResponseDto())
                throw new RuntimeException(String.format("No mooring found for the work order with the id: %1$s", workOrderResponseDto.getId()));
            MooringDueServiceResponseDto mooringDueServiceResponseDto = MooringDueServiceResponseDto.builder().build();

            if (null == workOrderResponseDto.getCustomerResponseDto())
                throw new RuntimeException(String.format("No customer found for the work order with the id: %1$s", workOrderResponseDto.getId()));

            final MooringResponseDto mooringResponseDto = workOrderResponseDto.getMooringResponseDto();
            final CustomerResponseDto customerResponseDto = workOrderResponseDto.getCustomerResponseDto();

            mooringMapper.mapToMooringDueServiceResponseDto(mooringDueServiceResponseDto, mooringResponseDto);

            mooringDueServiceResponseDto.setCustomerResponseDto(customerResponseDto);

            Optional<MooringDueServiceStatus> optionalMooringDueServiceStatus;
            final MooringDueServiceStatus mooringDueServiceStatus;
            final MooringDueServiceStatusDto mooringDueServiceStatusDto;

            if (workOrderResponseDto.getWorkOrderStatusDto().getStatus().equals(AppConstants.WorkOrderStatusConstants.CLOSE)) {
                optionalMooringDueServiceStatus = mooringDueServiceStatusRepository.findByStatus(AppConstants.MooringDueServiceStatusConstants.COMPLETE);
                if (optionalMooringDueServiceStatus.isEmpty())
                    throw new RuntimeException(String.format("No mooring due for service status found with the status as complete"));

                mooringDueServiceStatus = optionalMooringDueServiceStatus.get();
                mooringDueServiceStatusDto = MooringDueServiceStatusDto.builder().build();
                mooringDueServiceStatusMapper.toDto(mooringDueServiceStatusDto, mooringDueServiceStatus);

                mooringDueServiceResponseDto.setMooringDueServiceStatusDto(mooringDueServiceStatusDto);
            } else {
                optionalMooringDueServiceStatus = mooringDueServiceStatusRepository.findByStatus(AppConstants.MooringDueServiceStatusConstants.PENDING);
                if (optionalMooringDueServiceStatus.isEmpty())
                    throw new RuntimeException(String.format("No mooring due for service status found with the status as pending"));

                mooringDueServiceStatus = optionalMooringDueServiceStatus.get();
                mooringDueServiceStatusDto = MooringDueServiceStatusDto.builder().build();
                mooringDueServiceStatusMapper.toDto(mooringDueServiceStatusDto, mooringDueServiceStatus);

                mooringDueServiceResponseDto.setMooringDueServiceStatusDto(mooringDueServiceStatusDto);
            }

            if (mooringDueServiceResponseDtoHashMap.containsKey(mooringDueServiceResponseDto.getMooringNumber())) {
                MooringDueServiceResponseDto mooringDueServiceResponseDtoFromMap = mooringDueServiceResponseDtoHashMap.get(mooringDueServiceResponseDto.getMooringNumber());
                if (
                        null != mooringDueServiceStatusDto
                        && mooringDueServiceResponseDtoFromMap.getMooringDueServiceStatusDto().getStatus().equals(AppConstants.MooringDueServiceStatusConstants.COMPLETE)
                                && !mooringDueServiceResponseDtoFromMap.getMooringDueServiceStatusDto().getStatus().equals(mooringDueServiceStatusDto.getStatus())
                ) {
                    mooringDueServiceResponseDtoFromMap.setMooringDueServiceStatusDto(mooringDueServiceStatusDto);
                }

                if(null != mooringDueServiceResponseDtoFromMap.getMooringServiceDate()) {
                    if(null != workOrderResponseDto.getDueDate()) {
                        Date savedDueDate = dateUtil.stringToDate(workOrderResponseDto.getDueDate());
                        Date mooringServiceDate = dateUtil.stringToDate(mooringDueServiceResponseDtoFromMap.getMooringServiceDate());
                        if(savedDueDate.after(mooringServiceDate)) {
                            mooringDueServiceResponseDtoFromMap.setMooringServiceDate(workOrderResponseDto.getDueDate());
                        }
                    } else {
                        throw new RuntimeException(String.format("Due date is null for work order of id: %1$s", workOrderResponseDto.getId()));
                    }
                } else {
                    throw new RuntimeException(String.format("Mooring service date is null for mooring of id: %1$s", mooringDueServiceResponseDtoFromMap.getId()));
                }
            } else {
                if(null == workOrderResponseDto.getDueDate()) throw new RuntimeException(String.format("Due date is null for work order of id: %1$s", workOrderResponseDto.getId()));
                mooringDueServiceResponseDto.setMooringServiceDate(workOrderResponseDto.getDueDate());
                mooringDueServiceResponseDtoHashMap.put(mooringDueServiceResponseDto.getMooringNumber(), mooringDueServiceResponseDto);
            }

        }
        Collection<MooringDueServiceResponseDto> mooringDueServiceResponseDtoCollection = mooringDueServiceResponseDtoHashMap.values();
        return new ArrayList<>(mooringDueServiceResponseDtoCollection);
    }
}
