package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.BoatyardDto;
import com.marinamooringmanagement.model.dto.ServiceAreaDto;
import com.marinamooringmanagement.model.entity.ServiceArea;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.ServiceAreaRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface ServiceAreaService {
    BasicRestResponse saveServiceArea(final ServiceAreaRequestDto serviceAreaRequestDto, final HttpServletRequest request);

    BasicRestResponse fetchServiceAreas(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    BasicRestResponse fetchMooringsWithServiceArea(final BaseSearchRequest baseSearchRequest, final Integer id, final HttpServletRequest request);

    ServiceAreaDto getById(final Integer id);

    BasicRestResponse deleteServiceAreaById(final Integer id, final HttpServletRequest request);

    BasicRestResponse updateServiceArea(final ServiceAreaRequestDto serviceAreaRequestDto, final Integer id, final HttpServletRequest request);
}
