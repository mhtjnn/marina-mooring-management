package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.request.InventoryRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface InventoryService {
    BasicRestResponse saveInventory(final InventoryRequestDto inventoryRequestDto, final Integer vendorId, final HttpServletRequest request);

    BasicRestResponse fetchInventories(final BaseSearchRequest baseSearchRequest, final String searchText, final Integer vendorId, final HttpServletRequest request);

    BasicRestResponse updateInventory(final InventoryRequestDto inventoryRequestDto, final Integer id, final Integer vendorId, final HttpServletRequest request);

    BasicRestResponse deleteInventory(final Integer id, final Integer vendorId, final HttpServletRequest request);
}
