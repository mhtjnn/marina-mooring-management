package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.FormRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface FormService {
    BasicRestResponse uploadForm(final FormRequestDto formRequestDto, final HttpServletRequest request);

    BasicRestResponse fetchForms(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    BasicRestResponse editForm(final Integer id, final FormRequestDto formRequestDto, final HttpServletRequest request);
}
