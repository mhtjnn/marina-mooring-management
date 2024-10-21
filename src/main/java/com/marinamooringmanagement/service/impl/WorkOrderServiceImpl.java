package com.marinamooringmanagement.service.impl;

import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.AuthenticationException;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.services.DataService;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;
import com.marinamooringmanagement.client.OAuth2PlatformClientFactory;
import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.MathException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.helper.QBOServiceHelper;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.mapper.metadata.*;
import com.marinamooringmanagement.model.dto.*;
import com.marinamooringmanagement.model.dto.metadata.*;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.entity.Payment;
import com.marinamooringmanagement.model.entity.QBO.QBOUser;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.metadata.*;
import com.marinamooringmanagement.model.request.*;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.repositories.QBO.QBOUserRepository;
import com.marinamooringmanagement.repositories.metadata.MooringDueServiceStatusRepository;
import com.marinamooringmanagement.repositories.metadata.MooringStatusRepository;
import com.marinamooringmanagement.repositories.metadata.WorkOrderInvoiceStatusRepository;
import com.marinamooringmanagement.repositories.metadata.WorkOrderStatusRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.NotificationService;
import com.marinamooringmanagement.service.WorkOrderService;
import com.marinamooringmanagement.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private FormRepository formRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private VendorMapper vendorMapper;

    @Autowired
    private QBOServiceHelper helper;

    @Autowired
    private QBOUserRepository qboUserRepository;

    @Autowired
    private OAuth2PlatformClientFactory factory;

    @Autowired
    private VoiceMEMORepository voiceMEMORepository;

    @Autowired
    private VoiceMEMOMapper voiceMEMOMapper;

    @Autowired
    private MooringStatusRepository mooringStatusRepository;

    @Autowired
    private StateMapper stateMapper;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BoatyardRepository boatyardRepository;

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
                        if (null != workOrder.getCustomer()) {
                            CustomerResponseDto customerResponseDto = customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getCustomer());
                            if (null != workOrder.getCustomer().getState()) {
                                customerResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), workOrder.getCustomer().getState()));
                            }
                            if (null != workOrder.getCustomer().getCountry()) {
                                customerResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), workOrder.getCustomer().getCountry()));
                            }
                            workOrderResponseDto.setCustomerResponseDto(customerResponseDto);
                        }
                        if (null != workOrder.getBoatyard()) {
                            final BoatyardResponseDto boatyardResponseDto = boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getBoatyard());
                            if (null != workOrder.getBoatyard().getState() && null != workOrder.getBoatyard().getState().getId()) {
                                StateResponseDto stateResponseDto = stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), workOrder.getBoatyard().getState());
                                boatyardResponseDto.setStateResponseDto(stateResponseDto);
                            }
                            if (null != workOrder.getBoatyard().getCountry() && null != workOrder.getBoatyard().getCountry().getId()) {
                                CountryResponseDto countryResponseDto = countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), workOrder.getBoatyard().getCountry());
                                boatyardResponseDto.setCountryResponseDto(countryResponseDto);
                            }
                            workOrderResponseDto.setBoatyardResponseDto(boatyardResponseDto);

                        }
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

                        List<Image> imageList = imageRepository.findImagesByWorkOrderIdWithoutData(workOrder.getId());
                        List<Form> formList = formRepository.findFormsByWorkOrderIdWithoutData(workOrder.getId());
                        List<Inventory> inventoryList = inventoryRepository.findInventoriesByWorkOrder(workOrder.getId());
                        List<VoiceMEMO> voiceMEMOList = voiceMEMORepository.findVoiceMEMOsByWorkOrderIdWithoutData(workOrder.getId());

                        if (null != imageList && !imageList.isEmpty()) {
                            workOrderResponseDto.setImageResponseDtoList(imageList
                                    .stream()
                                    .map(image -> imageMapper.toResponseDto(ImageResponseDto.builder().build(), image))
                                    .toList());
                        }
                        if (null != formList && !formList.isEmpty()) {
                            workOrderResponseDto.setFormResponseDtoList(formList
                                    .stream()
                                    .map(form -> formMapper.toResponseDto(FormResponseDto.builder().build(), form))
                                    .toList());
                        }
                        if (null != inventoryList && !inventoryList.isEmpty()) {
                            workOrderResponseDto.setInventoryResponseDtoList(inventoryList
                                    .stream()
                                    .map(inventory -> {
                                        final InventoryResponseDto inventoryResponseDto = inventoryMapper.mapToInventoryResponseDto(InventoryResponseDto.builder().build(), inventory);
                                        VendorResponseDto vendorResponseDto = vendorMapper.mapToVendorResponseDto(VendorResponseDto.builder().build(), inventory.getVendor());
                                        inventoryResponseDto.setVendorResponseDto(vendorResponseDto);
                                        return inventoryResponseDto;
                                    })
                                    .toList());
                        }
                        if (null != voiceMEMOList && !voiceMEMOList.isEmpty()) {
                            workOrderResponseDto.setVoiceMEMOResponseDtoList(voiceMEMOList
                                    .stream()
                                    .map(voiceMEMO -> voiceMEMOMapper.toResponseDto(VoiceMEMOResponseDto.builder().build(), voiceMEMO))
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
    @Transactional
    public BasicRestResponse saveWorkOrder(WorkOrderRequestDto workOrderRequestDto, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to save the work order in the database");
            final WorkOrder workOrder = WorkOrder.builder().build();

            response.setContent(performSave(workOrderRequestDto, workOrder, null, request));

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
    @Transactional
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
            response.setContent(performSave(workOrderRequestDto, workOrder, workOrderId, request));
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
                        if (null != workOrder.getCustomer())
                            workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getCustomer()));
                        if (null != workOrder.getBoatyard())
                            workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getBoatyard()));
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
                                    if (null != workOrder.getCustomer()) {
                                        workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getCustomer()));
                                        if (null != workOrder.getCustomer().getFirstName()
                                                && null != workOrder.getCustomer().getLastName())
                                            mooringResponseDto.setCustomerName(
                                                    workOrder.getCustomer().getFirstName() + " " + workOrder.getCustomer().getLastName()
                                            );
                                    }
                                }
                                if (null != workOrder.getBoatyard())
                                    workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getBoatyard()));
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
            final User user;
            if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.FINANCE)) {
                final User financeUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No finance user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(financeUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No customer owner user found with the given id: %1$s", financeUser.getCustomerOwnerId())));
            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

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
                        if (null != workOrder.getCustomer())
                            workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getCustomer()));
                        if (null != workOrder.getBoatyard())
                            workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getBoatyard()));
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
                        if (null != workOrder.getCompletedDate()) {
                            workOrderResponseDto.setCompletedDate(DateUtil.dateToString(workOrder.getCompletedDate()));
                        }
                        return workOrderResponseDto;
                    })
                    .toList();

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
    @Transactional
    public BasicRestResponse approveWorkOrder(Integer id, HttpServletRequest request, BigDecimal invoiceAmount) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user;
            if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.FINANCE)) {
                final User financeUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No finance user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(financeUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No customer owner user found with the given id: %1$s", financeUser.getCustomerOwnerId())));
            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

            WorkOrder workOrder = workOrderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order found with the given id: %1$s", id)));

            String quickBookCustomerIdStr = null;

            final Mooring workOrderMappedMooring = workOrder.getMooring();
            if (null != workOrderMappedMooring) {
                final Customer mooringMappedCustomer = workOrderMappedMooring.getCustomer();
                if (null != mooringMappedCustomer) {
                    final QuickbookCustomer customerMappedQuickbookCustomer = mooringMappedCustomer.getQuickBookCustomer();
                    if (null != customerMappedQuickbookCustomer) {
                        quickBookCustomerIdStr = customerMappedQuickbookCustomer.getQuickbookCustomerId();
                    }
                }
            }

            if (null == quickBookCustomerIdStr)
                throw new RuntimeException(String.format("Work Order invoice with the id: %1$s is not connected with any Quickbook customer", id));

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

            workOrderInvoiceRepository.save(workOrderInvoice);

            Invoice invoice = new Invoice();
            List<Line> lineList = new ArrayList<>();

            Line line = new Line();
            line.setAmount(invoiceAmount); // Set the amount

            // Create a SalesItemLineDetail for the line
            SalesItemLineDetail salesItemLineDetail = new SalesItemLineDetail();
            ReferenceType itemRef = new ReferenceType();
            itemRef.setValue("1"); // Set the appropriate value for the item reference
            itemRef.setName("Services"); // Set the name if required

            salesItemLineDetail.setItemRef(itemRef);
            line.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);
            line.setSalesItemLineDetail(salesItemLineDetail);

            // Add the line to the list and set it in the invoice
            lineList.add(line);
            invoice.setLine(lineList);

            // Set customer reference
            ReferenceType customerRef = new ReferenceType();
            customerRef.setValue(quickBookCustomerIdStr);
            invoice.setCustomerRef(customerRef);

            final QBOUser qboUser;
            if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.ADMINISTRATOR)) {
                qboUser = qboUserRepository.findQBOUserByEmail(LoggedInUserUtil.getLoggedInUserEmail())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No QBO user found with the given email: %1$s", user.getEmail())));
            } else {
                qboUser = qboUserRepository.findQBOUserByEmail(user.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No QBO user found with the given email: %1$s", user.getEmail())));
            }

            String realmId = qboUser.getRealmId();
            if (StringUtils.isEmpty(realmId)) {
                throw new RuntimeException("No realm ID. QBO calls only work if the accounting scope was passed!");
            }
            String accessToken = qboUser.getAccessToken();

            // Save the payment using DataService
            try {
                DataService dataService = helper.getDataService(realmId, accessToken);

                Invoice savedInvoice = dataService.add(invoice);

                response.setContent(savedInvoice);
                response.setMessage("Work order approved and it's invoice saved successfully!");
                response.setStatus(HttpStatus.OK.value());

            } /*
             * Handle 401 status code -
             * If a 401 response is received, refresh tokens should be used to get a new access token,
             * and the API call should be tried again.
             */ catch (InvalidTokenException e) {
                log.error("Error while calling executeQuery :: " + e.getMessage());

                //refresh tokens
                log.info("received 401 during companyinfo call, refreshing tokens now");
                OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
                String refreshToken = qboUser.getRefreshToken();

                try {
                    BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);

                    qboUser.setLastModifiedDate(new Date(System.currentTimeMillis()));
                    qboUser.setAccessToken(bearerTokenResponse.getAccessToken());
                    qboUser.setRefreshToken(bearerTokenResponse.getRefreshToken());

                    qboUserRepository.save(qboUser);

                    //call company info again using new tokens
                    log.info("Saving payment using new token");
                    DataService dataService = helper.getDataService(realmId, accessToken);

                    Invoice savedInvoice = dataService.add(invoice);

                    response.setContent(savedInvoice);
                    response.setMessage("Work order approved and it's invoice saved successfully!");
                    response.setStatus(HttpStatus.OK.value());
                } catch (OAuthException e1) {
                    log.error("Error while calling bearer token :: " + e.getMessage());
                    response.setMessage("Error while calling bearer token :: " + e.getMessage());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                } catch (FMSException e1) {
                    log.error("Error while calling bearer token :: " + e.getMessage());
                    response.setMessage("Error while calling company currency :: " + e.getMessage());
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } catch (FMSException e) {
                List<com.intuit.ipp.data.Error> list = e.getErrorList();
                list.forEach(error -> log.error("Error while calling executeQuery :: " + error.getMessage()));
                response.setMessage("Error while calling executeQuery :: " + e.getMessage());
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
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

            workOrder.setReasonForDenial(reportProblem);

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
            final User user;
            if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.FINANCE)) {
                final User financeUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No finance user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(financeUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No customer owner user found with the given id: %1$s", financeUser.getCustomerOwnerId())));
            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

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

    @Override
    public BasicRestResponse fetchMooringDueForServiceForTechnician() {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            List<WorkOrder> workOrderList = workOrderRepository.findAllByTechnicianUser("", LoggedInUserUtil.getLoggedInUserID(), AppConstants.BooleanStringConst.NO);

            if (null != workOrderList && !workOrderList.isEmpty()) {
                List<MooringDueServiceResponseDto> mooringDueServiceResponseDtoList = getMooringDueServiceResponseDtoList(workOrderList);
                response.setMessage("Mooring due for service fetched successfully");
                response.setContent(mooringDueServiceResponseDtoList);
            } else {
                response.setContent(new ArrayList<>());
            }
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    @Transactional()
    public BasicRestResponse fetchWorkOrderById(Integer id, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the work orders in the database");

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthorityForTechnician(customerOwnerId);

            final Optional<WorkOrder> optionalWorkOrder;
            if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.ADMINISTRATOR)
                    || StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.CUSTOMER_OWNER)) {
                optionalWorkOrder = workOrderRepository.findWorkOrderById(id, user.getId());
            } else if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.TECHNICIAN)) {
                optionalWorkOrder = workOrderRepository.findWorkOrderByIdUsingTechnicianLogin(id, LoggedInUserUtil.getLoggedInUserID());
            } else {
                throw new RuntimeException("No authorized");
            }

            if (optionalWorkOrder.isEmpty())
                throw new ResourceNotFoundException(String.format("No work order found with the given id: %1$s", id));

            final WorkOrder workOrder = optionalWorkOrder.get();

            final WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
            if (null != workOrder.getMooring())
                workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
            if (null != workOrder.getCustomer()) {
                CustomerResponseDto customerResponseDto = customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getCustomer());
                if (null != workOrder.getCustomer().getState()) {
                    customerResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), workOrder.getCustomer().getState()));
                }
                if (null != workOrder.getCustomer().getCountry()) {
                    customerResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), workOrder.getCustomer().getCountry()));
                }
                workOrderResponseDto.setCustomerResponseDto(customerResponseDto);
            }
            if (null != workOrder.getBoatyard())
                workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getBoatyard()));
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

            List<Image> imageList = imageRepository.findByWorkOrderId(workOrder.getId());
            List<Form> formList = formRepository.findByWorkOrderId(workOrder.getId());
            List<Inventory> inventoryList = inventoryRepository.findByWorkOrderId(workOrder.getId());
            List<VoiceMEMO> voiceMEMOList = voiceMEMORepository.findByWorkOrderId(workOrder.getId());

            if (null != imageList && !imageList.isEmpty()) {
                workOrderResponseDto.setImageResponseDtoList(imageList
                        .stream()
                        .map(image -> {
                            ImageResponseDto imageResponseDto = ImageResponseDto.builder().build();
                            imageMapper.toResponseDto(imageResponseDto, image);
                            if (null != image.getImageData()) {
                                String encodedData = Base64.getEncoder().encodeToString(image.getImageData());
                                if (null != encodedData) {
                                    imageResponseDto.setEncodedData(encodedData);
                                }
                            }
                            return imageResponseDto;
                        })
                        .toList());
            }
            if (null != formList && !formList.isEmpty()) {
                workOrderResponseDto.setFormResponseDtoList(formList
                        .stream()
                        .map(form -> {
                            FormResponseDto formResponseDto = FormResponseDto.builder().build();
                            formMapper.toResponseDto(formResponseDto, form);
                            if (null != form.getFormData()) {
                                String encodedData = Base64.getEncoder().encodeToString(form.getFormData());
                                if (null != encodedData) {
                                    formResponseDto.setEncodedData(encodedData);
                                }
                            }
                            return formResponseDto;
                        })
                        .toList());
            }
            if (null != inventoryList && !inventoryList.isEmpty()) {
                workOrderResponseDto.setInventoryResponseDtoList(inventoryList
                        .stream()
                        .map(inventory -> {
                            final InventoryResponseDto inventoryResponseDto = inventoryMapper.mapToInventoryResponseDto(InventoryResponseDto.builder().build(), inventory);
                            VendorResponseDto vendorResponseDto = vendorMapper.mapToVendorResponseDto(VendorResponseDto.builder().build(), inventory.getVendor());
                            inventoryResponseDto.setVendorResponseDto(vendorResponseDto);
                            return inventoryResponseDto;
                        })
                        .toList());
            }
            if (null != voiceMEMOList && !voiceMEMOList.isEmpty()) {
                workOrderResponseDto.setVoiceMEMOResponseDtoList(voiceMEMOList
                        .stream()
                        .map(voiceMEMO -> {
                            VoiceMEMOResponseDto voiceMEMOResponseDto = VoiceMEMOResponseDto.builder().build();
                            voiceMEMOMapper.toResponseDto(voiceMEMOResponseDto, voiceMEMO);
                            if (null != voiceMEMO.getData()) {
                                String encodedData = Base64.getEncoder().encodeToString(voiceMEMO.getData());
                                voiceMEMOResponseDto.setEncodedData(encodedData);
                            }
                            return voiceMEMOResponseDto;
                        })
                        .toList());
            }

            response.setMessage(String.format("Work order by id %1$s fetched successfully.", id));
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderResponseDto);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Transactional
    private WorkOrderResponseDto performSave(final WorkOrderRequestDto workOrderRequestDto, final WorkOrder workOrder, final Integer workOrderId, final HttpServletRequest request) {

        boolean mooringStatusFlag = false;

        try {
            if (null == workOrderId) workOrder.setLastModifiedDate(new Date(System.currentTimeMillis()));

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user;

            if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.TECHNICIAN)) {
                final User technicianUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(technicianUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));
            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

            if (null != workOrder.getCustomerOwnerUser() && !workOrder.getCustomerOwnerUser().getId().equals(user.getId()))
                throw new AuthenticationException("Not authorized to preform operations on work order of different customer owner.");

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
                    if (null != imageRequestDto.getImageData())
                        image.setImageData(ImageUtils.validateEncodedString(imageRequestDto.getImageData()));
                    image.setCreationDate(new Date(System.currentTimeMillis()));
                    image.setLastModifiedDate(new Date(System.currentTimeMillis()));
                    imageList.add(image);

                    image.setWorkOrder(workOrder);
                }

                workOrder.setImageList(imageList);
            }

            // Update form list
            if (null != workOrderRequestDto.getFormRequestDtoList() && !workOrderRequestDto.getFormRequestDtoList().isEmpty()) {

                List<Integer> toDelete;
                List<Integer> savedFormIds;
                List<Form> formList;

                if (null != workOrder.getFormList()) formList = workOrder.getFormList();
                else formList = new ArrayList<>();

                if (null != workOrder.getFormList() && !workOrder.getFormList().isEmpty()) {
                    savedFormIds = formList.stream().map(Form::getId).toList();

                    toDelete = savedFormIds.stream()
                            .filter(id -> workOrderRequestDto.getFormRequestDtoList().stream()
                                    .noneMatch(formRequestDto -> null != formRequestDto.getId() && formRequestDto.getId().equals(id)))
                            .toList();

                    List<Form> toDeleteForms = formRepository.findAllById(toDelete);
                    formList.removeAll(toDeleteForms);
                }

                for (FormRequestDto formRequestDto : workOrderRequestDto.getFormRequestDtoList()) {

                    if (formRequestDto.getEncodedFormData() == null) continue;

                    Form childForm = null;

                    if (null == formRequestDto.getParentFormId()) {
                        // Handle parent form creation
                        Form parentForm = formRepository.findByIdWithoutData(formRequestDto.getId());

                        if (null == parentForm) continue;

                        for (Form savedForm : formList) {
                            if (savedForm.getParentFormId().equals(parentForm.getId())) {
                                childForm = savedForm;
                                break;
                            }
                        }

                        if (null == childForm) childForm = formMapper.toEntity(Form.builder().build(), parentForm);

                        String workOrderNumber = workOrder.getWorkOrderNumber();
                        childForm.setFormName(parentForm.getFormName() + "_" + workOrderNumber);
                        childForm.setParentFormId(parentForm.getId());
                        childForm.setWorkOrder(workOrder);
                        formList.add(childForm);
                    } else {
                        // Fetch existing child form
                        childForm = formRepository.findByIdWithoutData(formRequestDto.getId());
                    }

                    if (null != formRequestDto.getEncodedFormData()) {
                        byte[] formData = PDFUtils.isPdfFile(formRequestDto.getEncodedFormData());
                        childForm.setFormData(formData);
                    } else {
                        throw new RuntimeException("Form data cannot be null during save");
                    }
                }

                workOrder.setFormList(formList);
            }

            if (null != workOrderRequestDto.getVoiceMEMORequestDtoList() && !workOrderRequestDto.getVoiceMEMORequestDtoList().isEmpty()) {

                List<Integer> toDelete;
                List<Integer> savedVoiceMEMOIds;
                List<VoiceMEMO> voiceMEMOList;

                if (null != workOrder.getVoiceMEMOList()) voiceMEMOList = workOrder.getVoiceMEMOList();
                else voiceMEMOList = new ArrayList<>();

                if (null != workOrder.getFormList() && !workOrder.getFormList().isEmpty()) {
                    savedVoiceMEMOIds = voiceMEMOList.stream().map(VoiceMEMO::getId).toList();

                    toDelete = savedVoiceMEMOIds.stream()
                            .filter(id -> workOrderRequestDto.getVoiceMEMORequestDtoList().stream()
                                    .noneMatch(voiceMEMORequestDto -> null != voiceMEMORequestDto.getId() && voiceMEMORequestDto.getId().equals(id)))
                            .toList();

                    List<VoiceMEMO> toDeleteVoiceMEMO = voiceMEMORepository.findAllById(toDelete);
                    voiceMEMOList.removeAll(toDeleteVoiceMEMO);
                }

                for (VoiceMEMORequestDto voiceMEMORequestDto : workOrderRequestDto.getVoiceMEMORequestDtoList()) {

                    if (voiceMEMORequestDto.getEncodedData() == null) continue;

                    VoiceMEMO voiceMEMO;
                    if (null != voiceMEMORequestDto.getId()) {
                        voiceMEMO = voiceMEMORepository.findById(voiceMEMORequestDto.getId()).orElseThrow(() -> new ResourceNotFoundException(String.format("No voice MEMO found with the given id: %1$s", voiceMEMORequestDto.getId())));
                    } else {
                        voiceMEMO = VoiceMEMO.builder().build();
                        voiceMEMOMapper.toEntity(voiceMEMO, voiceMEMORequestDto);
                    }

                    byte[] decodedBytes = Base64.getDecoder().decode(voiceMEMORequestDto.getEncodedData());
                    voiceMEMO.setData(decodedBytes);
                    voiceMEMO.setWorkOrder(workOrder);
                    voiceMEMO.setUser(user);
                    voiceMEMOList.add(voiceMEMO);
                }

                workOrder.setVoiceMEMOList(voiceMEMOList);
            }

            final LocalDate currentDate = LocalDate.now();

            if (null == workOrderRequestDto.getScheduledDate() && null == workOrderRequestDto.getDueDate()) {

            } else if (null == workOrderRequestDto.getDueDate()) {

                final Date givenScheduleDate = DateUtil.stringToDate(workOrderRequestDto.getScheduledDate());
                if (workOrder.getScheduledDate() != null) {
                    final Date savedScheduleDate = workOrder.getScheduledDate();


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
                } else {
                    workOrder.setScheduledDate(givenScheduleDate);
                }
            } else if (null == workOrderRequestDto.getScheduledDate()) {

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

                    if (workOrder.getScheduledDate() != null) {
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
                }

                workOrder.setScheduledDate(givenScheduleDate);
                workOrder.setDueDate(givenDueDate);
            }

            if (null != workOrderRequestDto.getWorkOrderStatusId()) {

                log.info("Inside work order status update");

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

                // Waiting on Parts section
                if (workOrderStatus.getStatus().equals(AppConstants.WorkOrderStatusConstants.WAITING_ON_PARTS)) {
                    log.info("Inside waiting on parts work order status");
                    if (null != workOrderRequestDto.getInventoryRequestDtoList() && !workOrderRequestDto.getInventoryRequestDtoList().isEmpty()) {
                        List<Integer> toDelete;
                        List<Integer> savedInventoryIds;
                        List<Inventory> inventoryList = new ArrayList<>();
                        if (null != workOrder.getInventoryList() && !workOrder.getInventoryList().isEmpty()) {
                            log.info("Deleting work order inventory whose id is not given");
                            inventoryList = workOrder.getInventoryList();

                            savedInventoryIds = workOrder.getInventoryList().stream().map(Inventory::getId).toList();

                            toDelete = savedInventoryIds
                                    .stream()
                                    .filter(id -> workOrderRequestDto.getInventoryRequestDtoList().stream().noneMatch(inventoryRequestDto -> null != inventoryRequestDto.getId() && inventoryRequestDto.getId().equals(id)))
                                    .toList();

                            inventoryRepository.deleteAllById(toDelete);
                        }

                        if (inventoryList.isEmpty()) inventoryList = new ArrayList<>();

                        for (InventoryRequestDto inventoryRequestDto : workOrderRequestDto.getInventoryRequestDtoList()) {
                            int quantityAfterOperation;
                            log.info("Adding inventory to the inventory list");
                            final Inventory inventory = inventoryRepository.findById(inventoryRequestDto.getId())
                                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No inventory found with the given id: %1$s", inventoryRequestDto.getId())));

                            if (null == inventory.getParentInventoryId()) {
                                Inventory childInventory = null;

                                if (null != workOrder.getInventoryList()) {
                                    for (Inventory savedInventory : workOrder.getInventoryList()) {
                                        if (savedInventory.getParentInventoryId().equals(inventory.getId())) {
                                            childInventory = savedInventory;
                                            inventory.setQuantity(inventory.getQuantity() + childInventory.getQuantity());
                                            break;
                                        }
                                    }
                                }

                                if (null == childInventory)
                                    childInventory = inventoryMapper.mapToInventory(Inventory.builder().build(), inventory);

                                final String workOrderNumber = workOrder.getWorkOrderNumber();

                                childInventory.setParentInventoryId(inventory.getId());
                                childInventory.setItemName(String.format("%1$s_%2$s", inventory.getItemName(), workOrderNumber));

                                quantityAfterOperation = inventory.getQuantity() - inventoryRequestDto.getQuantity();

                                if (quantityAfterOperation < 0) {
                                    throw new MathException("Given quantity is greater than available quantity");
                                }

                                inventory.setQuantity(quantityAfterOperation);
                                childInventory.setQuantity(inventoryRequestDto.getQuantity());
                                inventoryList.add(childInventory);

                                childInventory.setWorkOrder(workOrder);

                                inventoryRepository.save(inventory);

                            } else {
                                final Inventory parentInventory = inventoryRepository.findById(inventory.getParentInventoryId())
                                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No inventory found with the given id: %1$s", inventory.getParentInventoryId())));

                                quantityAfterOperation = parentInventory.getQuantity() + inventory.getQuantity() - inventoryRequestDto.getQuantity();

                                if (quantityAfterOperation < 0) {
                                    throw new MathException("Given quantity is greater than available quantity");
                                }

                                inventory.setQuantity(inventoryRequestDto.getQuantity());
                                parentInventory.setQuantity(quantityAfterOperation);

                                inventoryRepository.save(parentInventory);

                                if (inventoryRequestDto.getQuantity() == 0) {
                                    inventory.setWorkOrder(null);
                                    inventoryList.remove(inventory);
                                    inventoryRepository.delete(inventory);
                                }
                            }

                        }

                        log.info("Setting the inventory list: {} ", inventoryList);
                        workOrder.setInventoryList(inventoryList);
                    }
                }
                workOrder.setWorkOrderStatus(workOrderStatus);
            } else {
                if (null == workOrderId)
                    throw new RuntimeException(String.format("While saving work order status cannot be null"));
            }

            if (null != workOrderRequestDto.getMooringId()) {
                final Mooring mooring = Mooring.builder().id(workOrderRequestDto.getMooringId()).build();
                workOrder.setMooring(mooring);
            } else {
                workOrder.setMooring(null);
            }

            if (null != workOrderRequestDto.getCustomerId()) {
                final Customer customer = Customer.builder().id(workOrderRequestDto.getCustomerId()).build();
                workOrder.setCustomer(customer);
            } else {
                if (null == workOrderId)
                    throw new NullPointerException("While saving work order customer cannot be null");
            }

            if (null != workOrderRequestDto.getBoatyardId()) {
                final Boatyard boatyard = Boatyard.builder().id(workOrderRequestDto.getBoatyardId()).build();
                workOrder.setBoatyard(boatyard);
            } else {
                workOrder.setBoatyard(null);
            }

            if (null != workOrderRequestDto.getMooringStatusId()) {
                if (null != workOrder.getMooring()) {

                    MooringStatus mooringStatus = mooringStatusRepository.findById(workOrderRequestDto.getMooringStatusId())
                            .orElseThrow(() -> new ResourceNotFoundException(String.format("No mooring status found with the given id: %1$s", workOrderRequestDto.getMooringStatusId())));

                    Mooring mooring = workOrder.getMooring();
                    mooring.setMooringStatus(mooringStatus);

                    mooringRepository.save(mooring);

                    mooringStatusFlag = true;
                }
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
            }

            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

            if (workOrderId == null && null != workOrder.getTechnicianUser()) {
                notificationService.createNotificationForSaveWorkOrder(savedWorkOrder);
            }

            if (mooringStatusFlag && null != workOrder.getTechnicianUser()) {
                notificationService.createNotificationForUpdateWorkOrder(savedWorkOrder);
            }

            WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), savedWorkOrder);
            if (null != savedWorkOrder.getMooring())
                workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), savedWorkOrder.getMooring()));
            if (null != savedWorkOrder.getCustomer()) {
                CustomerResponseDto customerResponseDto = customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), savedWorkOrder.getCustomer());
                if (null != savedWorkOrder.getCustomer().getState()) {
                    customerResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), savedWorkOrder.getCustomer().getState()));
                }
                if (null != savedWorkOrder.getCustomer().getCountry()) {
                    customerResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), savedWorkOrder.getCustomer().getCountry()));
                }
                workOrderResponseDto.setCustomerResponseDto(customerResponseDto);
            }
            if (null != savedWorkOrder.getBoatyard())
                workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), savedWorkOrder.getBoatyard()));
            if (null != savedWorkOrder.getCustomerOwnerUser())
                workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), savedWorkOrder.getCustomerOwnerUser()));
            if (null != savedWorkOrder.getTechnicianUser())
                workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), savedWorkOrder.getTechnicianUser()));
            if (null != savedWorkOrder.getWorkOrderStatus())
                workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), savedWorkOrder.getWorkOrderStatus()));
            if (null != savedWorkOrder.getWorkOrderPayStatus())
                workOrderResponseDto.setWorkOrderPayStatusDto(workOrderPayStatusMapper.toDto(WorkOrderPayStatusDto.builder().build(), savedWorkOrder.getWorkOrderPayStatus()));
            if (null != savedWorkOrder.getDueDate())
                workOrderResponseDto.setDueDate(DateUtil.dateToString(savedWorkOrder.getDueDate()));
            if (null != savedWorkOrder.getScheduledDate())
                workOrderResponseDto.setScheduledDate(DateUtil.dateToString(savedWorkOrder.getScheduledDate()));
            if (null != savedWorkOrder.getCompletedDate())
                workOrderResponseDto.setCompletedDate(DateUtil.dateToString(savedWorkOrder.getCompletedDate()));

            List<Image> imageList = imageRepository.findImagesByWorkOrderIdWithoutData(savedWorkOrder.getId());
            List<Form> formList = formRepository.findFormsByWorkOrderIdWithoutData(savedWorkOrder.getId());
            List<Inventory> inventoryList = inventoryRepository.findInventoriesByWorkOrder(savedWorkOrder.getId());
            List<VoiceMEMO> voiceMEMOList = voiceMEMORepository.findVoiceMEMOsByWorkOrderIdWithoutData(savedWorkOrder.getId());

            if (null != imageList && !imageList.isEmpty()) {
                workOrderResponseDto.setImageResponseDtoList(imageList
                        .stream()
                        .map(image -> imageMapper.toResponseDto(ImageResponseDto.builder().build(), image))
                        .toList());
            }
            if (null != formList && !formList.isEmpty()) {
                workOrderResponseDto.setFormResponseDtoList(formList
                        .stream()
                        .map(form -> formMapper.toResponseDto(FormResponseDto.builder().build(), form))
                        .toList());
            }
            if (null != inventoryList && !inventoryList.isEmpty()) {
                workOrderResponseDto.setInventoryResponseDtoList(inventoryList
                        .stream()
                        .map(inventory -> {
                            final InventoryResponseDto inventoryResponseDto = inventoryMapper.mapToInventoryResponseDto(InventoryResponseDto.builder().build(), inventory);
                            VendorResponseDto vendorResponseDto = vendorMapper.mapToVendorResponseDto(VendorResponseDto.builder().build(), inventory.getVendor());
                            inventoryResponseDto.setVendorResponseDto(vendorResponseDto);
                            return inventoryResponseDto;
                        })
                        .toList());
            }
            if (null != voiceMEMOList && !voiceMEMOList.isEmpty()) {
                workOrderResponseDto.setVoiceMEMOResponseDtoList(voiceMEMOList
                        .stream()
                        .map(voiceMEMO -> voiceMEMOMapper.toResponseDto(VoiceMEMOResponseDto.builder().build(), voiceMEMO))
                        .toList());
            }
            return workOrderResponseDto;

        } catch (Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException(e.getMessage(), e);
        }

    }

    private List<MooringDueServiceResponseDto> getMooringDueServiceResponseDtoList(final List<WorkOrder> workOrderList) {

        LocalDate currentDate = LocalDate.now();
        LocalDate dateAfter31Days = currentDate.plusDays(31);

        HashMap<String, MooringDueServiceResponseDto> mooringDueServiceResponseDtoHashMap = new HashMap<>();

        for (WorkOrder workOrder : workOrderList) {
            if (null == workOrder.getMooring()) continue;
            MooringDueServiceResponseDto mooringDueServiceResponseDto = MooringDueServiceResponseDto.builder().build();

            if (null != workOrder.getCustomer()) {
                final Customer customer = workOrder.getCustomer();
                mooringDueServiceResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), customer));
            }

            if (null != workOrder.getBoatyard()) {
                final Boatyard boatyard = workOrder.getBoatyard();
                mooringDueServiceResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), boatyard));
            }

            final Mooring mooring = workOrder.getMooring();

            mooringMapper.mapToMooringDueServiceResponseDto(mooringDueServiceResponseDto, mooring);

            Optional<MooringDueServiceStatus> optionalMooringDueServiceStatus;

            final MooringDueServiceStatus mooringDueServiceStatus;
            final MooringDueServiceStatusDto mooringDueServiceStatusDto;

            if (workOrder.getWorkOrderStatus().getStatus().equals(AppConstants.WorkOrderStatusConstants.CLOSE)) {

                if (mooringDueServiceResponseDto.getMooringDueServiceStatusDto() != null) continue;

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

                    if (mooringDueServiceDate.isBefore(dateAfter31Days))
                        mooringDueServiceResponseDtoFromMap.setUnder30(true);

                    if (null != workOrder.getDueDate()) {
                        Date savedDueDate = workOrder.getDueDate();
                        Date mooringServiceDate = DateUtil.stringToDate(mooringDueServiceResponseDtoFromMap.getMooringServiceDate());
                        if (savedDueDate.before(mooringServiceDate)) {
                            mooringDueServiceResponseDtoFromMap.setMooringServiceDate(DateUtil.dateToString(workOrder.getDueDate()));
                        }
                    }
                }
            } else {
                if (null == workOrder.getDueDate()) continue;

                mooringDueServiceResponseDto.setMooringServiceDate(DateUtil.dateToString(workOrder.getDueDate()));

                String mooringDueServiceDateStr = mooringDueServiceResponseDto.getMooringServiceDate();
                LocalDate mooringDueServiceDate = DateUtil.stringToLocalDate(mooringDueServiceDateStr);

                if (mooringDueServiceDate.isBefore(dateAfter31Days)) mooringDueServiceResponseDto.setUnder30(true);

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
