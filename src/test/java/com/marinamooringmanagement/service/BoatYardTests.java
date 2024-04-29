package com.marinamooringmanagement.service;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.mapper.BoatYardMapper;
import com.marinamooringmanagement.model.entity.BoatYard;
import com.marinamooringmanagement.model.request.BoatYardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.BoatYardRepository;
import com.marinamooringmanagement.service.impl.BoatYardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BoatYardTests {

    @Mock
    private BoatYardRepository boatYardRepository;

    @Mock
    private BoatYardMapper boatYardMapper;

    @InjectMocks
    private BoatYardServiceImpl boatYardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testSaveBoatYard() {
        BoatYardRequestDto boatYardRequestDto = new BoatYardRequestDto();
        boatYardRequestDto.setBoatYardName("Sample BoatYard");

        BoatYard boatYard = BoatYard.builder().build();
        when(boatYardMapper.mapToBoatYard(boatYard, boatYardRequestDto)).thenReturn(boatYard);

        assertDoesNotThrow(() -> boatYardService.saveBoatYard(boatYardRequestDto));

        verify(boatYardRepository, times(1)).save(boatYard);
    }

    @Test
    public void testBoatYardDeleteById() {
        Integer id = 1;
        assertDoesNotThrow(() -> boatYardService.deleteBoatYardbyId(id));
        verify(boatYardRepository, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateBoatYard() {
        Integer id = 1;
        BoatYardRequestDto boatYardRequestDto = new BoatYardRequestDto();
        boatYardRequestDto.setId(id);
        boatYardRequestDto.setBoatYardName("Updated BoatYard");

        BoatYard boatYard = BoatYard.builder().id(id).boatYardName("Sample BoatYard").build();
        Optional<BoatYard> optionalBoatYard = Optional.of(boatYard);

        when(boatYardRepository.findById(id)).thenReturn(optionalBoatYard);
        when(boatYardMapper.mapToBoatYard(boatYard, boatYardRequestDto)).thenReturn(boatYard);

        BasicRestResponse response = boatYardService.updateBoatYard(boatYardRequestDto, id);
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("BoatYard with the given boatyard id updated successfully!!!", response.getMessage());
    }

    @Test
    public void testGetBoatYard_EmptyList() {
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "mooringName";
        String sortDir = "asc";

        Page<BoatYard> page = Page.empty();

        when(boatYardRepository.findAll(any(PageRequest.class))).thenReturn(page);

        BasicRestResponse result = boatYardService.getBoatYard(pageNumber, pageSize, sortBy, sortDir);

        assertNotNull(result);
        assertEquals("All boatyard are fetched successfully", result.getMessage());
    }

    @Test
    public void testGetById_InvalidId() {
        Integer id = null;

        assertThrows(RuntimeException.class, () -> boatYardService.getbyId(id));
    }

    @Test
    public void testDeleteById_NonExistingId() {
        Integer id = 999;

        doThrow(DBOperationException.class).when(boatYardRepository).deleteById(id);

        assertThrows(DBOperationException.class, () -> boatYardService.deleteBoatYardbyId(id));
    }

}