package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface VoiceMEMOService {

    BasicRestResponse fetchVoiceMEMO(final Integer id, final HttpServletRequest request);

    BasicRestResponse deleteVoiceMEMO(final Integer id, final HttpServletRequest request);
}
