package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.InventoryMapper;
import com.marinamooringmanagement.mapper.VendorMapper;
import com.marinamooringmanagement.model.entity.Inventory;
import com.marinamooringmanagement.model.entity.metadata.InventoryType;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.InventoryRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.InventoryResponseDto;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import com.marinamooringmanagement.repositories.InventoryRepository;
import com.marinamooringmanagement.repositories.InventoryTypeRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.VendorRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.InventoryService;
import com.marinamooringmanagement.utils.ConversionUtils;
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

import java.math.BigDecimal;
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

    @Autowired
    private VendorMapper vendorMapper;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private ConversionUtils conversionUtils;

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Override
    public BasicRestResponse saveInventory(InventoryRequestDto inventoryRequestDto, Integer vendorId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            log.info(String.format("Save inventory method called.", inventoryRequestDto.getId()));

            final Inventory inventory = Inventory.builder().build();

            performSave(inventoryRequestDto, inventory, vendorId, null, request);

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

            final Vendor vendor = authorizationUtil.checkAuthorityForInventory(vendorId, request);

            Specification<Inventory> specs = new Specification<Inventory>() {
                @Override
                public Predicate toPredicate(Root<Inventory> inventory, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (searchText != null && !searchText.isEmpty()) {
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";

                        if (conversionUtils.canConvertToBigDecimal(searchText)) {
                            BigDecimal cost = new BigDecimal(searchText);
                            BigDecimal salePrice = new BigDecimal(searchText);

                            // Add predicates for exact match on cost and salePrice
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.equal(criteriaBuilder.toBigDecimal(inventory.get("cost")), cost),
                                    criteriaBuilder.equal(criteriaBuilder.toBigDecimal(inventory.get("salePrice")), salePrice),
                                    criteriaBuilder.like(criteriaBuilder.lower(inventory.get("itemName")), lowerCaseSearchText),
                                    criteriaBuilder.like(criteriaBuilder.lower(inventory.join("inventoryType").get("type")), lowerCaseSearchText),
                                    criteriaBuilder.like(criteriaBuilder.toString(inventory.get("id")), lowerCaseSearchText)
                            ));
                        } else {
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.like(criteriaBuilder.lower(inventory.get("itemName")), lowerCaseSearchText),
                                    criteriaBuilder.like(criteriaBuilder.lower(inventory.join("inventoryType").get("type")), lowerCaseSearchText),
                                    criteriaBuilder.like(criteriaBuilder.toString(inventory.get("id")), lowerCaseSearchText)
                            ));
                        }
                    }

                    // Always add the vendor ID predicate
                    predicates.add(criteriaBuilder.equal(inventory.join("vendor").get("id"), vendor.getId()));

                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
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
                    .map(inventory -> {
                        InventoryResponseDto inventoryResponseDto = inventoryMapper.mapToInventoryResponseDto(InventoryResponseDto.builder().build(), inventory);
                        if (null != inventory.getTaxable()) {
                            if ((inventory.getTaxable().equals(true))) {
                                inventoryResponseDto.setTaxable("yes");
                            } else {
                                inventoryResponseDto.setTaxable("no");
                            }
                        }
                        if (null != inventory.getVendor())
                            inventoryResponseDto.setVendorResponseDto(vendorMapper.mapToVendorResponseDto(VendorResponseDto.builder().build(), inventory.getVendor()));
                        return inventoryResponseDto;
                    })
                    .toList();

            response.setContent(inventoryResponseDtoList);
            response.setMessage(String.format("Inventories with vendor: %1$s fetched successfully", vendorId));
            response.setStatus(HttpStatus.OK.value());
            response.setTotalSize(inventoryRepository.findAll(specs).size());
            if (inventoryResponseDtoList.isEmpty()) response.setCurrentSize(0);
            else response.setCurrentSize(inventoryResponseDtoList.size());

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
            if (optionalInventory.isEmpty())
                throw new ResourceNotFoundException(String.format("No inventory found with the given id: %1$s", id));
            final Inventory inventory = optionalInventory.get();
            performSave(inventoryRequestDto, inventory, vendorId, id, request);
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

            Vendor vendor = authorizationUtil.checkAuthorityForInventory(vendorId, request);

            Optional<Inventory> optionalInventory = inventoryRepository.findById(id);
            if (optionalInventory.isEmpty())
                throw new ResourceNotFoundException(String.format("No inventory found with the given id: %1$s", id));
            final Inventory inventory = optionalInventory.get();

            if (!inventory.getVendor().equals(vendor))
                throw new RuntimeException(String.format("Inventory with id: %1$s is not associated with vendor of id: %2$s", id, vendorId));

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
            final HttpServletRequest request
    ) {
        try {
            final Vendor vendor = authorizationUtil.checkAuthorityForInventory(vendorId, request);

            inventoryMapper.mapToInventory(inventory, inventoryRequestDto);

            if (null == inventoryId) {
                inventory.setCreationDate(new Date(System.currentTimeMillis()));

                if (null == inventoryRequestDto.getInventoryTypeId())
                    throw new RuntimeException("Please select an inventory type");

                inventory.setVendor(vendor);
            } else {
                inventory.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }

            if(null != inventoryRequestDto.getTaxable()) {
                if(inventoryRequestDto.getTaxable().equals("yes")) {
                    inventory.setTaxable(true);
                } else if (inventoryRequestDto.getTaxable().equals("no")){
                    inventory.setTaxable(false);
                } else {
                    throw new RuntimeException("Taxable can only be yes or no");
                }
            } else {
                if(null == inventoryId) throw new RuntimeException(String.format("Taxable cannot be null during save"));
            }

            if (null != inventoryRequestDto.getInventoryTypeId()) {
                Optional<InventoryType> optionalInventoryType = inventoryTypeRepository.findById(inventoryRequestDto.getInventoryTypeId());
                if (optionalInventoryType.isEmpty())
                    throw new RuntimeException(String.format("No inventory type found with the given id: %1$s", inventoryRequestDto.getInventoryTypeId()));
                final InventoryType inventoryType = optionalInventoryType.get();

                inventory.setInventoryType(inventoryType);
            }

            final Inventory savedInventory = inventoryRepository.save(inventory);

            if (null != vendor.getInventoryList()) {
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
}
