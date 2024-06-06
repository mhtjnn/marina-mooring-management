package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.InventoryMapper;
import com.marinamooringmanagement.model.entity.Inventory;
import com.marinamooringmanagement.model.entity.InventoryType;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.InventoryRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.InventoryResponseDto;
import com.marinamooringmanagement.repositories.InventoryRepository;
import com.marinamooringmanagement.repositories.InventoryTypeRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.VendorRepository;
import com.marinamooringmanagement.security.config.LoggedInUserUtil;
import com.marinamooringmanagement.service.InventoryService;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryTypeRepository inventoryTypeRepository;

    @Autowired
    private SortUtils sortUtils;

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Override
    public BasicRestResponse saveInventory(InventoryRequestDto inventoryRequestDto, Integer vendorId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            log.info(String.format("Save inventory method called.", inventoryRequestDto.getId()));

            final Inventory inventory = Inventory.builder().build();

            performSave(inventoryRequestDto, inventory, vendorId ,null, request);

            response.setMessage("Inventory saved successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing save operation: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }

        return response;
    }

    @Override
    public BasicRestResponse fetchInventories(BaseSearchRequest baseSearchRequest, String searchText, Integer vendorId, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Vendor vendor = checkAuthority(vendorId, request);
            Specification<Inventory> specs = new Specification<Inventory>() {
                @Override
                public Predicate toPredicate(Root<Inventory> inventory, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText && !searchText.isEmpty()) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(inventory.get("itemName"), "%" + searchText + "%"),
                                criteriaBuilder.like(inventory.join("inventoryType").get("type"), "%" + searchText + "%")
                        ));
                    }

                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(inventory.join("vendor").get("id"), vendorId)));

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );

            Page<Inventory> inventoryList = inventoryRepository.findAll(specs, p);

            List<InventoryResponseDto> inventoryResponseDtoList = inventoryList
                    .getContent()
                    .stream()
                    .map(inventory -> inventoryMapper.mapToInventoryResponseDto(InventoryResponseDto.builder().build(), inventory))
                    .toList();

            response.setContent(inventoryResponseDtoList);
            response.setMessage(String.format("Inventories with vendor: %1$s fetched successfully", vendorId));
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setContent(new ArrayList<>());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    @Override
    public BasicRestResponse updateInventory(InventoryRequestDto inventoryRequestDto, Integer id, Integer vendorId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            log.info(String.format("Update inventory method called.", inventoryRequestDto.getId()));
            Optional<Inventory> optionalInventory = inventoryRepository.findById(id);
            if(optionalInventory.isEmpty()) throw new ResourceNotFoundException(String.format("No inventory found with the given id: %1$s", id));
            final Inventory inventory = optionalInventory.get();
            performSave(inventoryRequestDto, inventory, vendorId ,id, request);
            response.setMessage("Inventory updated successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing update operation: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }

        return response;
    }

    @Override
    public BasicRestResponse deleteInventory(Integer id, Integer vendorId, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            Vendor vendor = checkAuthority(vendorId, request);

            Optional<Inventory> optionalInventory = inventoryRepository.findById(id);
            if(optionalInventory.isEmpty()) throw new ResourceNotFoundException(String.format("No inventory found with the given id: %1$s", id));
            final Inventory inventory = optionalInventory.get();

            if(!inventory.getVendor().equals(vendor)) throw new RuntimeException(String.format("Inventory with id: %1$s is not associated with vendor of id: %2$s", id, vendorId));

            vendor.getInventoryList().removeIf(inventory1 -> inventory1.getId().equals(inventory.getId()));

            inventoryRepository.delete(inventory);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(String.format("Inventory with id: %1$s deleted successfully", id));

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getLocalizedMessage());
        }

        return response;
    }

    private void performSave(
            final InventoryRequestDto inventoryRequestDto,
            final Inventory inventory,
            final Integer vendorId,
            final Integer inventoryId,
            final HttpServletRequest request) {

        try {
            final Vendor vendor = checkAuthority(vendorId, request);

            inventoryMapper.mapToInventory(inventory, inventoryRequestDto);

            if(null == inventoryId) {
                inventory.setCreationDate(new Date(System.currentTimeMillis()));

                if(null == inventoryRequestDto.getInventoryTypeId()) throw new RuntimeException("Please select an inventory type");

                inventory.setVendor(vendor);
            } else {
                inventory.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }

            if(null != inventoryRequestDto.getInventoryTypeId()) {
                Optional<InventoryType> optionalInventoryType = inventoryTypeRepository.findById(inventoryRequestDto.getInventoryTypeId());
                if(optionalInventoryType.isEmpty()) throw new RuntimeException(String.format("No inventory type found with the given id: %1$s", inventoryRequestDto.getInventoryTypeId()));
                final InventoryType inventoryType = optionalInventoryType.get();

                inventory.setInventoryType(inventoryType);
            }

            final Inventory savedInventory = inventoryRepository.save(inventory);

            if(null != vendor.getInventoryList()) {
                vendor.getInventoryList().removeIf(inventory1 -> inventory1.getId().equals(savedInventory.getId()));
            } else {
                vendor.setInventoryList(new ArrayList<>());
            }

            vendor.getInventoryList().add(savedInventory);

            vendorRepository.save(vendor);
        } catch (Exception e) {
            throw e;
        }
    }

    private Vendor checkAuthority(final Integer vendorId, final HttpServletRequest request) {
        final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
        final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();
        Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

        if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
            if(customerOwnerId == -1) throw new RuntimeException("Please select a customer owner");
        } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
            if (customerOwnerId != -1 && !customerOwnerId.equals(loggedInUserId))
                throw new RuntimeException("Not authorized to perform operations on customer with different customer owner id");
            customerOwnerId = loggedInUserId;
        } else {
            throw new RuntimeException("Not Authorized");
        }

        Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);
        if(optionalVendor.isEmpty()) throw new ResourceNotFoundException(String.format("No vendor found with the given id: %1$s", vendorId));
        final Vendor vendor = optionalVendor.get();

        if(null == vendor.getUser()) throw new RuntimeException(String.format("Vendor with id: %1$s has no user", vendorId));

        if(!vendor.getUser().getId().equals(customerOwnerId)) throw new RuntimeException(String.format("Vendor with the id: %1$s is associated with some other customer owner"));

        return vendor;
    }
}
