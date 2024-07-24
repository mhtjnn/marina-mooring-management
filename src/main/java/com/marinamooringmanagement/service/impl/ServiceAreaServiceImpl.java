package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.BoatyardMapper;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.mapper.ServiceAreaMapper;
import com.marinamooringmanagement.mapper.metadata.CountryMapper;
import com.marinamooringmanagement.mapper.metadata.ServiceAreaTypeMapper;
import com.marinamooringmanagement.mapper.metadata.StateMapper;
import com.marinamooringmanagement.model.dto.ServiceAreaDto;
import com.marinamooringmanagement.model.dto.metadata.ServiceAreaTypeDto;
import com.marinamooringmanagement.model.entity.ServiceArea;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.ServiceAreaType;
import com.marinamooringmanagement.model.entity.metadata.State;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.ServiceAreaRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import com.marinamooringmanagement.repositories.CustomerRepository;
import com.marinamooringmanagement.repositories.MooringRepository;
import com.marinamooringmanagement.repositories.ServiceAreaRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.metadata.CountryRepository;
import com.marinamooringmanagement.repositories.metadata.ServiceAreaTypeRepository;
import com.marinamooringmanagement.repositories.metadata.StateRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.MooringService;
import com.marinamooringmanagement.service.ServiceAreaService;
import com.marinamooringmanagement.utils.DateUtil;
import com.marinamooringmanagement.utils.GPSUtil;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceAreaServiceImpl implements ServiceAreaService {

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StateMapper stateMapper;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private MooringService mooringService;

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private ServiceAreaTypeRepository serviceAreaTypeRepository;

    @Autowired
    private ServiceAreaTypeMapper serviceAreaTypeMapper;

    @Autowired
    private BoatyardMapper boatyardMapper;

    private static final Logger log = LoggerFactory.getLogger(ServiceAreaServiceImpl.class);

    @Override
    public BasicRestResponse saveServiceArea(ServiceAreaRequestDto serviceAreaRequestDto, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final ServiceArea serviceArea = ServiceArea.builder().build();
            performSave(serviceAreaRequestDto, serviceArea, null, request);
            log.info(String.format("Saving data in the database for service area ID: %d", serviceAreaRequestDto.getId()));
            response.setMessage("Service area saved Successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing save operation: %s", e.getMessage()), e);
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchServiceAreas(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            Specification<ServiceArea> spec = new Specification<ServiceArea>() {
                @Override
                public Predicate toPredicate(Root<ServiceArea> serviceArea, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText) {
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";
                        Join<ServiceArea, State> stateJoin = serviceArea.join("state", JoinType.LEFT);
                        Join<ServiceArea, Country> countryJoin = serviceArea.join("country", JoinType.LEFT);
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(serviceArea.get("serviceAreaName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(serviceArea.get("id")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(serviceArea.get("streetHouse")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(serviceArea.get("aptSuite")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(stateJoin.get("name")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(countryJoin.get("name")), lowerCaseSearchText)
                        ));
                    }

                    predicates.add(authorizationUtil.fetchPredicate(customerOwnerId, serviceArea, criteriaBuilder));

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Sort sort = SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir());
            final Pageable p = PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize(), sort);

            Page<ServiceArea> serviceAreaList = serviceAreaRepository.findAll(spec, p);

            final List<ServiceAreaResponseDto> serviceAreaResponseDtoList = serviceAreaList
                    .getContent()
                    .stream()
                    .map(serviceArea -> {
                        ServiceAreaResponseDto serviceAreaResponseDto = ServiceAreaResponseDto.builder().build();
                        serviceAreaMapper.mapToResponseDto(serviceAreaResponseDto, serviceArea);
                        if(null != serviceArea.getServiceAreaType())
                            serviceAreaResponseDto.setServiceAreaTypeDto(serviceAreaTypeMapper.toDto(ServiceAreaTypeDto.builder().build(), serviceArea.getServiceAreaType()));
                        if (null != serviceArea.getState())
                            serviceAreaResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), serviceArea.getState()));
                        if (null != serviceArea.getCountry())
                            serviceAreaResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), serviceArea.getCountry()));
                        serviceAreaResponseDto.setMooringInventoried((null == serviceArea.getMooringList()) ? 0 : serviceArea.getMooringList().size());
                        if (null != serviceArea.getUser()) serviceAreaResponseDto.setUserId(serviceArea.getUser().getId());
                        return serviceAreaResponseDto;
                    })
                    .toList();

            log.info(String.format("Service areas fetched successfully"));

            response.setContent(serviceAreaResponseDtoList);
            response.setMessage("All service areas fetched successfully");
            response.setStatus(HttpStatus.OK.value());
            response.setTotalSize(serviceAreaRepository.findAll(spec).size());
            if (!serviceAreaResponseDtoList.isEmpty()) response.setCurrentSize(serviceAreaResponseDtoList.size());
            else response.setCurrentSize(0);
        } catch (Exception e) {
            log.error(String.format("Error occurred while fetching ServiceArea: %s", e.getMessage()), e);
            throw e;
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchMooringsWithServiceArea(BaseSearchRequest baseSearchRequest, Integer id, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        try {
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            Optional<ServiceArea> optionalServiceArea = serviceAreaRepository.findById(id);
            if (optionalServiceArea.isEmpty())
                throw new ResourceNotFoundException(String.format("No service area found with the given id: %1$s", id));

            final ServiceArea serviceArea = optionalServiceArea.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);
            if (null != serviceArea.getUser()) {
                if (!serviceArea.getUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("ServiceArea with the id: %1$s is associated with some other user", id));
            } else {
                throw new RuntimeException(String.format("ServiceArea with the id: %1$s is not associated with any User", id));
            }

            if (serviceArea.getMooringList().isEmpty()) response.setTotalSize(0);
            else response.setTotalSize(serviceArea.getMooringList().size());

            List<Mooring> filteredMoorings = serviceArea.getMooringList().stream()
                    .filter(mooring -> mooring.getUser().getId().equals(serviceArea.getUser().getId()))
                    .collect(Collectors.toList());

            final Sort sort = SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir());
            final Pageable p = PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize(), sort);

            int start = (int) p.getOffset();
            int end = Math.min((start + p.getPageSize()), filteredMoorings.size());

            List<Mooring> paginatedMoorings;
            if (start > filteredMoorings.size()) {
                paginatedMoorings = List.of(); // Return an empty list if the start index is out of bounds
            } else {
                paginatedMoorings = filteredMoorings.subList(start, end);
            }

            List<MooringResponseDto> mooringResponseDtoList = paginatedMoorings.stream()
                    .map(mooring -> {
                        MooringResponseDto mooringResponseDto = mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring);
                        if (null != mooring.getCustomer()) mooringResponseDto.setCustomerId(mooring.getCustomer().getId());
                        if (null != mooring.getCustomer()) mooringResponseDto.setCustomerName(String.format(mooring.getCustomer().getFirstName() + " " + mooring.getCustomer().getLastName()));
                        if(null != mooring.getUser()) mooringResponseDto.setUserId(mooring.getUser().getId());
                        if(null != mooring.getBoatyard()) mooringResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), mooring.getBoatyard()));
                        if(null != mooring.getServiceArea()) mooringResponseDto.setServiceAreaResponseDto(serviceAreaMapper.mapToResponseDto(ServiceAreaResponseDto.builder().build(), mooring.getServiceArea()));
                        if(null != mooring.getInstallBottomChainDate()) mooringResponseDto.setInstallBottomChainDate(DateUtil.dateToString(mooring.getInstallBottomChainDate()));
                        if(null != mooring.getInstallTopChainDate()) mooringResponseDto.setInstallTopChainDate(DateUtil.dateToString(mooring.getInstallTopChainDate()));
                        if(null != mooring.getInstallConditionOfEyeDate()) mooringResponseDto.setInstallConditionOfEyeDate(DateUtil.dateToString(mooring.getInstallConditionOfEyeDate()));
                        return mooringResponseDto;
                    })
                    .collect(Collectors.toList());

            log.info(String.format("Moorings fetched with the serviceArea id as %1$s", id));
            response.setMessage(String.format("Moorings fetched with the serviceArea id as %1$s", id));
            response.setTime(new Timestamp(System.currentTimeMillis()));
            response.setContent(mooringResponseDtoList);

            if (mooringResponseDtoList.isEmpty()) response.setCurrentSize(0);
            else response.setCurrentSize(mooringResponseDtoList.size());

            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(String.format(e.getLocalizedMessage()));
            response.setTime(new Timestamp(System.currentTimeMillis()));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public ServiceAreaDto getById(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID cannot be null");
        }
        try {
            Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findById(id);
            if (serviceAreaOptional.isPresent()) {
                return serviceAreaMapper.mapToDto(ServiceAreaDto.builder().build(), serviceAreaOptional.get());
            }
            throw new DBOperationException(String.format("Service with ID : %d doesn't exist", id));
        } catch (Exception e) {
            log.error(String.format("Error occurred while retrieving service area for ID: %d: %s", id, e.getMessage()), e);

            throw new DBOperationException(String.format("Service area with ID : %d doesn't exist", id));
        }
    }

    @Override
    public BasicRestResponse deleteServiceAreaById(Integer id, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            if (-1 == customerOwnerId && null != request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID))
                customerOwnerId = (Integer) request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            Optional<ServiceArea> optionalServiceArea = serviceAreaRepository.findById(id);

            if (optionalServiceArea.isEmpty())
                throw new DBOperationException(String.format("No service area found with the given id: %1$s", id));

            final ServiceArea serviceArea = optionalServiceArea.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if (null != serviceArea.getUser()) {
                if (!serviceArea.getUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("ServiceArea with the id: %1$s is associated with some other user", id));
            } else {
                throw new RuntimeException(String.format("ServiceArea with the id: %1$s is not associated with any User", id));
            }

            if (null == optionalServiceArea.get().getServiceAreaName())
                throw new RuntimeException(String.format("ServiceArea with the id as %1$s has no name", id));

            List<Mooring> mooringList = optionalServiceArea.get().getMooringList();

            for (Mooring mooring : mooringList) {
                mooringService.deleteMooring(mooring.getId(), request);
            }

            serviceAreaRepository.deleteById(id);

            response.setMessage(String.format("Service area with ID %d deleted successfully", id));
            response.setStatus(HttpStatus.OK.value());
            log.info(String.format("Service area with ID %d deleted successfully", id));
        } catch (Exception e) {
            log.error(String.format("Error occurred while deleting service area with ID %d", id));
            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    @Override
    public BasicRestResponse updateServiceArea(final ServiceAreaRequestDto serviceAreaRequestDto, Integer id, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            if (null == id) {
                log.info(String.format("Update attempt without a service area ID provided in the request DTO"));

                throw new ResourceNotFoundException(String.format("Service area not found with id: %1$s", id));
            }
            Optional<ServiceArea> optionalServiceArea = serviceAreaRepository.findById(id);
            if (optionalServiceArea.isPresent()) {
                ServiceArea serviceArea = optionalServiceArea.get();
                performSave(serviceAreaRequestDto, serviceArea, id, request);
                response.setMessage(String.format("Service area with the given service area id %d updated successfully!!!", id));
                response.setStatus(HttpStatus.OK.value());
            } else {
                throw new ResourceNotFoundException(String.format("Service area with Id: %d not found", id));
            }
        } catch (Exception e) {
            log.error(String.format("Error occurred while updating service area: %s", e.getMessage()), e);
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private void performSave(ServiceAreaRequestDto serviceAreaRequestDto, ServiceArea serviceArea, Integer id, HttpServletRequest request) {
        try {
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            User user = authorizationUtil.checkAuthority(customerOwnerId);

            serviceArea.setLastModifiedDate(new Date(System.currentTimeMillis()));

            Optional<ServiceArea> optionalServiceArea = null;

            if (null != serviceAreaRequestDto.getServiceAreaName()) {
                optionalServiceArea = serviceAreaRepository.findByServiceAreaName(serviceAreaRequestDto.getServiceAreaName());
                if (optionalServiceArea.isPresent()) {
                    if (null == id) {
                        throw new RuntimeException(String.format("Given service area name: %1$s is already present", serviceAreaRequestDto.getServiceAreaName()));
                    } else {
                        if (!optionalServiceArea.get().getId().equals(id))
                            throw new RuntimeException(String.format("Given service area name: %1$s is already present", serviceAreaRequestDto.getServiceAreaName()));
                    }
                }
            }

            serviceArea.setUser(user);

            serviceAreaMapper.mapToEntity(serviceArea, serviceAreaRequestDto);

            if (null == id) serviceArea.setCreationDate(new Date());
            serviceArea.setLastModifiedDate(new Date());

            if (null != serviceAreaRequestDto.getGpsCoordinates()) {
                final String gpsCoordinates = GPSUtil.getGpsCoordinates(serviceAreaRequestDto.getGpsCoordinates());
                serviceArea.setGpsCoordinates(gpsCoordinates);
            }

            if(null != serviceAreaRequestDto.getServiceAreaTypeId()) {
                final ServiceAreaType serviceAreaType = serviceAreaTypeRepository.findById(serviceAreaRequestDto.getServiceAreaTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No service area type found with the given id: %1$s", serviceAreaRequestDto.getServiceAreaTypeId())));
                serviceArea.setServiceAreaType(serviceAreaType);
            }

            if (null != serviceAreaRequestDto.getStateId()) {
                final Optional<State> optionalState = stateRepository.findById(serviceAreaRequestDto.getStateId());
                if (optionalState.isEmpty())
                    throw new ResourceNotFoundException(String.format("No state found with the given Id: %1$s", serviceAreaRequestDto.getStateId()));
                serviceArea.setState(optionalState.get());
            }

            if (null != serviceAreaRequestDto.getCountryId()) {
                final Optional<Country> optionalCountry = countryRepository.findById(serviceAreaRequestDto.getCountryId());
                if (optionalCountry.isEmpty())
                    throw new ResourceNotFoundException(String.format("No country found with the given Id: %1$s", serviceAreaRequestDto.getCountryId()));
                serviceArea.setCountry(optionalCountry.get());
            }

            final ServiceArea savedServiceArea = serviceAreaRepository.save(serviceArea);

            log.info(String.format("Service area saved successfully with ID: %d", serviceArea.getId()));
        } catch (Exception e) {
            log.error(String.format("Error occurred during performSave() function: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}
