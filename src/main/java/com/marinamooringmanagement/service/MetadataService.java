package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

public interface MetadataService {
    BasicRestResponse fetchStatus(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchBoatType(BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchSizeOfWeight(BaseSearchRequest baseSearchRequest);
}
