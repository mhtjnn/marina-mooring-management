package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.mapper.metadata.MooringStatusMapper;
import com.marinamooringmanagement.model.dto.ImageDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.metadata.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.ImageRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.repositories.metadata.*;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.MooringService;
import com.marinamooringmanagement.utils.DateUtil;
import com.marinamooringmanagement.utils.GPSUtil;
import com.marinamooringmanagement.utils.ImageUtils;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.OverridesAttribute;
import org.apache.commons.lang3.time.DateUtils;
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

/**
 * Service implementation for managing Mooring entities.
 */
@Service
public class MooringServiceImpl extends GlobalExceptionHandler implements MooringService{

    private static final Logger log = LoggerFactory.getLogger(MooringServiceImpl.class);

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private BoatyardRepository boatyardRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private MooringStatusMapper mooringStatusMapper;

    @Autowired
    private MooringStatusRepository mooringStatusRepository;

    @Autowired
    private BoatTypeRepository boatTypeRepository;

    @Autowired
    private SizeOfWeightRepository sizeOfWeightRepository;

    @Autowired
    private TypeOfWeightRepository typeOfWeightRepository;

    @Autowired
    private TopChainConditionRepository topChainConditionRepository;

    @Autowired
    private EyeConditionRepository eyeConditionRepository;

    @Autowired
    private BottomChainConditionRepository bottomChainConditionRepository;

    @Autowired
    private ShackleSwivelConditionRepository shackleSwivelConditionRepository;

    @Autowired
    private PennantConditionRepository pennantConditionRepository;

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoatyardMapper boatyardMapper;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Autowired
    private UserMapper userMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ImageRepository imageRepository;

    /**
     * Fetches a list of moorings based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText        the text used to search for specific moorings by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the mooring search.
     */
    @Override
    @Transactional
    public BasicRestResponse fetchMoorings(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");
            MooringsWithGPSCoordinatesResponse mooringsWithGPSCoordinatesResponse = MooringsWithGPSCoordinatesResponse.builder().build();
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);


            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            final List<Mooring> mooringList = mooringRepository.findAll((null == searchText) ? "" : searchText, user.getId());

            List<MooringWithGPSCoordinateResponse> allMooringsWithGPSCoordinate = new ArrayList<>();

