package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.FormMapper;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.entity.Form;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.FormRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.FormResponseDto;
import com.marinamooringmanagement.model.response.UserResponseDto;
import com.marinamooringmanagement.repositories.FormRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.service.FormService;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FormServiceImpl implements FormService {

    private static final Logger log = LoggerFactory.getLogger(FormServiceImpl.class);

    @Autowired
    private FormMapper formMapper;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public BasicRestResponse uploadForm(final FormRequestDto formRequestDto, final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Form form = Form.builder().build();
            performSave(formRequestDto, form, null, request);
            response.setMessage(String.format("Form uploaded successfully!!!"));
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchForms(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            Specification<Form> spec = new Specification<Form>() {
                @Override
                public Predicate toPredicate(Root<Form> form, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText) {
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(form.get("id")), lowerCaseSearchText)
                        ));
                    }

                    predicates.add(authorizationUtil.fetchPredicate(customerOwnerId, form, criteriaBuilder));

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );
            final Page<Form> formList = formRepository.findAll(spec, p);

            response.setTotalSize(formRepository.findAll(spec).size());

            final List<FormResponseDto> formResponseDtoList = formList
                    .getContent()
                    .stream()
                    .map(form -> {
                        FormResponseDto formResponseDto = FormResponseDto.builder().build();
                        formMapper.toResponseDto(FormResponseDto.builder().build(), form);
                        if(null != form.getUser()) formResponseDto.setUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), form.getUser()));
                        return formResponseDto;
                    })
                    .toList();

            response.setContent(formResponseDtoList);

            if(formResponseDtoList.isEmpty()) response.setCurrentSize(0);
            else response.setCurrentSize(formResponseDtoList.size());

            response.setMessage("All forms are fetched successfully");
            response.setStatus(HttpStatus.OK.value());

            log.info(String.format("Forms fetched successfully"));

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse editForm(final Integer id, final FormRequestDto formRequestDto, final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Form form = formRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("No form found with the id: %1$s", id)));
            performSave(formRequestDto, form, id, request);
            response.setMessage(String.format("Form updated successfully!!!"));
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private void performSave(final FormRequestDto formRequestDto, final Form form, final Integer id, final HttpServletRequest request) {
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(null == id) form.setCreationDate(new Date(System.currentTimeMillis()));
            form.setLastModifiedDate(new Date(System.currentTimeMillis()));

            formMapper.toEntity(form, formRequestDto);

            if(null == id) form.setUser(user);

            formRepository.save(form);
        } catch (Exception e) {
            throw e;
        }
    }
}
