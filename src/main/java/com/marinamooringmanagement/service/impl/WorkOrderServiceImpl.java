package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.mapper.metadata.*;
import com.marinamooringmanagement.model.dto.*;
import com.marinamooringmanagement.model.dto.metadata.*;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.metadata.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.ImageRequestDto;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.repositories.metadata.MooringDueServiceStatusRepository;
import com.marinamooringmanagement.repositories.metadata.MooringStatusRepository;
import com.marinamooringmanagement.repositories.metadata.WorkOrderInvoiceStatusRepository;
import com.marinamooringmanagement.repositories.metadata.WorkOrderStatusRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.WorkOrderService;
import com.marinamooringmanagement.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private ImageMapper imageMapper;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MooringDueServiceStatusMapper mooringDueServiceStatusMapper;

    @Autowired
    private MooringDueServiceStatusRepository mooringDueServiceStatusRepository;

    @Autowired
    private WorkOrderPayStatusRepository workOrderPayStatusRepository;

    @Autowired
    private WorkOrderPayStatusMapper workOrderPayStatusMapper;

    @Autowired
    private WorkOrderInvoiceRepository workOrderInvoiceRepository;

    @Autowired
    private WorkOrderInvoiceStatusRepository workOrderInvoiceStatusRepository;

    @Autowired
    private WorkOrderInvoiceMapper workOrderInvoiceMapper;

    @Autowired
    private WorkOrderInvoiceStatusMapper workOrderInvoiceStatusMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FormMapper formMapper;

    @Autowired
    private MooringStatusRepository mooringStatusRepository;

    @Autowired
    private VoiceMEMORepository voiceMEMORepository;

    @Autowired
    private VoiceMEMOMapper voiceMEMOMapper;

    private static final Logger log = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

    @Override
    public BasicRestResponse fetchWorkOrders(final BaseSearchRequest baseSearchRequest, final String searchText, final String showCompletedWorkOrders, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the work orders in the database");

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthorityForTechnician(customerOwnerId);

            final String loggedInUserRole = LoggedInUserUtil.getLoggedInUserRole();

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            final List<WorkOrder> workOrderList;
            if (StringUtils.equals(loggedInUserRole, AppConstants.Role.ADMINISTRATOR)
                    || StringUtils.equals(loggedInUserRole, AppConstants.Role.CUSTOMER_OWNER)) {
                workOrderList = workOrderRepository.findAll((null == searchText) ? "" : searchText, user.getId(), showCompletedWorkOrders);
            } else if (StringUtils.equals(loggedInUserRole, AppConstants.Role.TECHNICIAN)) {
                workOrderList = workOrderRepository.findAllByTechnicianUser((null == searchText) ? "" : searchText, user.getId(), showCompletedWorkOrders);
            } else {
                throw new RuntimeException("No authorized");
            }

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), workOrderList.size());

            List<WorkOrder> paginatedWorkOrder;
            if (start > workOrderList.size()) paginatedWorkOrder = new ArrayList<>();
            else paginatedWorkOrder = workOrderList.subList(start, end);

            final List<WorkOrderResponseDto> workOrderResponseDtoList = paginatedWorkOrder
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
                        if (null != workOrder.getWorkOrderPayStatus())
                            workOrderResponseDto.setWorkOrderPayStatusDto(workOrderPayStatusMapper.toDto(WorkOrderPayStatusDto.builder().build(), workOrder.getWorkOrderPayStatus()));
                        if (null != workOrder.getDueDate())
                            workOrderResponseDto.setDueDate(DateUtil.dateToString(workOrder.getDueDate()));
                        if (null != workOrder.getScheduledDate())
                            workOrderResponseDto.setScheduledDate(DateUtil.dateToString(workOrder.getScheduledDate()));
                        if (null != workOrder.getCompletedDate())
                            workOrderResponseDto.setCompletedDate(DateUtil.dateToString(workOrder.getCompletedDate()));
                        if (null != workOrder.getImageList() && !workOrder.getImageList().isEmpty()) {
                            workOrderResponseDto.setImageDtoList(workOrder.getImageList()
                                    .stream()
                                    .map(image -> imageMapper.toDto(ImageDto.builder().build(), image))
                                    .toList());
                        }
                        return workOrderResponseDto;
                    })
                    .collect(Collectors.toList());

            response.setTotalSize(workOrderList.size());
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

            WorkOrderResponseDto workOrderResponseDto = performSave(workOrderRequestDto, workOrder, null, request);

            response.setMessage("Work order saved successfully.");
            response.setStatus(HttpStatus.CREATED.value());
            response.setContent(workOrderResponseDto);
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
            final WorkOrderResponseDto workOrderResponseDto = performSave(workOrderRequestDto, workOrder, workOrderId, request);
            response.setMessage("Work order updated successfully");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderResponseDto);
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
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            if (-1 == customerOwnerId && null != request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID))
                customerOwnerId = (Integer) request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

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
    public BasicRestResponse fetchOpenWorkOrders(final BaseSearchRequest baseSearchRequest, final Integer technicianId, final HttpServletRequest request, final String filterDateFrom, final String filterDateTo) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User technicianUser = authorizationUtil.checkForTechnician(technicianId, customerOwnerId);

            Date filterFromDate;
            Date filterToDate;

            List<WorkOrderResponseDto> workOrderResponseDtoList = new ArrayList<>();

            List<WorkOrder> workOrderList = new ArrayList<>();

            if (null != filterDateFrom && null != filterDateTo) {
                filterFromDate = DateUtil.stringToDate(filterDateFrom);
                filterToDate = DateUtil.stringToDate(filterDateTo);

                if (filterToDate.before(filterFromDate))
                    throw new RuntimeException(String.format("Invalid date range: %1$s cannot be earlier than %2$s.", filterDateTo, filterDateFrom));

                workOrderList = workOrderRepository.findWorkOrderForGivenTechnicianWithDateFilter
                        (
                                technicianId,
                                technicianUser.getCustomerOwnerId(),
                                filterFromDate,
                                filterToDate,
                                AppConstants.BooleanStringConst.NO
                        );

            } else {
                workOrderList = workOrderRepository.findWorkOrderForGivenTechnicianWithoutDateFilter(
                        technicianId,
                        technicianUser.getCustomerOwnerId(),
                        AppConstants.BooleanStringConst.NO
                );
            }


            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), workOrderList.size());

            List<WorkOrder> paginatedWorkOrder;
            if (start > workOrderList.size()) {
                paginatedWorkOrder = new ArrayList<>();
            } else {
                paginatedWorkOrder = workOrderList.subList(start, end);
            }

            workOrderResponseDtoList = paginatedWorkOrder
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
                            workOrderResponseDto.setDueDate(DateUtil.dateToString(workOrder.getDueDate()));
                        if (null != workOrder.getScheduledDate())
                            workOrderResponseDto.setScheduledDate(DateUtil.dateToString(workOrder.getScheduledDate()));
                        if (null != workOrder.getCompletedDate())
                            workOrderResponseDto.setCompletedDate(DateUtil.dateToString(workOrder.getCompletedDate()));

                        return workOrderResponseDto;
                    })
                    .toList();

            response.setCurrentSize(workOrderResponseDtoList.size());
            response.setTotalSize(workOrderList.size());
            response.setMessage(String.format("Work orders with technician of given id: %1$s fetched successfully", technicianId));
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderResponseDtoList);
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
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User technicianUser = authorizationUtil.checkForTechnician(technicianId, customerOwnerId);

            Date filterFromDate;
            Date filterToDate;

            List<WorkOrderResponseDto> workOrderResponseDtoList = new ArrayList<>();

            List<WorkOrder> workOrderList = new ArrayList<>();

            if (null != filterDateFrom && null != filterDateTo) {
                filterFromDate = DateUtil.stringToDate(filterDateFrom);
                filterToDate = DateUtil.stringToDate(filterDateTo);

                if (filterToDate.before(filterFromDate))
                    throw new RuntimeException(String.format("Invalid date range: %1$s cannot be earlier than %2$s.", filterDateTo, filterDateFrom));

                workOrderList = workOrderRepository.findWorkOrderForGivenTechnicianWithDateFilter
                        (
                                technicianId,
                                technicianUser.getCustomerOwnerId(),
                                filterFromDate,
                                filterToDate,
                                AppConstants.BooleanStringConst.YES
                        );

            } else {
                workOrderList = workOrderRepository.findWorkOrderForGivenTechnicianWithoutDateFilter(
                        technicianId,
                        technicianUser.getCustomerOwnerId(),
                        AppConstants.BooleanStringConst.YES
                );
            }


            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), workOrderList.size());

            List<WorkOrder> paginatedWorkOrder;
            if (start > workOrderList.size()) {
                paginatedWorkOrder = new ArrayList<>();
            } else {
                paginatedWorkOrder = workOrderList.subList(start, end);
            }

            workOrderResponseDtoList = paginatedWorkOrder
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
                            workOrderResponseDto.setDueDate(DateUtil.dateToString(workOrder.getDueDate()));
                        if (null != workOrder.getScheduledDate())
                            workOrderResponseDto.setScheduledDate(DateUtil.dateToString(workOrder.getScheduledDate()));
                        if (null != workOrder.getCompletedDate())
                            workOrderResponseDto.setCompletedDate(DateUtil.dateToString(workOrder.getCompletedDate()));

                        return workOrderResponseDto;
                    })
                    .toList();

            response.setCurrentSize(workOrderResponseDtoList.size());
            response.setTotalSize(workOrderList.size());
            response.setMessage(String.format("Work orders with technician of given id: %1$s fetched successfully", technicianId));
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderResponseDtoList);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchAllOpenWorkOrdersAndMooringDueForService(BaseSearchRequest baseSearchRequest, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            final AllWorkOrdersAndMooringDueForServiceResponse allWorkOrdersAndMooringDueForServiceResponse = AllWorkOrdersAndMooringDueForServiceResponse.builder().build();

            List<WorkOrderResponseDto> openWorkOrderResponseDtoList;

            List<WorkOrder> workOrderList = workOrderRepository.findAll("", user.getId(), AppConstants.BooleanStringConst.NO);

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), workOrderList.size());

            List<WorkOrder> paginatedWorkOrder;
            if (start > workOrderList.size()) {
                paginatedWorkOrder = new ArrayList<>();
            } else {
                paginatedWorkOrder = workOrderList.subList(start, end);
            }

            openWorkOrderResponseDtoList =
                    paginatedWorkOrder
                            .stream()
                            .map(workOrder -> {
                                WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                                workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                                MooringResponseDto mooringResponseDto = MooringResponseDto.builder().build();
                                if (null != workOrder.getMooring()) {
                                    mooringMapper.mapToMooringResponseDto(mooringResponseDto, workOrder.getMooring());
                                    if (null != workOrder.getMooring().getInstallConditionOfEyeDate()) {
                                        mooringResponseDto.setInstallConditionOfEyeDate(DateUtil.dateToString(workOrder.getMooring().getInstallConditionOfEyeDate()));
                                    }
                                    if (null != workOrder.getMooring().getInstallTopChainDate()) {
                                        mooringResponseDto.setInstallTopChainDate(DateUtil.dateToString(workOrder.getMooring().getInstallTopChainDate()));
                                    }
                                    if (null != workOrder.getMooring().getInstallBottomChainDate()) {
                                        mooringResponseDto.setInstallBottomChainDate(DateUtil.dateToString(workOrder.getMooring().getInstallBottomChainDate()));
                                    }
                                    if (null != workOrder.getMooring().getInspectionDate()) {
                                        mooringResponseDto.setInspectionDate(DateUtil.dateToString(workOrder.getMooring().getInspectionDate()));
                                    }
                                    workOrderResponseDto.setMooringResponseDto(mooringResponseDto);
                                    if (null != workOrder.getMooring().getServiceArea())
                                        mooringResponseDto.setServiceAreaResponseDto(serviceAreaMapper.mapToResponseDto(ServiceAreaResponseDto.builder().build(), workOrder.getMooring().getServiceArea()));
                                    if (null != workOrder.getMooring().getCustomer()) {
                                        workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                                        if (null != workOrder.getMooring().getCustomer().getFirstName()
                                                && null != workOrder.getMooring().getCustomer().getLastName())
                                            mooringResponseDto.setCustomerName(
                                                    workOrder.getMooring().getCustomer().getFirstName() + " " + workOrder.getMooring().getCustomer().getLastName()
                                            );
                                    }
                                }
                                if (null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard())
                                    workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                                if (null != workOrder.getCustomerOwnerUser())
                                    workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                                if (null != workOrder.getTechnicianUser())
                                    workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                                if (null != workOrder.getWorkOrderStatus())
                                    workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                                if (null != workOrder.getDueDate())
                                    workOrderResponseDto.setDueDate(DateUtil.dateToString(workOrder.getDueDate()));
                                if (null != workOrder.getScheduledDate())
                                    workOrderResponseDto.setScheduledDate(DateUtil.dateToString(workOrder.getScheduledDate()));

                                return workOrderResponseDto;
                            })
                            .collect(Collectors.toList());

            allWorkOrdersAndMooringDueForServiceResponse.setWorkOrderResponseDtoList(openWorkOrderResponseDtoList);
            allWorkOrdersAndMooringDueForServiceResponse.setMooringDueServiceResponseDtoList(getMooringDueServiceResponseDtoList(workOrderList));

            response.setCurrentSize(openWorkOrderResponseDtoList.size());
            response.setTotalSize(workOrderList.size());
            response.setMessage(String.format("All open work orders and mooring due for service with customer owner of given id: %1$s fetched successfully", user.getId()));
            response.setStatus(HttpStatus.OK.value());
            response.setContent(allWorkOrdersAndMooringDueForServiceResponse);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchCompletedWorkOrdersWithPendingPayApproval(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request, final String payStatus) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            final WorkOrderPayStatus workOrderPayStatus = workOrderPayStatusRepository.findByStatus(payStatus).orElseThrow(() -> new ResourceNotFoundException(String.format("No work order pay status found with status name as %1$s", payStatus)));

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            final List<WorkOrder> workOrderList = workOrderRepository.findAllWorkOrderWithPayStatus((null == searchText) ? "" : searchText, user.getId(), AppConstants.BooleanStringConst.YES, workOrderPayStatus.getId());

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), workOrderList.size());

            List<WorkOrder> paginatedWorkOrder;
            if (start > workOrderList.size()) {
                paginatedWorkOrder = new ArrayList<>();
            } else {
                paginatedWorkOrder = workOrderList.subList(start, end);
            }

            final List<WorkOrderResponseDto> workOrderResponseDtoList = paginatedWorkOrder
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
                        if (null != workOrder.getWorkOrderPayStatus())
                            workOrderResponseDto.setWorkOrderPayStatusDto(workOrderPayStatusMapper.toDto(WorkOrderPayStatusDto.builder().build(), workOrder.getWorkOrderPayStatus()));
                        if (null != workOrder.getDueDate())
                            workOrderResponseDto.setDueDate(DateUtil.dateToString(workOrder.getDueDate()));
                        if (null != workOrder.getScheduledDate())
                            workOrderResponseDto.setScheduledDate(DateUtil.dateToString(workOrder.getScheduledDate()));
                        if (null != workOrder.getImageList() && !workOrder.getImageList().isEmpty()) {
                            workOrderResponseDto.setImageDtoList(workOrder.getImageList()
                                    .stream()
                                    .map(image -> imageMapper.toDto(ImageDto.builder().build(), image))
                                    .toList());
                        }
                        if (null != workOrder.getCompletedDate()) {
                            workOrderResponseDto.setCompletedDate(DateUtil.dateToString(workOrder.getCompletedDate()));
                        }
                        return workOrderResponseDto;
                    })
                    .collect(Collectors.toList());

            response.setTotalSize(workOrderList.size());
            response.setCurrentSize(paginatedWorkOrder.size());
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
    public BasicRestResponse approveWorkOrder(Integer id, HttpServletRequest request, Double invoiceAmount) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            WorkOrder workOrder = workOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("No work order found with the given id: %1$s", id)));

            if (null == workOrder.getCustomerOwnerUser())
                throw new RuntimeException(String.format("Work order with the id: %1$s is not associated with any customer owner", id));
            if (ObjectUtils.notEqual(workOrder.getCustomerOwnerUser().getId(), user.getId()))
                throw new RuntimeException(String.format("Work order with the id: %1$s is associated with other customer owner", id));

            if (null == workOrder.getWorkOrderStatus() || null == workOrder.getWorkOrderStatus().getStatus())
                throw new RuntimeException(String.format("Work order  with the given id: %1$s doesn't contain any status", id));
            if (null == workOrder.getWorkOrderPayStatus() || null == workOrder.getWorkOrderPayStatus().getStatus())
                throw new RuntimeException(String.format("Work order  with the given id: %1$s doesn't contain any pay status", id));

            if (!StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.COMPLETED))
                throw new RuntimeException(String.format("Work order with the given id: %1$s is in %2$s status", id, workOrder.getWorkOrderStatus().getStatus()));
            if (null == workOrder.getWorkOrderPayStatus() || null == workOrder.getWorkOrderPayStatus().getStatus())
                throw new RuntimeException(String.format("Work order with the given id: %1$s doesn't consist of pay status", id));
            if (!StringUtils.equals(workOrder.getWorkOrderPayStatus().getStatus(), AppConstants.WorkOrderPayStatusConstants.NOACTION))
                throw new RuntimeException(String.format("Work order with the given id: %1$s has already gone through an action", id));

            WorkOrderInvoiceStatus workOrderInvoiceStatus = workOrderInvoiceStatusRepository.findByStatus(AppConstants.WorkOrderInvoiceStatusConstants.PENDING)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order invoice status found with the status as: %1$s", AppConstants.WorkOrderInvoiceStatusConstants.PENDING)));

            WorkOrderPayStatus workOrderPayStatus = workOrderPayStatusRepository.findByStatus(AppConstants.WorkOrderPayStatusConstants.APPROVED)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order pay status found with the status as %1$s", AppConstants.WorkOrderPayStatusConstants.APPROVED)));

            workOrder.setWorkOrderPayStatus(workOrderPayStatus);
            workOrderRepository.save(workOrder);

            WorkOrderInvoice workOrderInvoice = WorkOrderInvoice.builder().build();
            workOrderInvoice.setCreationDate(new Date(System.currentTimeMillis()));
            workOrderInvoice.setLastModifiedDate(new Date(System.currentTimeMillis()));
            workOrderInvoice.setInvoiceAmount(invoiceAmount);
            workOrderInvoice.setWorkOrder(workOrder);
            workOrderInvoice.setWorkOrderInvoiceStatus(workOrderInvoiceStatus);
            workOrderInvoice.setCustomerOwnerUser(user);
            workOrderInvoice.setPaymentList(new ArrayList<>());

            final WorkOrderInvoice savedWorkOrderInvoice = workOrderInvoiceRepository.save(workOrderInvoice);

            response.setMessage("Work order approved and it's invoice saved successfully!");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse denyWorkOrder(final Integer id, final HttpServletRequest request, final String reportProblem) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            WorkOrder workOrder = workOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("No work order found with the given id: %1$s", id)));

            if (null == workOrder.getCustomerOwnerUser())
                throw new RuntimeException(String.format("Work order with the id: %1$s is not associated with any customer owner", id));
            if (ObjectUtils.notEqual(workOrder.getCustomerOwnerUser().getId(), user.getId()))
                throw new RuntimeException(String.format("Work order with the id: %1$s is associated with other customer owner", id));

            if (null == workOrder.getWorkOrderStatus() || null == workOrder.getWorkOrderStatus().getStatus())
                throw new RuntimeException(String.format("Work order  with the given id: %1$s doesn't contain any status", id));
            if (null == workOrder.getWorkOrderPayStatus() || null == workOrder.getWorkOrderPayStatus().getStatus())
                throw new RuntimeException(String.format("Work order  with the given id: %1$s doesn't contain any pay status", id));

            if (!StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.COMPLETED))
                throw new RuntimeException(String.format("Work order with the given id: %1$s is in %2$s status", id, workOrder.getWorkOrderStatus().getStatus()));
            if (null == workOrder.getWorkOrderPayStatus() || null == workOrder.getWorkOrderPayStatus().getStatus())
                throw new RuntimeException(String.format("Work order with the given id: %1$s doesn't consist of pay status", id));
            if (!StringUtils.equals(workOrder.getWorkOrderPayStatus().getStatus(), AppConstants.WorkOrderPayStatusConstants.NOACTION))
                throw new RuntimeException(String.format("Work order with the given id: %1$s has already gone through an action", id));

            if (null != workOrder.getWorkOrderInvoice())
                throw new RuntimeException(String.format("Work order with the given id: %1$s already contains an invoice with the id: %2$s", workOrder.getId(), workOrder.getWorkOrderInvoice().getId()));

            WorkOrderStatus workOrderStatus = workOrderStatusRepository.findByStatus(AppConstants.WorkOrderStatusConstants.DENIED)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order status found with the given status as %1$s", AppConstants.WorkOrderStatusConstants.DENIED)));
            workOrder.setWorkOrderStatus(workOrderStatus);

            WorkOrderPayStatus workOrderPayStatus = workOrderPayStatusRepository.findByStatus(AppConstants.WorkOrderPayStatusConstants.NOACTION)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order pay status found with the status as %1$s", AppConstants.WorkOrderPayStatusConstants.NOACTION)));
            workOrder.setWorkOrderPayStatus(workOrderPayStatus);

            workOrder.setProblem(reportProblem);

            workOrderRepository.save(workOrder);

            response.setMessage("Work order denied successfully!");
            response.setContent(workOrderMapper.mapToWorkOrderDto(WorkOrderDto.builder().build(), workOrder));
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchWorkOrderInvoice(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the work order invoices in the database");

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthorityForTechnician(customerOwnerId);

            final List<WorkOrderInvoice> workOrderInvoiceList = workOrderInvoiceRepository.findAll((null == searchText) ? "" : searchText, user.getId());

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), workOrderInvoiceList.size());

            List<WorkOrderInvoice> paginatedWorkOrderInvoiceList;
            if (start > workOrderInvoiceList.size()) paginatedWorkOrderInvoiceList = new ArrayList<>();
            else paginatedWorkOrderInvoiceList = workOrderInvoiceList.subList(start, end);

            final List<WorkOrderInvoiceResponseDto> workOrderInvoiceResponseDtoList = paginatedWorkOrderInvoiceList
                    .stream()
                    .map(workOrderInvoice -> {
                        WorkOrderInvoiceResponseDto workOrderInvoiceResponseDto = workOrderInvoiceMapper.mapToWorkOrderInvoiceResponseDto(WorkOrderInvoiceResponseDto.builder().build(), workOrderInvoice);

                        if (null != workOrderInvoice.getCreationDate())
                            workOrderInvoiceResponseDto.setInvoiceDate(DateUtil.dateToString(workOrderInvoice.getCreationDate()));
                        if (null != workOrderInvoice.getLastModifiedDate())
                            workOrderInvoiceResponseDto.setLastContactTime(DateUtil.dateToString(workOrderInvoice.getLastModifiedDate()));

                        WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrderInvoice.getWorkOrder());
                        if (null != workOrderInvoice.getWorkOrder().getMooring())
                            workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrderInvoice.getWorkOrder().getMooring()));
                        if (null != workOrderInvoice.getWorkOrder().getMooring() && null != workOrderInvoice.getWorkOrder().getMooring().getCustomer())
                            workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrderInvoice.getWorkOrder().getMooring().getCustomer()));
                        if (null != workOrderInvoice.getWorkOrder().getMooring() && null != workOrderInvoice.getWorkOrder().getMooring().getBoatyard())
                            workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrderInvoice.getWorkOrder().getMooring().getBoatyard()));
                        if (null != workOrderInvoice.getWorkOrder().getCustomerOwnerUser())
                            workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrderInvoice.getWorkOrder().getCustomerOwnerUser()));
                        if (null != workOrderInvoice.getWorkOrder().getTechnicianUser())
                            workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrderInvoice.getWorkOrder().getTechnicianUser()));
                        if (null != workOrderInvoice.getWorkOrder().getWorkOrderStatus())
                            workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrderInvoice.getWorkOrder().getWorkOrderStatus()));
                        if (null != workOrderInvoice.getWorkOrder().getWorkOrderPayStatus())
                            workOrderResponseDto.setWorkOrderPayStatusDto(workOrderPayStatusMapper.toDto(WorkOrderPayStatusDto.builder().build(), workOrderInvoice.getWorkOrder().getWorkOrderPayStatus()));
                        if (null != workOrderInvoice.getWorkOrder().getDueDate())
                            workOrderResponseDto.setDueDate(DateUtil.dateToString(workOrderInvoice.getWorkOrder().getDueDate()));
                        if (null != workOrderInvoice.getWorkOrder().getScheduledDate())
                            workOrderResponseDto.setScheduledDate(DateUtil.dateToString(workOrderInvoice.getWorkOrder().getScheduledDate()));
                        if (null != workOrderInvoice.getWorkOrder().getCompletedDate())
                            workOrderResponseDto.setCompletedDate(DateUtil.dateToString(workOrderInvoice.getWorkOrder().getCompletedDate()));

                        workOrderInvoiceResponseDto.setWorkOrderResponseDto(workOrderResponseDto);
                        workOrderInvoiceResponseDto.setWorkOrderInvoiceStatusDto(workOrderInvoiceStatusMapper.toDto(WorkOrderInvoiceStatusDto.builder().build(), workOrderInvoice.getWorkOrderInvoiceStatus()));

                        List<Payment> paymentList = paymentRepository.findPaymentsByWorkOrderInvoice(workOrderInvoice.getId(), workOrderInvoice.getCustomerOwnerUser().getId());

                        if (paymentList != null && !paymentList.isEmpty()) {
                            List<PaymentResponseDto> paymentResponseDtoList = paymentList.stream()
                                    .map(payment -> {
                                        PaymentResponseDto paymentResponseDto = paymentMapper.mapToResponseDto(PaymentResponseDto.builder().build(), payment);
                                        paymentResponseDto.setPaymentTypeDto(paymentTypeMapper.mapToDto(PaymentTypeDto.builder().build(), payment.getPaymentType()));
                                        return paymentResponseDto;
                                    })
                                    .toList();
                            workOrderInvoiceResponseDto.setPaymentResponseDtoList(paymentResponseDtoList);
                        }

                        return workOrderInvoiceResponseDto;
                    })
                    .toList();

            response.setTotalSize(workOrderInvoiceList.size());
            response.setCurrentSize(workOrderInvoiceResponseDtoList.size());
            response.setMessage("All work orders invoices fetched successfully.");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderInvoiceResponseDtoList);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private WorkOrderResponseDto performSave(final WorkOrderRequestDto workOrderRequestDto, final WorkOrder workOrder, final Integer workOrderId, final HttpServletRequest request) {
        try {
            if (null == workOrderId) workOrder.setLastModifiedDate(new Date(System.currentTimeMillis()));

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            User user;

            if(StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.TECHNICIAN)) {
                User technicianUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No technician found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(technicianUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No customer owner found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

            workOrder.setCustomerOwnerUser(user);

            workOrderMapper.mapToWorkOrder(workOrder, workOrderRequestDto);

            if (null == workOrderId) {
                final String workOrderNumber = createWorkOrderNumber();
                workOrder.setWorkOrderNumber(workOrderNumber);
                workOrder.setCreationDate(new Date(System.currentTimeMillis()));
            }



            if (null != workOrderRequestDto.getImageRequestDtoList() && !workOrderRequestDto.getImageRequestDtoList().isEmpty()) {
                List<Image> imageList = new ArrayList<>();
                if (null != workOrder.getImageList() && !workOrder.getImageList().isEmpty())
                    imageList = workOrder.getImageList();
                for (ImageRequestDto imageRequestDto : workOrderRequestDto.getImageRequestDtoList()) {
                    Image image = imageMapper.toEntity(Image.builder().build(), imageRequestDto);
                    if(null != imageRequestDto.getImageData()) image.setImageData(ImageUtils.validateEncodedString(imageRequestDto.getImageData()));
                    image.setCreationDate(new Date(System.currentTimeMillis()));
                    image.setLastModifiedDate(new Date(System.currentTimeMillis()));
                    imageList.add(image);
                }

                workOrder.setImageList(imageList);
            }

            if (null != workOrderRequestDto.getFormRequestDtoList() && !workOrderRequestDto.getFormRequestDtoList().isEmpty()) {
                List<Form> dbSavedForm = (null != workOrder.getFormList()) ? workOrder.getFormList() : new ArrayList<>();
                List<Form> savedForm = workOrderRequestDto.getFormRequestDtoList()
                        .stream()
                        .map(formRequestDto -> {
                            Form form = Form.builder().build();
                            if (null == formRequestDto.getFormName())
                                throw new RuntimeException("Form name cannot be blank during save");
                            if (null == formRequestDto.getFileName())
                                throw new RuntimeException("File name cannot be blank during save");
                            form.setCreationDate(new Date(System.currentTimeMillis()));
                            form.setCreatedBy(user.getFirstName() + " " + user.getLastName());
                            form.setLastModifiedDate(new Date(System.currentTimeMillis()));

                            formMapper.toEntity(form, formRequestDto);

                            if (null != formRequestDto.getEncodedFormData()) {
                                byte[] formData = PDFUtils.isPdfFile(formRequestDto.getEncodedFormData());
                                form.setFormData(formData);
                            } else {
                                throw new RuntimeException("Form data cannot be null during save");
                            }

                            form.setUser(user);
                            form.setWorkOrder(workOrder);

                            return form;
                        }).toList();

                dbSavedForm.addAll(savedForm);
                workOrder.setFormList(dbSavedForm);
            }

            if (null != workOrderRequestDto.getVoiceMEMORequestDtoList() && !workOrderRequestDto.getVoiceMEMORequestDtoList().isEmpty()) {
                List<VoiceMEMO> dbSavedVoiceMEMO = (null != workOrder.getVoiceMEMOList()) ? workOrder.getVoiceMEMOList() : new ArrayList<>();
                List<VoiceMEMO> savedVoiceMEMO = workOrderRequestDto.getVoiceMEMORequestDtoList()
                        .stream()
                        .map(voiceMEMORequestDto -> {
                            VoiceMEMO voiceMEMO = VoiceMEMO.builder().build();
                            if (null == voiceMEMORequestDto.getName())
                                throw new RuntimeException("Voice MEMO name cannot be blank during save");

                            voiceMEMO.setCreationDate(new Date(System.currentTimeMillis()));
                            voiceMEMO.setCreatedBy(user.getFirstName() + " " + user.getLastName());
                            voiceMEMO.setLastModifiedDate(new Date(System.currentTimeMillis()));

                            voiceMEMOMapper.toEntity(voiceMEMO, voiceMEMORequestDto);

                            if (null != voiceMEMORequestDto.getEncodedData()) {
                                byte[] data = PDFUtils.isPdfFile(voiceMEMORequestDto.getEncodedData());
                                voiceMEMO.setData(data);
                            } else {
                                throw new RuntimeException("Data cannot be null during save");
                            }

                            voiceMEMO.setUser(user);
                            voiceMEMO.setWorkOrder(workOrder);

                            return voiceMEMO;
                        }).toList();

                dbSavedVoiceMEMO.addAll(savedVoiceMEMO);
                workOrder.setVoiceMEMOList(dbSavedVoiceMEMO);
            }

            final LocalDate currentDate = LocalDate.now();

            if (null == workOrderRequestDto.getScheduledDate() && null == workOrderRequestDto.getDueDate()) {
                if (null == workOrderId)
                    throw new RuntimeException(String.format("Due date and Schedule date cannot be null during save"));
            } else if (null == workOrderRequestDto.getDueDate()) {
                if (null == workOrderId)
                    throw new RuntimeException(String.format("Due date cannot be null during saved"));
                final Date savedScheduleDate = workOrder.getScheduledDate();
                final Date givenScheduleDate = DateUtil.stringToDate(workOrderRequestDto.getScheduledDate());

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
                final Date givenDueDate = DateUtil.stringToDate(workOrderRequestDto.getDueDate());

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

                final Date givenScheduleDate = DateUtil.stringToDate(workOrderRequestDto.getScheduledDate());
                final Date givenDueDate = DateUtil.stringToDate(workOrderRequestDto.getDueDate());

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

                if (null == workOrderId && (StringUtils.equals(workOrderStatus.getStatus(), AppConstants.WorkOrderStatusConstants.CLOSE)
                        || StringUtils.equals(workOrderStatus.getStatus(), AppConstants.WorkOrderStatusConstants.COMPLETED)))
                    throw new RuntimeException(String.format("New work orders cannot be saved with status as %1$s", workOrderStatus.getStatus()));

                if (StringUtils.equals(workOrderStatus.getStatus(), AppConstants.WorkOrderStatusConstants.COMPLETED)) {
                    if (null != workOrder.getCompletedDate()) {
                        if (workOrder.getCompletedDate().after(new Date()))
                            throw new RuntimeException(String.format("Completed date was after today date: %1$s", new Date()));
                    }

                    final WorkOrderPayStatus workOrderPayStatus = workOrderPayStatusRepository.findByStatus(AppConstants.WorkOrderPayStatusConstants.NOACTION)
                            .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order pay status found with the status as: %1$s", AppConstants.WorkOrderPayStatusConstants.NOACTION)));
                    workOrder.setWorkOrderPayStatus(workOrderPayStatus);

                    workOrder.setCompletedDate(new Date());
                } else {
                    if (null != workOrder.getCompletedDate()) workOrder.setCompletedDate(null);
                }
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

                if(null != workOrderRequestDto.getMooringStatusId()) {
                    final MooringStatus mooringStatus = mooringStatusRepository.findById(workOrderRequestDto.getMooringStatusId())
                            .orElseThrow(() -> new ResourceNotFoundException(String.format("No mooring status found with the given id: %1$s", workOrderRequestDto.getMooringStatusId())));

                    mooring.setMooringStatus(mooringStatus);
                }

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

            final WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), savedWorkOrder);
            if (null != savedWorkOrder.getMooring())
                workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), savedWorkOrder.getMooring()));
            if (null != savedWorkOrder.getMooring() && null != savedWorkOrder.getMooring().getCustomer())
                workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), savedWorkOrder.getMooring().getCustomer()));
            if (null != savedWorkOrder.getMooring() && null != savedWorkOrder.getMooring().getBoatyard())
                workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), savedWorkOrder.getMooring().getBoatyard()));
            if (null != savedWorkOrder.getCustomerOwnerUser())
                workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), savedWorkOrder.getCustomerOwnerUser()));
            if (null != savedWorkOrder.getTechnicianUser())
                workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), savedWorkOrder.getTechnicianUser()));
            if (null != savedWorkOrder.getWorkOrderStatus())
                workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), savedWorkOrder.getWorkOrderStatus()));
            if (null != savedWorkOrder.getDueDate())
                workOrderResponseDto.setDueDate(DateUtil.dateToString(savedWorkOrder.getDueDate()));
            if (null != savedWorkOrder.getScheduledDate())
                workOrderResponseDto.setScheduledDate(DateUtil.dateToString(savedWorkOrder.getScheduledDate()));
            if (null != savedWorkOrder.getCompletedDate())
                workOrderResponseDto.setCompletedDate(DateUtil.dateToString(savedWorkOrder.getCompletedDate()));

            return workOrderResponseDto;
        } catch (
                Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException(e.getMessage(), e);
        }

    }

    private List<MooringDueServiceResponseDto> getMooringDueServiceResponseDtoList(final List<WorkOrder> workOrderList) {

        LocalDate currentDate = LocalDate.now();
        LocalDate dateAfter31Days = currentDate.plusDays(31);

        HashMap<String, MooringDueServiceResponseDto> mooringDueServiceResponseDtoHashMap = new HashMap<>();

        for (WorkOrder workOrder : workOrderList) {
            if (null == workOrder.getMooring())
                throw new RuntimeException(String.format("No mooring found for the work order with the id: %1$s", workOrder.getId()));
            MooringDueServiceResponseDto mooringDueServiceResponseDto = MooringDueServiceResponseDto.builder().build();

            if (null == workOrder.getMooring().getCustomer())
                throw new RuntimeException(String.format("No customer found for the work order with the id: %1$s", workOrder.getId()));

            if (null == workOrder.getMooring().getBoatyard())
                throw new RuntimeException(String.format("No boatyard found for the work order with the id: %1$s", workOrder.getId()));

            final Mooring mooring = workOrder.getMooring();
            final Customer customer = workOrder.getMooring().getCustomer();
            final Boatyard boatyard = workOrder.getMooring().getBoatyard();

            mooringMapper.mapToMooringDueServiceResponseDto(mooringDueServiceResponseDto, mooring);

            mooringDueServiceResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), customer));
            mooringDueServiceResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), boatyard));

            Optional<MooringDueServiceStatus> optionalMooringDueServiceStatus;

            final MooringDueServiceStatus mooringDueServiceStatus;
            final MooringDueServiceStatusDto mooringDueServiceStatusDto;

            if (workOrder.getWorkOrderStatus().getStatus().equals(AppConstants.WorkOrderStatusConstants.CLOSE)) {

                if(mooringDueServiceResponseDto.getMooringDueServiceStatusDto() != null) continue;

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

                if (null != mooringDueServiceResponseDtoFromMap.getMooringServiceDate()) {
                    String mooringDueServiceDateStr = mooringDueServiceResponseDtoFromMap.getMooringServiceDate();
                    LocalDate mooringDueServiceDate = DateUtil.stringToLocalDate(mooringDueServiceDateStr);

                    if(mooringDueServiceDate.isBefore(dateAfter31Days)) mooringDueServiceResponseDtoFromMap.setUnder30(true);

                    if (null != workOrder.getDueDate()) {
                        Date savedDueDate = workOrder.getDueDate();
                        Date mooringServiceDate = DateUtil.stringToDate(mooringDueServiceResponseDtoFromMap.getMooringServiceDate());
                        if (savedDueDate.before(mooringServiceDate)) {
                            mooringDueServiceResponseDtoFromMap.setMooringServiceDate(DateUtil.dateToString(workOrder.getDueDate()));
                        }
                    } else {
                        throw new RuntimeException(String.format("Due date is null for work order of id: %1$s", workOrder.getId()));
                    }
                } else {
                    throw new RuntimeException(String.format("Mooring service date is null for mooring of id: %1$s", mooringDueServiceResponseDtoFromMap.getId()));
                }
            } else {
                if (null == workOrder.getDueDate())
                    throw new RuntimeException(String.format("Due date is null for work order of id: %1$s", workOrder.getId()));

                mooringDueServiceResponseDto.setMooringServiceDate(DateUtil.dateToString(workOrder.getDueDate()));

                String mooringDueServiceDateStr = mooringDueServiceResponseDto.getMooringServiceDate();
                LocalDate mooringDueServiceDate = DateUtil.stringToLocalDate(mooringDueServiceDateStr);

                if(mooringDueServiceDate.isBefore(dateAfter31Days)) mooringDueServiceResponseDto.setUnder30(true);

                if (null != workOrder.getMooring()) {
                    if (null != workOrder.getMooring().getInstallBottomChainDate())
                        mooringDueServiceResponseDto.setInstallBottomChainDate(DateUtil.dateToString(workOrder.getMooring().getInstallBottomChainDate()));
                    if (null != workOrder.getMooring().getInstallTopChainDate())
                        mooringDueServiceResponseDto.setInstallTopChainDate(DateUtil.dateToString(workOrder.getMooring().getInstallTopChainDate()));
                    if (null != workOrder.getMooring().getInstallConditionOfEyeDate())
                        mooringDueServiceResponseDto.setInstallConditionOfEyeDate(DateUtil.dateToString(workOrder.getMooring().getInstallConditionOfEyeDate()));
                    if (null != workOrder.getMooring().getInspectionDate())
                        mooringDueServiceResponseDto.setInspectionDate(DateUtil.dateToString(workOrder.getMooring().getInspectionDate()));
                    if (null != workOrder.getMooring().getServiceArea())
                        mooringDueServiceResponseDto.setServiceAreaResponseDto(serviceAreaMapper.mapToResponseDto(ServiceAreaResponseDto.builder().build(), workOrder.getMooring().getServiceArea()));
                    mooringDueServiceResponseDtoHashMap.put(mooringDueServiceResponseDto.getMooringNumber(), mooringDueServiceResponseDto);
                }
            }

        }
        Collection<MooringDueServiceResponseDto> mooringDueServiceResponseDtoCollection = mooringDueServiceResponseDtoHashMap.values();
        return new ArrayList<>(mooringDueServiceResponseDtoCollection);
    }

    public String createWorkOrderNumber() {
        final StringBuilder workOrderNumber = new StringBuilder();
        workOrderNumber.append("WOR");


        int randomThreeDigitNumber = 100 + (int) (Math.random() * 900);
        String randomThreeDigitNumberStr = Integer.toString(randomThreeDigitNumber);
        workOrderNumber.append(randomThreeDigitNumberStr);

        final String workOrderNumberStr = workOrderNumber.toString();

        Optional<WorkOrder> optionalWorkOrder = workOrderRepository.findByWorkOrderNumber(workOrderNumberStr);
        if (optionalWorkOrder.isPresent()) createWorkOrderNumber();
        return workOrderNumberStr;
    }
}
