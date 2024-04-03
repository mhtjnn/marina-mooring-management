package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.VendorMapper;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import com.marinamooringmanagement.repositories.VendorRepository;
import com.marinamooringmanagement.service.VendorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;


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

    /**
     * Fetches a paginated list of vendors.
     *
     * @param page    the page number
     * @param size    the page size
     * @param sortBy  the field to sort by
     * @param sortDir the sort direction (asc or desc)
     * @return a list of vendor response DTOs
     */
    @Override
    public List<VendorResponseDto> fetchVendors(Integer page, Integer size, String sortBy, String sortDir) {
        try {
            logger.info("API called to fetch all the vendors from the database");
            Sort sort = null;
            if(sortDir.equalsIgnoreCase("asc")) {
                sort = Sort.by(sortBy).ascending();
            } else {
                sort = Sort.by(sortBy).descending();
            }
            Pageable p = PageRequest.of(page, size, sort);
            Page<Vendor> vendorList = vendorRepository.findAll(p);
            List<VendorResponseDto> vendorResponseDtoList = vendorList.stream()
                    .map(vendor -> vendorMapper.mapToVendorResponseDto(VendorResponseDto.builder().build(), vendor))
                    .collect(Collectors.toList());
            return vendorResponseDtoList;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all the vendors from the database", e);
            throw new DBOperationException("Error occurred while fetching all the vendors from the database", e);
        }
    }

    /**
     * Saves a vendor based on the provided request DTO.
     *
     * @param requestDto the vendor request DTO
     */
    @Override
    public void saveVendor(VendorRequestDto requestDto) {
        try {
            logger.info("API called to save the vendor in the database");
            Vendor vendor = Vendor.builder().build();
            performSave(requestDto, vendor, null);
        } catch (Exception e) {
            logger.error("Error occurred while saving the vendor in the database", e);
            throw new DBOperationException("Error occurred while saving the vendor in the database", e);
        }
    }

    /**
     * Deletes a vendor based on the provided ID.
     *
     * @param vendorId the vendor ID
     * @return a message indicating the deletion status
     */
    @Override
    public String deleteVendor(Integer vendorId) {
        try {
            logger.info("API called to delete the vendor from the database");
            vendorRepository.deleteById(vendorId);
            return "Vendor with the given vendor ID is deleted successfully";
        } catch (Exception e) {
            logger.error("Error occurred while deleting the vendor from the database", e);
            throw new DBOperationException("Error occurred while deleting the vendor from the database", e);
        }
    }

    /**
     * Updates a vendor based on the provided request DTO and vendor ID.
     *
     * @param requestDto the vendor request DTO
     * @param vendorId   the vendor ID
     */
    @Override
    public void updateVendor(VendorRequestDto requestDto, Integer vendorId) {
        try {
            logger.info("API called to update the vendor in the database");
            if(null == vendorId) {
                throw new RuntimeException("vendorId cannot be null during update command");
            } else {
                Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);
                if(optionalVendor.isEmpty()) {
                    throw new ResourceNotFoundException("No vendor exists with the given vendor ID");
                }
                Vendor vendor = optionalVendor.get();
                performSave(requestDto, vendor, vendorId);
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating the vendor in the database", e);
            throw new DBOperationException("Error occurred while updating the vendor in the database", e);
        }
    }

    /**
     * Performs the actual saving of a vendor entity based on the request DTO and vendor object.
     *
     * @param requestDto the vendor request DTO
     * @param vendor     the vendor object to be saved or updated
     * @param vendorId   the vendor ID (null for new vendors)
     */
    private void performSave(VendorRequestDto requestDto, Vendor vendor, Integer vendorId) {
        try {
            logger.info("performSave() function called");
            if(null == vendorId) {
                vendor.setCreationDate(new Date(System.currentTimeMillis()));
                vendor.setLastModifiedDate(new Date(System.currentTimeMillis()));
            } else {
                vendor.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }
            vendorMapper.mapToVendor(vendor, requestDto);
            vendorRepository.save(vendor);
        } catch (Exception e) {
            logger.error("Error occurred during performSave() operation", e);
            throw new DBOperationException("Error occurred during performSave() operation", e);
        }
    }
}
