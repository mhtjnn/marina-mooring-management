package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.ImageRequestDto;
import com.marinamooringmanagement.model.request.MultipleImageRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface ImageService {
    BasicRestResponse uploadImage(final Integer entityId, final String entity, final MultipleImageRequestDto multipleImageRequestDto, final HttpServletRequest request);

    BasicRestResponse fetchImages(final Integer entityId, final String entity, final HttpServletRequest request);

    BasicRestResponse editImage(final Integer id, final String entity, final Integer entityId, final ImageRequestDto imageRequestDto, final HttpServletRequest request);

    BasicRestResponse viewImage(final Integer id, final HttpServletRequest request);

    BasicRestResponse deleteImage(final Integer id, final HttpServletRequest request);

}
