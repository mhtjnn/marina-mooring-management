package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.request.InventoryRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface InventoryService {
    BasicRestResponse saveInventory(InventoryRequestDto inventoryRequestDto, Integer vendorId, HttpServletRequest request);

    BasicRestResponse fetchInventories(BaseSearchRequest baseSearchRequest, String searchText, Integer vendorId);

    BasicRestResponse updateInventory(InventoryRequestDto inventoryRequestDto, Integer id, Integer vendorId, HttpServletRequest request);

    BasicRestResponse deleteInventory(Integer id, Integer vendorId, HttpServletRequest request);
}
