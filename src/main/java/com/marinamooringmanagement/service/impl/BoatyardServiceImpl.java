package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.BoatyardMapper;
import com.marinamooringmanagement.mapper.ServiceAreaMapper;
import com.marinamooringmanagement.mapper.metadata.CountryMapper;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.mapper.metadata.StateMapper;
import com.marinamooringmanagement.model.dto.BoatyardDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.State;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.BoatyardRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.repositories.metadata.CountryRepository;
import com.marinamooringmanagement.repositories.metadata.StateRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.BoatyardService;
import com.marinamooringmanagement.service.MooringService;
import com.marinamooringmanagement.utils.DateUtil;
import com.marinamooringmanagement.utils.GPSUtil;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Implementation of the BoatyardService interface.
 * Provides methods for CRUD operations on Boatyard entities.
 */
@Service
public class BoatyardServiceImpl implements BoatyardService {

    @Autowired
    private BoatyardRepository boatyardRepository;

    @Autowired
    private BoatyardMapper boatyardMapper;

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
    private ServiceAreaMapper serviceAreaMapper;

    private static final Logger log = LoggerFactory.getLogger(BoatyardServiceImpl.class);

    /**
     * Saves a Boatyard entity.
     *
     * @param boatYardRequestDto The BoatyardDto containing the data to be saved.
     */
    @Override
    public BasicRestResponse saveBoatyard(final BoatyardRequestDto boatYardRequestDto, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Boatyard boatyard = Boatyard.builder().build();
            performSave(boatYardRequestDto, boatyard, null, request);
            log.info(String.format("Saving data in the database for Boatyard ID %d", boatyard.getId()));
            response.setMessage("Boatyard Saved Successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing save operation: %s", e.getMessage()), e);
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Fetches a list of boatyards based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText        the text used to search for specific boatyards by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the boatyard search.
     */
    @Override
    public BasicRestResponse fetchBoatyards(final BaseSearchRequest baseSearchRequest, String searchText, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            List<Boatyard> boatyardList = boatyardRepository.findAll((null == searchText) ? "" : searchText, user.getId());

            final Sort sort = SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir());
            final Pageable p = PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize(), sort);

            final List<BoatyardResponseDto> boatyardDtoList = boatyardList
                    .stream()
                    .map(boatyard -> {
                        BoatyardResponseDto boatyardResponseDto = BoatyardResponseDto.builder().build();
                        boatyardMapper.mapToBoatYardResponseDto(boatyardResponseDto, boatyard);
                        if (null != boatyard.getState())
                            boatyardResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), boatyard.getState()));
                        if (null != boatyard.getCountry())
                            boatyardResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), boatyard.getCountry()));
                        boatyardResponseDto.setMooringInventoried(boatyardRepository.countAllMooringForGivenBoatyard(user.getId(), boatyard.getId()));
                        if (null != boatyard.getUser()) boatyardResponseDto.setUserId(boatyard.getUser().getId());
                        return boatyardResponseDto;
                    })
                    .collect(Collectors.toList());

            log.info(String.format("Boatyard fetched successfully"));

            response.setContent(boatyardDtoList);
            response.setMessage("All boatyard are fetched successfully");
            response.setStatus(HttpStatus.OK.value());
            response.setTotalSize(boatyardList.size());
            if (!boatyardDtoList.isEmpty()) response.setCurrentSize(boatyardDtoList.size());
            else response.setCurrentSize(0);
        } catch (Exception e) {
            log.error(String.format("Error occurred while fetching Boatyard: %s", e.getMessage()), e);
            throw e;
        }
        return response;
    }

    /**
     * Retrieves a Boatyard entity by its ID.
     *
     * @param id The ID of the Boatyard to retrieve.
     * @return The BoatyardDto object corresponding to the given ID.
     */
    @Override
    public BoatyardDto getbyId(final Integer id) {
        if (id == null) {
            throw new RuntimeException("ID cannot be null");
        }
        try {
            Optional<Boatyard> BoatYardEntityOptional = boatyardRepository.findById(id);
            if (BoatYardEntityOptional.isPresent()) {
                return boatyardMapper.toDto(BoatyardDto.builder().build(), BoatYardEntityOptional.get());
            }
            throw new DBOperationException(String.format("Boatyard with ID : %d doesn't exist", id));
        } catch (Exception e) {
            log.error(String.format("Error occurred while retrieving Boatyard for ID: %d: %s", id, e.getMessage()), e);

            throw new DBOperationException(String.format("Boatyard with ID : %d doesn't exist", id));
        }
    }


    /**
     * Deletes a Boatyard entity by its ID.
     *
     * @param id The ID of the Boatyard to delete.
     */
    @Override
    public BasicRestResponse deleteBoatyardById(final Integer id, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            if (-1 == customerOwnerId && null != request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID))
                customerOwnerId = (Integer) request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            Optional<Boatyard> optionalBoatyard = boatyardRepository.findById(id);

            if (optionalBoatyard.isEmpty())
                throw new DBOperationException(String.format("No boatyard found with the given id: %1$s", id));

            final Boatyard boatyard = optionalBoatyard.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if (null != boatyard.getUser()) {
                if (!boatyard.getUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("Boatyard with the id: %1$s is associated with some other user", id));
            } else {
                throw new RuntimeException(String.format("Boatyard with the id: %1$s is not associated with any User", id));
            }

            if (null == optionalBoatyard.get().getBoatyardName())
                throw new RuntimeException(String.format("Boatyard with the id as %1$s has no name", id));

            List<Mooring> mooringList = optionalBoatyard.get().getMooringList();

            for (Mooring mooring : mooringList) {
                mooringService.deleteMooring(mooring.getId(), request);
            }

            boatyardRepository.deleteById(id);

            response.setMessage(String.format("BoatYard with ID %d deleted successfully", id));
            response.setStatus(HttpStatus.OK.value());
            log.info(String.format("BoatYard with ID %d deleted successfully", id));
        } catch (Exception e) {
            log.error(String.format("Error occurred while deleting BoatYard with ID %d", id));
            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    /**
     * Updates a Boatyard entity.
     *
     * @param boatYardRequestDto The BoatyardDto containing the updated data.
     * @param id                 The ID of the Boatyard to update.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @Override
    public BasicRestResponse updateBoatyard(final BoatyardRequestDto boatYardRequestDto, final Integer id, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            if (null == id) {
                log.info(String.format("Update attempt without a Boatyard ID provided in the request DTO"));

                throw new ResourceNotFoundException(String.format("Boatyard not found with id: %1$s", id));
            }
            Optional<Boatyard> optionalBoatYard = boatyardRepository.findById(id);
            if (optionalBoatYard.isPresent()) {
                Boatyard boatyard = optionalBoatYard.get();
                performSave(boatYardRequestDto, boatyard, id, request);
                response.setMessage(String.format("Boatyard with the given boatyard id %d updated successfully!!!", id));
                response.setStatus(HttpStatus.OK.value());
            } else {
                throw new ResourceNotFoundException(String.format("Boatyard with Id: %d not found", id));
            }
        } catch (Exception e) {
            log.error(String.format("Error occurred while updating boatyard: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    /**
     * Fetches Moorings related to a specific boatyard from the database.
     *
     * @param id the ID of the boatyard.
     * @return a {@link BasicRestResponse} containing the moorings related to the boatyard.
     */
    @Override
    public BasicRestResponse fetchMooringsWithBoatyard(final BaseSearchRequest baseSearchRequest, final Integer id, final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        try {
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            Optional<Boatyard> optionalBoatyard = boatyardRepository.findById(id);
            if (optionalBoatyard.isEmpty())
                throw new ResourceNotFoundException(String.format("No boatyard found with the given id: %1$s", id));

            final Boatyard boatyard = optionalBoatyard.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);
            if (null != boatyard.getUser()) {
                if (!boatyard.getUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("Boatyard with the id: %1$s is associated with some other user", id));
            } else {
                throw new RuntimeException(String.format("Boatyard with the id: %1$s is not associated with any User", id));
            }

            if (boatyard.getMooringList().isEmpty()) response.setTotalSize(0);
            else response.setTotalSize(boatyard.getMooringList().size());

            List<Mooring> filteredMoorings = boatyardRepository.findAllMooringForGivenBoatyard(boatyard.getUser().getId(), boatyard.getId());

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
                        if (mooring.getCustomer() != null) {
                            mooringResponseDto.setCustomerId(mooring.getCustomer().getId());
                        }
                        if (mooring.getUser() != null) {
                            mooringResponseDto.setUserId(mooring.getUser().getId());
                        }
                        if (boatyard.getMainContact() != null) {
                            mooringResponseDto.setMainContact(boatyard.getMainContact());
                        }

                        if(null != mooring.getServiceArea()) mooringResponseDto.setServiceAreaResponseDto(serviceAreaMapper.mapToResponseDto(ServiceAreaResponseDto.builder().build(), mooring.getServiceArea()));
                        if(null != mooring.getInstallBottomChainDate()) mooringResponseDto.setInstallBottomChainDate(DateUtil.dateToString(mooring.getInstallBottomChainDate()));
                        if(null != mooring.getInstallTopChainDate()) mooringResponseDto.setInstallTopChainDate(DateUtil.dateToString(mooring.getInstallTopChainDate()));
                        if(null != mooring.getInstallConditionOfEyeDate()) mooringResponseDto.setInstallConditionOfEyeDate(DateUtil.dateToString(mooring.getInstallConditionOfEyeDate()));
                        if(null != mooring.getInspectionDate()) mooringResponseDto.setInspectionDate(DateUtil.dateToString(mooring.getInspectionDate()));
                        if (null != mooring.getCustomer()) {
                            if(null == mooring.getCustomer().getFirstName()) throw new RuntimeException(String.format("First name is null for customer with id: %1$s", mooring.getCustomer().getId()));
                            if(null == mooring.getCustomer().getLastName()) throw new RuntimeException(String.format("Last name is null for customer with id: %1$s", mooring.getCustomer().getId()));
                            mooringResponseDto.setCustomerName(String.format(mooring.getCustomer().getFirstName() + " " + mooring.getCustomer().getLastName()));
                        }
                        if(null != mooring.getBoatyard()) mooringResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), mooring.getBoatyard()));
                        return mooringResponseDto;
                    })
                    .collect(Collectors.toList());

            log.info(String.format("Moorings fetched with the boatyard id as %1$s", id));
            response.setMessage(String.format("Moorings fetched with the boatyard id as %1$s", id));
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

    /**
     * Performs the saving operation for a Boatyard entity.
     *
     * @param boatyardRequestDto The BoatyardRequestDto containing the data to be saved.
     * @param boatyard           The Boatyard entity to be saved or updated.
     * @param id                 The ID of the Boatyard entity, if available.
     * @throws DBOperationException If an error occurs during the saving operation.
     */
    public void performSave(final BoatyardRequestDto boatyardRequestDto, final Boatyard boatyard, Integer id, final HttpServletRequest request) {
        try {
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            User user = authorizationUtil.checkAuthority(customerOwnerId);

            boatyard.setLastModifiedDate(new Date(System.currentTimeMillis()));

            Optional<Boatyard> optionalBoatyard = null;

            if (null == id) {
                final String boatyardId = createBoatyardId();
                boatyard.setBoatyardId(boatyardId);
                boatyard.setCreationDate(new Date(System.currentTimeMillis()));
            }

            if (null != boatyardRequestDto.getBoatyardName()) {
                optionalBoatyard = boatyardRepository.findByBoatyardName(boatyardRequestDto.getBoatyardName());
                if (optionalBoatyard.isPresent()) {
                    if (null == id) {
                        throw new RuntimeException(String.format("Given boatyard name: %1$s is already present", boatyardRequestDto.getBoatyardName()));
                    } else {
                        if (!optionalBoatyard.get().getId().equals(id))
                            throw new RuntimeException(String.format("Given boatyard name: %1$s is already present", boatyardRequestDto.getBoatyardName()));
                    }
                }
            }

            boatyard.setUser(user);

            boatyardMapper.mapToBoatYard(boatyard, boatyardRequestDto);

            if (null == id) boatyard.setCreationDate(new Date());
            boatyard.setLastModifiedDate(new Date());

            if (null != boatyardRequestDto.getGpsCoordinates()) {
                final String gpsCoordinates = GPSUtil.getGpsCoordinates(boatyardRequestDto.getGpsCoordinates());
                boatyard.setGpsCoordinates(gpsCoordinates);
            }

            if (null != boatyardRequestDto.getStateId()) {
                final Optional<State> optionalState = stateRepository.findById(boatyardRequestDto.getStateId());
                if (optionalState.isEmpty())
                    throw new ResourceNotFoundException(String.format("No state found with the given Id: %1$s", boatyardRequestDto.getStateId()));
                boatyard.setState(optionalState.get());
            }

            if (null != boatyardRequestDto.getCountryId()) {
                final Optional<Country> optionalCountry = countryRepository.findById(boatyardRequestDto.getCountryId());
                if (optionalCountry.isEmpty())
                    throw new ResourceNotFoundException(String.format("No country found with the given Id: %1$s", boatyardRequestDto.getCountryId()));
                boatyard.setCountry(optionalCountry.get());
            }

            final Boatyard savedBoatyard = boatyardRepository.save(boatyard);

            if (StringUtils.isNotEmpty(boatyardRequestDto.getBoatyardName())
                    && StringUtils.isNotEmpty(boatyard.getBoatyardName())
                    && !StringUtils.equals(boatyardRequestDto.getBoatyardName(), boatyard.getBoatyardName())) {

                List<Mooring> mooringList = boatyard.getMooringList().stream().map(mooring -> {
                    mooring.setBoatyard(savedBoatyard);
                    return mooring;
                }).toList();

                mooringRepository.saveAll(mooringList);
            }

            log.info(String.format("Boatyard saved successfully with ID: %d", boatyard.getId()));
        } catch (Exception e) {
            log.error(String.format("Error occurred during performSave() function: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }
    }

    public Boatyard fetchBoatyardById(final Integer boatyardId, final HttpServletRequest request) {
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            Optional<Boatyard> optionalBoatyard = boatyardRepository.findById(boatyardId);
            if (optionalBoatyard.isEmpty())
                throw new ResourceNotFoundException(String.format("No boatyard found with the given id: %1$s", boatyardId));
            final Boatyard boatyard = optionalBoatyard.get();

            if (null == boatyard.getUser())
                throw new RuntimeException(String.format("Boatyard with the id: %1$s is associated with no user", boatyardId));
            if (!boatyard.getUser().getId().equals(user.getId()))
                throw new RuntimeException(String.format("Boatyard with given id: %1$s is associated with some other user", boatyardId));

            return boatyard;
        } catch (Exception e) {
            throw e;
        }
    }

    public String createBoatyardId() {
        final StringBuilder boatyardId = new StringBuilder();
        boatyardId.append("BY");


        int randomThreeDigitNumber = 100 + (int) (Math.random() * 900);
        String randomThreeDigitNumberStr = Integer.toString(randomThreeDigitNumber);
        boatyardId.append(randomThreeDigitNumberStr);

        final String boatyardIdString = boatyardId.toString();

        Optional<Boatyard> optionalBoatyard = boatyardRepository.findByBoatyardId(boatyardIdString);
        if (optionalBoatyard.isPresent()) createBoatyardId();
        return boatyardIdString;
    }
}