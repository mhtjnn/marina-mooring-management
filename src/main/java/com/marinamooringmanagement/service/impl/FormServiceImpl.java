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
import com.marinamooringmanagement.utils.DateUtil;
import com.marinamooringmanagement.utils.PDFUtils;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
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
            authorizationUtil.checkAuthority(customerOwnerId);
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
                        FormResponseDto formResponseDto = formMapper.toResponseDto(FormResponseDto.builder().build(), form);
                        if(null != form.getCreationDate()) formResponseDto.setSubmittedDate(DateUtil.dateToString(form.getCreationDate()));
                        if(null != form.getCreatedBy()) formResponseDto.setSubmittedBy(form.getCreatedBy());
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

    @Override
    public BasicRestResponse deleteForm(Integer id, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);
            Form form = formRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("No form found with the given id: %1$s", id)));
            log.info(String.format("Deleting form with id: %1$s", id));
            if(ObjectUtils.notEqual(user, form.getUser())) {
                log.error(String.format("Form with id: %1$s is associated with other user", id));
                throw new RuntimeException(String.format("Form with id: %1$s is associated with other user", id));
            }
            formRepository.delete(form);
            log.info(String.format("Form with id: %1$s is deleted successfully!!!", id));
            response.setMessage(String.format("Form with id: %1$s is deleted successfully!!!", id));
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public Form downloadForm(Integer id, HttpServletRequest request) {
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);
            Form form = formRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("No form found with the given id: %1$s", id)));
            log.info(String.format("Downloading form with id: %1$s", id));
            if(ObjectUtils.notEqual(user, form.getUser())) {
                log.error(String.format("Form with id: %1$s is associated with other user", id));
                throw new RuntimeException(String.format("Form with id: %1$s is associated with other user", id));
            }
            return form;
        } catch (Exception e) {
            throw  e;
        }
    }

    private void performSave(final FormRequestDto formRequestDto, final Form form, final Integer id, final HttpServletRequest request) {
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(null == id) {
                if(null == formRequestDto.getFormName()) throw new RuntimeException(String.format("Form name cannot be blank during save"));
                if(null == formRequestDto.getFileName()) throw new RuntimeException(String.format("File name cannot be blank during save"));
                form.setCreationDate(new Date(System.currentTimeMillis()));
                form.setCreatedBy(user.getFirstName() + " " + user.getLastName());
            }
            form.setLastModifiedDate(new Date(System.currentTimeMillis()));

            formMapper.toEntity(form, formRequestDto);

            if(null != formRequestDto.getEncodedFormData()) {
                byte[] formData = PDFUtils.isPdfFile(formRequestDto.getEncodedFormData());
                form.setFormData(formData);
            } else {
                if(null == id) throw new RuntimeException(String.format("Form data cannot be null during save"));
            }

            if(null == id) form.setUser(user);

            formRepository.save(form);
        } catch (Exception e) {
            throw e;
        }
    }
}
