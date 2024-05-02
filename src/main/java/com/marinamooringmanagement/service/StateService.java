package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.StateDto;
import com.marinamooringmanagement.model.request.StateSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

public interface StateService {
    BasicRestResponse fetchStates(StateSearchRequest countrySearchRequest);

    BasicRestResponse saveState(StateDto dto);

    BasicRestResponse updateState(StateDto dto, Integer countryId);

    BasicRestResponse deleteState(Integer id);
}
