package com.marinamooringmanagement.service;

import com.marinamooringmanagement.mapper.VendorMapper;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import com.marinamooringmanagement.repositories.VendorRepository;
import com.marinamooringmanagement.service.impl.VendorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class VendorServiceImplTest {

    @Mock
    private VendorMapper mapper;

    @Mock
    private VendorRepository vendorRepo;

    @InjectMocks
    private VendorServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_successfully_save_vendor() {
        VendorRequestDto vendorRequestDto = newVendorRequestDtoInstance();

        Vendor vendor = newVendorInstance();

        when(mapper.mapToVendor(any(Vendor.class), any(VendorRequestDto.class))).thenReturn(vendor);

        when(vendorRepo.save(any(Vendor.class))).thenReturn(vendor);

        Vendor returnedVendor = service.performSave(vendorRequestDto, vendor, null);

        Assertions.assertEquals(vendorRequestDto.getCompanyName(), returnedVendor.getCompanyName());
        Assertions.assertEquals(vendorRequestDto.getWebsite(), returnedVendor.getWebsite());
        Assertions.assertEquals(vendorRequestDto.getStreet(), returnedVendor.getStreet());

        verify(mapper, times(1)).mapToVendor(vendor, vendorRequestDto);
        verify(vendorRepo, times(1)).save(vendor);
    }

    @Test
    public void should_return_all_vendors() {
        List<VendorResponseDto> vendorResponseDtoList = new ArrayList<>();
        vendorResponseDtoList.add(newVendorResponseDtoInstance());

        List<Vendor> vendors = new ArrayList<>();
        vendors.add(newVendorInstance());

        Page<Vendor> pagedVendorList = new PageImpl<>(vendors);

        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setContent(vendorResponseDtoList);

        when(vendorRepo.findAll(any(Pageable.class))).thenReturn(pagedVendorList);

        when(mapper.mapToVendorResponseDto(any(VendorResponseDto.class),any(Vendor.class)))
                .thenReturn(newVendorResponseDtoInstance());


        BasicRestResponse getResponse = service.fetchVendors(1,10,"id","asc",1,"","","");

        Assertions.assertEquals(response.getContent(), getResponse.getContent());
        verify(vendorRepo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void should_successfully_update_vendor() {
        VendorRequestDto vendorRequestDto = newVendorRequestDtoInstance();
        vendorRequestDto.setWebsite("changed");

        Vendor oldVendor = newVendorInstance();

        Vendor updatedVendor = newVendorInstance();
        updatedVendor.setWebsite("changed");

        when(mapper.mapToVendor(any(Vendor.class), any(VendorRequestDto.class))).thenReturn(updatedVendor);

        when(vendorRepo.save(any(Vendor.class))).thenReturn(updatedVendor);

        Vendor returnedVendor = service.performSave(vendorRequestDto, oldVendor, 1);

        Assertions.assertEquals(updatedVendor.getWebsite(), returnedVendor.getWebsite());

        verify(mapper, times(1)).mapToVendor(any(Vendor.class), any(VendorRequestDto.class));
        verify(vendorRepo, times(1)).save(any(Vendor.class));
    }

    public VendorRequestDto newVendorRequestDtoInstance() {
        return VendorRequestDto.builder()
                .companyName("test")
                .website("test")
                .street("234")
                .build();
    }

    public Vendor newVendorInstance() {
        return Vendor.builder()
                .companyName("test")
                .website("test")
                .street("234")
                .build();
    }

    public VendorResponseDto newVendorResponseDtoInstance() {
        return VendorResponseDto.builder()
                .id(1)
                .companyName("test")
                .country("test")
                .build();
    }

}
