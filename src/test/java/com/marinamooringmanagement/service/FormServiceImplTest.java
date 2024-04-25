package com.marinamooringmanagement.service;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.mapper.FormMapper;
import com.marinamooringmanagement.model.entity.Form;
import com.marinamooringmanagement.model.request.FormSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.FormResponseDto;
import com.marinamooringmanagement.repositories.FormRepository;
import com.marinamooringmanagement.service.impl.FormServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;


public class FormServiceImplTest {

    @Mock
    private FormRepository repository;

    @InjectMocks
    private FormServiceImpl service;

    @Mock
    private FormMapper formMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void should_successfully_fetch_forms() {
        final List<FormResponseDto> formResponseDtoList = new ArrayList<>();
        formResponseDtoList.add(newFormResponseDtoInstance());

        final List<Form> forms = new ArrayList<>();
        forms.add(newFormInstance());

        final FormSearchRequest formSearchRequest = newFormSearchRequestInstance();

        final Page<Form> pagedFormList = new PageImpl<>(forms);

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setContent(formResponseDtoList);

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pagedFormList);

        when(formMapper.mapToFormResponseDto(any(FormResponseDto.class),any(Form.class))).thenReturn(newFormResponseDtoInstance());

        final BasicRestResponse getResponse = service.fetchForms(formSearchRequest);

        Assertions.assertEquals(response.getContent(), getResponse.getContent());
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testFetchForms_Success() {
        // Mocking formSearchRequest
        final FormSearchRequest formSearchRequest = new FormSearchRequest();
        formSearchRequest.setPageNumber(0);
        formSearchRequest.setPageSize(10);
        formSearchRequest.setSort(Sort.by(Sort.Direction.ASC, "formName"));
        // Set other properties as needed

        // Mocking formList
        final Page<Form> formPage = mock(Page.class);

        // Mocking formResponseDtoList
        final FormResponseDto formResponseDto = new FormResponseDto();
        when(formMapper.mapToFormResponseDto(any(), any())).thenReturn(formResponseDto);

        // Mocking findAll method of formRepository
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(formPage);
        when(formPage.getContent()).thenReturn(Collections.singletonList(new Form())); // assuming there is at least one form in the list

        // Invoke the method under test
        final BasicRestResponse result = service.fetchForms(formSearchRequest);

        // Verify the response
        assert result != null;
        assert result.getStatus() == HttpStatus.OK.value();
        assert result.getContent() != null;
        // You may add more assertions based on your specific requirements
    }

    public Form newFormInstance() {
        return Form.builder()
                .id(1)
                .customerName("test")
                .formName("test")
                .build();
    }

    public FormResponseDto newFormResponseDtoInstance() {
        return FormResponseDto.builder()
                .id(1)
                .customerName("test")
                .formName("test")
                .build();
    }

    public FormSearchRequest newFormSearchRequestInstance() {
        final FormSearchRequest mooringSearchRequest =  FormSearchRequest.builder().build();
        mooringSearchRequest.setPageNumber(Integer.valueOf(AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM));
        mooringSearchRequest.setPageSize(Integer.valueOf(AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE));
        mooringSearchRequest.setSort(Sort.by("id").ascending());
        return mooringSearchRequest;
    }

}
