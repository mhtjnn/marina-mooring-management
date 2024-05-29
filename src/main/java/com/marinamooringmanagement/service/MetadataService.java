package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

public interface MetadataService {
    BasicRestResponse fetchStatus(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchBoatType(BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchSizeOfWeight(BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchTypeOfWeight(BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchTopChainCondition(BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchEyeConditions(BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchBottomChainConditions(BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchShackleSwivelConditions(BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchPennantConditions(BaseSearchRequest baseSearchRequest);
}