            final List<MooringResponseDto> mooringResponseDtoList = mooringList
                    .stream()
                    .map(mooring -> {
                        MooringResponseDto mooringResponseDto = mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring);

                        MooringWithGPSCoordinateResponse mooringWithGPSCoordinateResponse = mooringMapper.mapToMooringWithGPSCoordinateResponse(MooringWithGPSCoordinateResponse.builder().build(), mooring);
                        if(null != mooring.getMooringStatus()) mooringWithGPSCoordinateResponse.setStatusId(mooring.getMooringStatus().getId());
                        allMooringsWithGPSCoordinate.add(mooringWithGPSCoordinateResponse);

                        if (null != mooring.getCustomer()) mooringResponseDto.setCustomerId(mooring.getCustomer().getId());
                        if (null != mooring.getCustomer()) mooringResponseDto.setCustomerName(String.format(mooring.getCustomer().getFirstName() + " " + mooring.getCustomer().getLastName()));
                        if(null != mooring.getUser()) mooringResponseDto.setUserId(mooring.getUser().getId());
                        if(null != mooring.getBoatyard()) mooringResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), mooring.getBoatyard()));
                        if(null != mooring.getServiceArea()) mooringResponseDto.setServiceAreaResponseDto(serviceAreaMapper.mapToResponseDto(ServiceAreaResponseDto.builder().build(), mooring.getServiceArea()));
                        if(null != mooring.getInstallBottomChainDate()) mooringResponseDto.setInstallBottomChainDate(DateUtil.dateToString(mooring.getInstallBottomChainDate()));
                        if(null != mooring.getInstallTopChainDate()) mooringResponseDto.setInstallTopChainDate(DateUtil.dateToString(mooring.getInstallTopChainDate()));
                        if(null != mooring.getInstallConditionOfEyeDate()) mooringResponseDto.setInstallConditionOfEyeDate(DateUtil.dateToString(mooring.getInstallConditionOfEyeDate()));
                        if(null != mooring.getInspectionDate()) mooringResponseDto.setInspectionDate(DateUtil.dateToString(mooring.getInspectionDate()));
                        if(null != mooring.getImageList() && !mooring.getImageList().isEmpty()) {
                            mooringResponseDto.setImageDtoList(mooring.getImageList()
                                    .stream()
                                    .map(image -> imageMapper.toDto(ImageDto.builder().build(), image))
                                    .toList());
                        }
                        return mooringResponseDto;
                    })
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), mooringResponseDtoList.size());

            List<MooringResponseDto> paginatedMooringResponseDtoList;

            if(start > mooringList.size()) {
                paginatedMooringResponseDtoList = new ArrayList<>();
            } else {
                paginatedMooringResponseDtoList = mooringResponseDtoList.subList(start, end);
            }

            mooringsWithGPSCoordinatesResponse.setMooringResponseDtoList(paginatedMooringResponseDtoList);
            mooringsWithGPSCoordinatesResponse.setMooringWithGPSCoordinateResponseList(allMooringsWithGPSCoordinate);

            response.setMessage("All moorings fetched successfully.");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(mooringsWithGPSCoordinatesResponse);
            response.setTotalSize(mooringResponseDtoList.size());
            response.setCurrentSize(paginatedMooringResponseDtoList.size());
        } catch (Exception e) {
            log.error("Error occurred while fetching all the moorings in the database", e);
            throw e;
        }
        return response;
    }

    private List<MooringWithGPSCoordinateResponse> fetchMooringWithGpsCoordinates() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MooringWithGPSCoordinateResponse> query = criteriaBuilder.createQuery(MooringWithGPSCoordinateResponse.class);
        Root<Mooring> root = query.from(Mooring.class);

        // Selecting id, mooring_id, gps_coordinates
        query.select(criteriaBuilder.construct(MooringWithGPSCoordinateResponse.class,
                root.get("id"),
                root.get("mooringNumber"),
                root.get("gpsCoordinates"),
                root.join("mooringStatus").get("id")
        ));

        // Executing the query
        TypedQuery<MooringWithGPSCoordinateResponse> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    /**
     * Saves a mooring based on the provided request DTO.
     *
     * @param mooringRequestDto the mooring request DTO
     */
    @Override
    @Transactional
    public BasicRestResponse saveMooring(final MooringRequestDto mooringRequestDto, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to save the mooring in the database");
            final Mooring mooring = new Mooring();

            if(null == mooringRequestDto.getMooringNumber()) throw new RuntimeException("Mooring number cannot be blank");

            performSave(mooringRequestDto, mooring, null, request);

            response.setMessage("Mooring saved successfully.");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("Error occurred while saving the mooring in the database {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Updates a mooring based on the provided request DTO and mooring ID.
     *
     * @param mooringRequestDto the mooring request DTO
     * @param mooringId         the mooring ID
     */
    @Override
    @Transactional
    public BasicRestResponse updateMooring(final MooringRequestDto mooringRequestDto, final Integer mooringId, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to update the mooring with the given mooring ID");
            if (mooringId == null) {
                throw new IllegalArgumentException("Mooring Id not provided for update request");
            }
            Optional<Mooring> optionalMooring = mooringRepository.findById(mooringId);
            final Mooring mooring = optionalMooring.orElseThrow(() -> new ResourceNotFoundException(String.format("Mooring not found with id: %1$s", mooringId)));
            performSave(mooringRequestDto, mooring, mooringId, request);
            response.setMessage("Mooring updated successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while updating mooring {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Deletes a mooring based on the provided ID.
     *
     * @param id the mooring ID
     * @return a message indicating the deletion status
     */
    @Override
    @Transactional
    public BasicRestResponse deleteMooring(final Integer id, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            if(-1 == customerOwnerId && null != request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID)) customerOwnerId = (Integer) request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            Optional<Mooring> optionalMooring = mooringRepository.findById(id);
            if (optionalMooring.isEmpty()) throw new RuntimeException(String.format("No mooring exists with %1$s", id));
            final Mooring savedMooring = optionalMooring.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(null != savedMooring.getUser()) {
                if(!savedMooring.getUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Mooring with the id: %1$s is associated with some other user", id));
            } else {
                throw new RuntimeException(String.format("Mooring with the id: %1$s is not associated with any User", id));
            }

            if(null != optionalMooring.get().getCustomer()) {
                Customer customer = optionalMooring.get().getCustomer();
                List<Mooring> mooringList = customer.getMooringList();
                mooringList.removeIf(mooring -> mooring.getId().equals(optionalMooring.get().getId()));
                customerRepository.save(customer);
            }

            mooringRepository.deleteById(id);
            Optional<Mooring> optionalMooringAfterDeleteOperation = mooringRepository.findById(id);
            final String message = optionalMooringAfterDeleteOperation.isEmpty() ? String.format("Mooring with id %1$s deleted successfully", id) : String.format("Failed to delete mooring with the given id %1$s", id);
            response.setMessage(message);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while deleting mooring with id " + id, e);
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Performs the actual saving of a mooring entity based on the request DTO and mooring object.
     *
     * @param mooringRequestDto the mooring request DTO
     * @param mooring           the mooring object to be saved or updated
     * @param id                the mooring ID (null for new moorings)
     */
    public Mooring performSave(final MooringRequestDto mooringRequestDto, final Mooring mooring, final Integer id, final HttpServletRequest request) {
        try {
            log.info("performSave() function called");

            final int customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(id == null) mooring.setCreationDate(new Date(System.currentTimeMillis()));

            final Mooring initialMooring = copyMooring(mooring);

            Optional<Mooring> optionalMooring;
            if(null != mooringRequestDto.getMooringNumber() && !mooringRequestDto.getMooringNumber().isBlank()) {
                optionalMooring = mooringRepository.findByMooringNumber(mooringRequestDto.getMooringNumber());
                if(optionalMooring.isPresent()) {
                    if(null == id) {
                        throw new RuntimeException(String.format("Given mooring number: %1$s is already present", mooringRequestDto.getMooringNumber()));
                    } else {
                        if(!optionalMooring.get().getId().equals(id)) throw new RuntimeException(String.format("Given mooring number: %1$s is associated with other mooring", mooringRequestDto.getMooringNumber()));
                    }
                }
            } else {
                throw new RuntimeException(String.format("Mooring number cannot be blank"));
            }

            mooringMapper.mapToMooring(mooring, mooringRequestDto);
            mooring.setUser(user);
            Mooring savedMooring = mooringRepository.save(mooring);

            if(null != mooringRequestDto.getImageRequestDtoList() && !mooringRequestDto.getImageRequestDtoList().isEmpty()) {
                List<Image> imageList = new ArrayList<>();
                if(null != mooring.getImageList() && !mooring.getImageList().isEmpty()) imageList = mooring.getImageList();
                Integer imageNumber = 1;
                for(ImageRequestDto imageRequestDto: mooringRequestDto.getImageRequestDtoList()) {
                    Image image = imageMapper.toEntity(Image.builder().build(), imageRequestDto);

                    if(null == imageRequestDto.getImageName()) throw new RuntimeException(String.format("No name provided for image at number: %1$s", imageNumber));
                    if(null == imageRequestDto.getImageData()) throw new RuntimeException(String.format("No image provided for: %1$s", imageRequestDto.getImageName()));

                    image.setImageData(ImageUtils.validateEncodedString(imageRequestDto.getImageData()));
                    image.setCreationDate(new Date(System.currentTimeMillis()));
                    image.setLastModifiedDate(new Date(System.currentTimeMillis()));
                    image.setMooring(savedMooring);
                    imageList.add(image);
                    imageNumber++;
                }
                imageRepository.saveAll(imageList);
            }

            if(null != mooringRequestDto.getInstallBottomChainDate() && !mooringRequestDto.getInstallBottomChainDate().isEmpty()) {
                Date installBottomChainDate = DateUtil.stringToDate(mooringRequestDto.getInstallBottomChainDate());
                savedMooring.setInstallBottomChainDate(installBottomChainDate);
            }

            if(null != mooringRequestDto.getInstallTopChainDate() && !mooringRequestDto.getInstallTopChainDate().isEmpty()) {
                Date installTopChainDate = DateUtil.stringToDate(mooringRequestDto.getInstallTopChainDate());
                savedMooring.setInstallTopChainDate(installTopChainDate);
            }

            if(null != mooringRequestDto.getInstallConditionOfEyeDate() && !mooringRequestDto.getInstallConditionOfEyeDate().isEmpty()) {
                Date conditionOfEyeDate = DateUtil.stringToDate(mooringRequestDto.getInstallConditionOfEyeDate());
                savedMooring.setInstallConditionOfEyeDate(conditionOfEyeDate);
            }

            if(null != mooringRequestDto.getInspectionDate() && !mooringRequestDto.getInspectionDate().isEmpty()) {
                Date inspectionDate = DateUtil.stringToDate(mooringRequestDto.getInspectionDate());
                savedMooring.setInspectionDate(inspectionDate);
            }

            if (null != mooringRequestDto.getGpsCoordinates() && !mooringRequestDto.getGpsCoordinates().isEmpty()) {
                final String gpsCoordinates = GPSUtil.getGpsCoordinates(mooringRequestDto.getGpsCoordinates());
                savedMooring.setGpsCoordinates(gpsCoordinates);
            }

            Optional<Customer> optionalCustomer;
            if(null != mooringRequestDto.getCustomerId()) {
                optionalCustomer = customerRepository.findById(mooringRequestDto.getCustomerId());
                if (optionalCustomer.isEmpty())
                    throw new RuntimeException(String.format("No customer found with the given customer Id: %1$s", mooringRequestDto.getCustomerId()));
                savedMooring.setCustomer(optionalCustomer.get());
                final Customer customer = optionalCustomer.get();
                if (!customer.getUser().getId().equals((customerOwnerId == -1) ? loggedInUserUtil.getLoggedInUserID() : customerOwnerId))
                    throw new RuntimeException(String.format("Customer with the id: %1$s is associated with some other customer owner", mooringRequestDto.getCustomerId()));

                final CustomerType customerType = customerTypeRepository.findByType(AppConstants.CustomerTypeConstants.DOCK);
                if(null != mooringRequestDto.getAddDock() && mooringRequestDto.getAddDock()) customer.setCustomerType(customerType);
            } else {
                optionalCustomer = Optional.empty();
            }

            Optional<Boatyard> optionalBoatyard;
            if (null != mooringRequestDto.getBoatyardId()) {
                optionalBoatyard = boatyardRepository.findById(mooringRequestDto.getBoatyardId());
                if (optionalBoatyard.isEmpty())
                    throw new ResourceNotFoundException(String.format("No boatyard found with the given boatyard id: %1$s", mooringRequestDto.getBoatyardId()));
                final Boatyard boatyard = optionalBoatyard.get();
                if (!boatyard.getUser().getId().equals((customerOwnerId == -1) ? loggedInUserUtil.getLoggedInUserID() : customerOwnerId))
                    throw new RuntimeException(String.format("Boatyard with the id: %1$s is associated with some other customer owner", mooringRequestDto.getBoatyardId()));
                savedMooring.setBoatyard(optionalBoatyard.get());
            } else {
                optionalBoatyard = Optional.empty();
            }

            Optional<ServiceArea> optionalServiceArea;
            if (null != mooringRequestDto.getServiceAreaId()) {
                optionalServiceArea = serviceAreaRepository.findById(mooringRequestDto.getServiceAreaId());
                if (optionalServiceArea.isEmpty())
                    throw new ResourceNotFoundException(String.format("No service area found with the given service area id: %1$s", mooringRequestDto.getServiceAreaId()));
                final ServiceArea serviceArea = optionalServiceArea.get();
                if (!serviceArea.getUser().getId().equals((customerOwnerId == -1) ? loggedInUserUtil.getLoggedInUserID() : customerOwnerId))
                    throw new RuntimeException(String.format("Service area with the id: %1$s is associated with some other customer owner", mooringRequestDto.getServiceAreaId()));
                savedMooring.setServiceArea(serviceArea);
            } else {
                optionalServiceArea = Optional.empty();
            }

            if(null != mooringRequestDto.getStatusId()) {
                final MooringStatus mooringStatus = mooringStatusRepository.findById(mooringRequestDto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No status found with the given id: %1$s", mooringRequestDto.getStatusId())));

                savedMooring.setMooringStatus(mooringStatus);
            }

            if (null != mooringRequestDto.getBoatTypeId()) {
                Optional<BoatType> optionalBoatType = boatTypeRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalBoatType.isEmpty())
                    throw new ResourceNotFoundException(String.format("No boat type found with the given id: %1$s", mooringRequestDto.getBoatTypeId()));
                savedMooring.setBoatType(optionalBoatType.get());
            }

            if (null != mooringRequestDto.getTypeOfWeightId()) {
                Optional<TypeOfWeight> optionalTypeOfWeight = typeOfWeightRepository.findById(mooringRequestDto.getTypeOfWeightId());
                if (optionalTypeOfWeight.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Type of weight found with the given id: %1$s", mooringRequestDto.getTypeOfWeightId()));
                savedMooring.setTypeOfWeight(optionalTypeOfWeight.get());
            }

            if (null != mooringRequestDto.getEyeConditionId()) {
                Optional<EyeCondition> optionalEyeCondition = eyeConditionRepository.findById(mooringRequestDto.getEyeConditionId());
                if (optionalEyeCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Eye condition found with the given id: %1$s", mooringRequestDto.getEyeConditionId()));
                savedMooring.setEyeCondition(optionalEyeCondition.get());
            }

            if (null != mooringRequestDto.getTopChainConditionId()) {
                Optional<TopChainCondition> optionalTopChainCondition = topChainConditionRepository.findById(mooringRequestDto.getTopChainConditionId());
                if (optionalTopChainCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Top chain condition found with the given id: %1$s", mooringRequestDto.getTopChainConditionId()));
                savedMooring.setTopChainCondition(optionalTopChainCondition.get());
            }

            if (null != mooringRequestDto.getBottomChainConditionId()) {
                Optional<BottomChainCondition> optionalBottomChainCondition = bottomChainConditionRepository.findById(mooringRequestDto.getBottomChainConditionId());
                if (optionalBottomChainCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Bottom chain condition found with the given id: %1$s", mooringRequestDto.getBottomChainConditionId()));
                savedMooring.setBottomChainCondition(optionalBottomChainCondition.get());
            }
            if (null != mooringRequestDto.getShackleSwivelConditionId()) {
                Optional<ShackleSwivelCondition> optionalShackleSwivelCondition = shackleSwivelConditionRepository.findById(mooringRequestDto.getShackleSwivelConditionId());
                if (optionalShackleSwivelCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Shackle swivel condition found with the given id: %1$s", mooringRequestDto.getShackleSwivelConditionId()));
                savedMooring.setShackleSwivelCondition(optionalShackleSwivelCondition.get());
            }

            mooring.setLastModifiedDate(new Date(System.currentTimeMillis()));

            savedMooring = mooringRepository.save(savedMooring);
            Mooring finalSavedMooring = savedMooring;

            optionalBoatyard.ifPresent(boatyard -> {
                if(null != boatyard.getMooringList()) boatyard.getMooringList().add(finalSavedMooring);
                else {
                    boatyard.setMooringList(new ArrayList<>());
                    boatyard.getMooringList().add(finalSavedMooring);
                }
                boatyardRepository.save(optionalBoatyard.get());
            });
            optionalCustomer.ifPresent(customer -> {
                if(null != customer.getMooringList()) customer.getMooringList().add(finalSavedMooring);
                else {
                    customer.setMooringList(new ArrayList<>());
                    customer.getMooringList().add(finalSavedMooring);
                }
                customerRepository.save(customer);
            });
            optionalServiceArea.ifPresent(serviceArea -> {
                if(null != serviceArea.getMooringList()) serviceArea.getMooringList().add(finalSavedMooring);
                else {
                    serviceArea.setMooringList(new ArrayList<>());
                    serviceArea.getMooringList().add(finalSavedMooring);
                }
                serviceAreaRepository.save(optionalServiceArea.get());
            });

            mooringChangedLogs(initialMooring, savedMooring, user);

            return savedMooring;
        } catch (Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException(e.getMessage(), e);
        }
    }

    @Transactional
    public Mooring fetchMooringById(final Integer mooringId, final HttpServletRequest request) {
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            Optional<Mooring> optionalMooring = mooringRepository.findById(mooringId);
            if(optionalMooring.isEmpty()) throw new ResourceNotFoundException(String.format("No mooring found with the given id: %1$s", mooringId));
            final Mooring mooring = optionalMooring.get();

            if(null == mooring.getUser()) throw new RuntimeException(String.format("Mooring with the id: %1$s is associated with no user", mooringId));
            if(!mooring.getUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Mooring with given id: %1$s is associated with some other user", mooringId));

            return mooring;
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    private Mooring copyMooring(final Mooring mooring) {

        final Mooring copyMooring = Mooring.builder().build();

        if(null != mooring.getId()) copyMooring.setId(mooring.getId());
        if(null != mooring.getMooringNumber()) copyMooring.setMooringNumber(mooring.getMooringNumber());
        if(null != mooring.getHarborOrArea()) copyMooring.setHarborOrArea(mooring.getHarborOrArea());
        if(null != mooring.getGpsCoordinates()) copyMooring.setGpsCoordinates(mooring.getGpsCoordinates());
        if(null != mooring.getInstallTopChainDate()) copyMooring.setInstallTopChainDate(mooring.getInstallTopChainDate());
        if(null != mooring.getInstallBottomChainDate()) copyMooring.setInstallBottomChainDate(mooring.getInstallBottomChainDate());
        if(null != mooring.getInstallConditionOfEyeDate()) copyMooring.setInstallConditionOfEyeDate(mooring.getInstallConditionOfEyeDate());
        if(null != mooring.getBoatName()) copyMooring.setBoatName(mooring.getBoatName());
        if(null != mooring.getBoatSize()) copyMooring.setBoatSize(mooring.getBoatSize());
        if(null != mooring.getBoatType()) copyMooring.setBoatType(mooring.getBoatType());
        if(null != mooring.getBoatWeight()) copyMooring.setBoatWeight(mooring.getBoatWeight());
        if(null != mooring.getSizeOfWeight()) copyMooring.setSizeOfWeight(mooring.getSizeOfWeight());
        if(null != mooring.getTypeOfWeight()) copyMooring.setTypeOfWeight(mooring.getTypeOfWeight());
        if(null != mooring.getEyeCondition()) copyMooring.setEyeCondition(mooring.getEyeCondition());
        if(null != mooring.getTopChainCondition()) copyMooring.setTopChainCondition(mooring.getTopChainCondition());
        if(null != mooring.getBottomChainCondition()) copyMooring.setBottomChainCondition(mooring.getBottomChainCondition());
        if(null != mooring.getShackleSwivelCondition()) copyMooring.setShackleSwivelCondition(mooring.getShackleSwivelCondition());
        if(null != mooring.getPendantCondition()) copyMooring.setPendantCondition(mooring.getPendantCondition());
        if(null != mooring.getDepthAtMeanHighWater()) copyMooring.setDepthAtMeanHighWater(mooring.getDepthAtMeanHighWater());
        if(null != mooring.getMooringStatus()) copyMooring.setMooringStatus(mooring.getMooringStatus());
        if(null != mooring.getCustomer()) copyMooring.setCustomer(mooring.getCustomer());
        if(null != mooring.getUser()) copyMooring.setUser(mooring.getUser());
        if(null != mooring.getBoatyard()) copyMooring.setBoatyard(mooring.getBoatyard());

        return copyMooring;

    }

    @Transactional
    private void mooringChangedLogs(final Mooring initialMooring, final Mooring savedMooring, final User user) {

        if(initialMooring.getId() != null && savedMooring.getId() != null && !initialMooring.getId().equals(savedMooring.getId()))
            log.info(String.format("Mooring (Integer) Id changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getId(), savedMooring.getId(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getMooringNumber() != null && savedMooring.getMooringNumber() != null && !initialMooring.getMooringNumber().equals(savedMooring.getMooringNumber()))
            log.info(String.format("Mooring number changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getMooringNumber(), savedMooring.getMooringNumber(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getHarborOrArea() != null && savedMooring.getHarborOrArea() != null && !initialMooring.getHarborOrArea().equals(savedMooring.getHarborOrArea()))
            log.info(String.format("Mooring harbor or area changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getHarborOrArea(), savedMooring.getHarborOrArea(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getGpsCoordinates() != null && savedMooring.getGpsCoordinates() != null && !initialMooring.getGpsCoordinates().equals(savedMooring.getGpsCoordinates()))
            log.info(String.format("Mooring gps coordinates changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getGpsCoordinates(), savedMooring.getGpsCoordinates(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getInstallBottomChainDate() != null && savedMooring.getInstallBottomChainDate() != null && !DateUtils.isSameDay(initialMooring.getInstallBottomChainDate(), savedMooring.getInstallBottomChainDate()))
            log.info(String.format("Mooring install bottom chain date changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getInstallBottomChainDate(), savedMooring.getInstallBottomChainDate(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getInstallTopChainDate() != null && savedMooring.getInstallTopChainDate() != null && !DateUtils.isSameDay(initialMooring.getInstallTopChainDate(), savedMooring.getInstallTopChainDate()))
            log.info(String.format("Mooring install top chain date changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getInstallTopChainDate(), savedMooring.getInstallTopChainDate(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getInstallConditionOfEyeDate() != null && savedMooring.getInstallConditionOfEyeDate() != null && !DateUtils.isSameDay(initialMooring.getInstallConditionOfEyeDate(), savedMooring.getInstallConditionOfEyeDate()))
            log.info(String.format("Mooring install condition of eye date changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getInstallConditionOfEyeDate(), savedMooring.getInstallConditionOfEyeDate(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getBoatName() != null && savedMooring.getBoatName() != null && !initialMooring.getBoatName().equals(savedMooring.getBoatName()))
            log.info(String.format("Mooring boat name changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getBoatName(), savedMooring.getBoatName(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getBoatSize() != null && savedMooring.getBoatSize() != null && !initialMooring.getBoatSize().equals(savedMooring.getBoatSize()))
            log.info(String.format("Mooring boat size changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getBoatSize(), savedMooring.getBoatSize(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getBoatType() != null
                        && savedMooring.getBoatType() != null
                        && initialMooring.getBoatType().getBoatType() != null
                        && savedMooring.getBoatType().getBoatType() != null
                        && !initialMooring.getBoatType().getBoatType().equals(savedMooring.getBoatType().getBoatType())
        )
            log.info(String.format("Mooring boat type changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getBoatType(), savedMooring.getBoatType(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getBoatWeight() != null && savedMooring.getBoatWeight() != null && !initialMooring.getBoatWeight().equals(savedMooring.getBoatWeight()))
            log.info(String.format("Mooring boat weight changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getBoatWeight(), savedMooring.getBoatWeight(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getSizeOfWeight() != null && savedMooring.getSizeOfWeight() != null && !initialMooring.getSizeOfWeight().equals(savedMooring.getSizeOfWeight()))
            log.info(String.format("Mooring size of weight changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getSizeOfWeight(), savedMooring.getSizeOfWeight(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getTypeOfWeight() != null
                        && savedMooring.getTypeOfWeight() != null
                        && initialMooring.getTypeOfWeight().getType() != null
                        && savedMooring.getTypeOfWeight().getType() != null
                && !initialMooring.getTypeOfWeight().getType().equals(savedMooring.getTypeOfWeight().getType())
        )
            log.info(String.format("Mooring type of weight changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getTypeOfWeight(), savedMooring.getTypeOfWeight(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getEyeCondition() != null
                        && savedMooring.getEyeCondition() != null
                        && initialMooring.getEyeCondition().getCondition() != null
                        && savedMooring.getEyeCondition().getCondition() != null
                        && !initialMooring.getEyeCondition().getCondition().equals(savedMooring.getEyeCondition().getCondition())
        )
            log.info(String.format("Mooring eye condition changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getEyeCondition(), savedMooring.getEyeCondition(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getTopChainCondition() != null
                        && savedMooring.getTopChainCondition() != null
                        && initialMooring.getTopChainCondition().getCondition() != null
                        && savedMooring.getTopChainCondition().getCondition() != null
                        && !initialMooring.getTopChainCondition().getCondition().equals(savedMooring.getTopChainCondition().getCondition())
        )
            log.info(String.format("Mooring top chain condition changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getTopChainCondition(), savedMooring.getTopChainCondition(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getBottomChainCondition() != null
                        && savedMooring.getBottomChainCondition() != null
                        && initialMooring.getBottomChainCondition().getCondition() != null
                        && savedMooring.getBottomChainCondition().getCondition() != null
                        && !initialMooring.getBottomChainCondition().getCondition().equals(savedMooring.getBottomChainCondition().getCondition())
        )
            log.info(String.format("Mooring bottom chain condition changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getBottomChainCondition(), savedMooring.getBottomChainCondition(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getShackleSwivelCondition() != null
                        && savedMooring.getShackleSwivelCondition() != null
                        && initialMooring.getShackleSwivelCondition().getCondition() != null
                        && savedMooring.getShackleSwivelCondition().getCondition() != null
                        && !initialMooring.getShackleSwivelCondition().getCondition().equals(savedMooring.getShackleSwivelCondition().getCondition())
        )
            log.info(String.format("Mooring shackle swivel condition changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getShackleSwivelCondition(), savedMooring.getShackleSwivelCondition(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getPendantCondition() != null && savedMooring.getPendantCondition() != null && !initialMooring.getPendantCondition().equals(savedMooring.getPendantCondition()))
            log.info(String.format("Mooring pendant condition changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getPendantCondition(), savedMooring.getPendantCondition(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialMooring.getDepthAtMeanHighWater() != null && savedMooring.getDepthAtMeanHighWater() != null && !initialMooring.getDepthAtMeanHighWater().equals(savedMooring.getDepthAtMeanHighWater()))
            log.info(String.format("Mooring depth at mean highWater changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getDepthAtMeanHighWater(), savedMooring.getDepthAtMeanHighWater(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getMooringStatus() != null
                        && savedMooring.getMooringStatus() != null
                        && initialMooring.getMooringStatus().getStatus() != null
                        && savedMooring.getMooringStatus().getStatus() != null
                        && !initialMooring.getMooringStatus().getStatus().equals(savedMooring.getMooringStatus().getStatus())
        )
            log.info(String.format("Mooring status changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialMooring.getMooringStatus(), savedMooring.getMooringStatus(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getCustomer() != null
                        && savedMooring.getCustomer() != null
                        && initialMooring.getCustomer().getId() != null
                        && savedMooring.getCustomer().getId() != null
                        && !initialMooring.getCustomer().getId().equals(savedMooring.getCustomer().getId())
        )
            log.info(String.format("Mooring assigned to customer changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), initialMooring.getCustomer()), customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), savedMooring.getCustomer()), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getUser() != null
                        && savedMooring.getUser() != null
                        && initialMooring.getUser().getId() != null
                        && savedMooring.getUser().getId() != null
                        && !initialMooring.getUser().getId().equals(savedMooring.getUser().getId())
        )
            log.info(String.format("Mooring associated with user changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), initialMooring.getUser()), userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), savedMooring.getUser()), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialMooring.getBoatyard() != null
                        && savedMooring.getBoatyard() != null
                        && initialMooring.getBoatyard().getId() != null
                        && savedMooring.getBoatyard().getId() != null
                        && !initialMooring.getBoatyard().getId().equals(savedMooring.getBoatyard().getId())
        )
            log.info(String.format("Mooring boatyard changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), initialMooring.getBoatyard()), boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), savedMooring.getBoatyard()), user.getId(), user.getFirstName() + " " + user.getLastName()));

    }
}
