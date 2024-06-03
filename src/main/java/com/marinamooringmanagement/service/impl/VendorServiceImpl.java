package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.VendorMapper;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import com.marinamooringmanagement.repositories.VendorRepository;
import com.marinamooringmanagement.service.VendorService;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
 * Service implementation for managing Vendor entities.
 */
@Service
public class VendorServiceImpl implements VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorServiceImpl.class);

    @Autowired
    private VendorMapper vendorMapper;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private SortUtils sortUtils;

    /**
     * Fetches a list of vendors based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific vendors by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the vendor search.
     */
    @Override
    public BasicRestResponse fetchVendors(final BaseSearchRequest baseSearchRequest, final String searchText) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            logger.info("API called to fetch all the vendors from the database");

            Specification<Vendor> spec = new Specification<Vendor>() {
                @Override
                public Predicate toPredicate(Root<Vendor> vendor, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if(null != searchText) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(vendor.get("state"), "%"+searchText+"%"),
                                criteriaBuilder.like(vendor.get("companyName"), "%" + searchText + "%"),
                                criteriaBuilder.like(vendor.get("website"), "%" + searchText + "%")
                        ));
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );

            final Page<Vendor> vendorList = vendorRepository.findAll(spec, p);

            final List<VendorResponseDto> vendorResponseDtoList = vendorList
                    .getContent()
                    .stream()
                    .map(vendor -> vendorMapper.mapToVendorResponseDto(VendorResponseDto.builder().build(), vendor))
                    .collect(Collectors.toList());

            response.setMessage("List of vendors in the database");
            response.setContent(vendorResponseDtoList);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            logger.error("Error occurred while fetching all the vendors from the database: {}", e.getLocalizedMessage());
            response.setMessage("Error occurred while fetching list of vendors from the database");
            response.setContent(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Saves a vendor based on the provided request DTO.
     *
     * @param requestDto the vendor request DTO
     */
    @Override
    public BasicRestResponse saveVendor(final VendorRequestDto requestDto) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            logger.info("API called to save the vendor in the database");
            final Vendor vendor = Vendor.builder().build();
            performSave(requestDto, vendor, null);
            response.setMessage("Vendor Saved Successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            logger.error("Error occurred while saving the vendor in the database {}", e.getLocalizedMessage());
            response.setMessage("Error occurred while saving the vendor in the database");
            response.setContent(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Deletes a vendor based on the provided ID.
     *
     * @param vendorId the vendor ID
     * @return a message indicating the deletion status
     */
    @Override
    public BasicRestResponse deleteVendor(final Integer vendorId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            logger.info("API called to delete the vendor from the database");
            vendorRepository.deleteById(vendorId);
            final String message = vendorRepository.findById(vendorId).isPresent() ? String.format("Vendor with the id %1$s failed to get deleted", vendorId) : String.format("Vendor with the id %1$s is deleted successfully", vendorId);
            response.setMessage(message);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            logger.error("Error occurred while deleting the vendor from the database {}", e.getLocalizedMessage());
            response.setMessage("Error occurred while deleting the vendor from the database");
            response.setContent(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Updates a vendor based on the provided request DTO and vendor ID.
     *
     * @param requestDto the vendor request DTO
     * @param vendorId   the vendor ID
     */
    @Override
    public BasicRestResponse updateVendor(final VendorRequestDto requestDto, final Integer vendorId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            logger.info("API called to update the vendor in the database");
            if(null == vendorId) {
                throw new RuntimeException("vendorId cannot be null during update command");
            } else {
                Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);
                if(optionalVendor.isEmpty()) {
                    throw new ResourceNotFoundException("No vendor exists with the given vendor ID");
                }
                final Vendor vendor = optionalVendor.get();
                performSave(requestDto, vendor, vendorId);
            }
            response.setMessage("Vendor updated successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            logger.error("Error occurred while updating the vendor in the database {}", e.getLocalizedMessage());
            response.setMessage("Error occurred while updating the vendor in the database");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Performs the actual saving of a vendor entity based on the request DTO and vendor object.
     *
     * @param requestDto the vendor request DTO
     * @param vendor     the vendor object to be saved or updated
     * @param vendorId   the vendor ID (null for new vendors)
     */
    public Vendor performSave(final VendorRequestDto requestDto, final Vendor vendor, final Integer vendorId) {
        final Vendor savedVendor;
        try {
            logger.info("performSave() function called");
            if(null == vendorId) {
                vendor.setCreationDate(new Date(System.currentTimeMillis()));
                vendor.setLastModifiedDate(new Date(System.currentTimeMillis()));
            } else {
                vendor.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }
            vendorMapper.mapToVendor(vendor, requestDto);
            savedVendor = vendorRepository.save(vendor);
        } catch (Exception e) {
            logger.error("Error occurred during performSave() operation {}", e.getLocalizedMessage());
            throw new DBOperationException("Error occurred during performSave() operation", e);
        }
        return savedVendor;
    }
}