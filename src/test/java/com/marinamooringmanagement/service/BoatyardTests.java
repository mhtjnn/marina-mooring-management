//package com.marinamooringmanagement.service;
//
//import com.marinamooringmanagement.exception.DBOperationException;
//import com.marinamooringmanagement.mapper.BoatyardMapper;
//import com.marinamooringmanagement.model.entity.Boatyard;
//import com.marinamooringmanagement.model.request.BoatyardRequestDto;
//import com.marinamooringmanagement.model.response.BasicRestResponse;
//import com.marinamooringmanagement.repositories.BoatyardRepository;
//import com.marinamooringmanagement.service.impl.BoatyardServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpStatus;
//
//import java.util.Optional;
//
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class BoatyardTests {
//
//    @Mock
//    private BoatyardRepository boatYardRepository;
//
//    @Mock
//    private BoatyardMapper boatYardMapper;
//
//    @InjectMocks
//    private BoatyardServiceImpl boatYardService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//
//    @Test
//    public void testSaveBoatYard() {
//        BoatyardRequestDto boatYardRequestDto = new BoatyardRequestDto();
//        boatYardRequestDto.setBoatyardName("Sample BoatYard");
//
//        Boatyard boatYard = Boatyard.builder().build();
//        when(boatYardMapper.mapToBoatYard(boatYard, boatYardRequestDto)).thenReturn(boatYard);
//
//        assertDoesNotThrow(() -> boatYardService.saveBoatyard(boatYardRequestDto));
//
//        verify(boatYardRepository, times(1)).save(boatYard);
//    }
//
//    @Test
//    public void testBoatYardDeleteById() {
//        Integer id = 1;
//        assertDoesNotThrow(() -> boatYardService.deleteBoatyardById(id));
//        verify(boatYardRepository, times(1)).deleteById(id);
//    }
//
//    @Test
//    public void testUpdateBoatYard() {
//        Integer id = 1;
//        BoatyardRequestDto boatYardRequestDto = new BoatyardRequestDto();
//        boatYardRequestDto.setId(id);
//        boatYardRequestDto.setBoatyardName("Updated BoatYard");
//
//        Boatyard boatYard = Boatyard.builder().id(id).boatyardName("Sample BoatYard").build();
//        Optional<Boatyard> optionalBoatYard = Optional.of(boatYard);
//
//        when(boatYardRepository.findById(id)).thenReturn(optionalBoatYard);
//        when(boatYardMapper.mapToBoatYard(boatYard, boatYardRequestDto)).thenReturn(boatYard);
//
//        BasicRestResponse response = boatYardService.updateBoatyard(boatYardRequestDto, id);
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK.value(), response.getStatus());
//        assertEquals("BoatYard with the given boatyard id updated successfully!!!", response.getMessage());
//    }
//
//    @Test
//    public void testGetBoatYard_EmptyList() {
//        Integer pageNumber = 0;
//        Integer pageSize = 10;
//        String sortBy = "mooringName";
//        String sortDir = "asc";
//
//        Page<Boatyard> page = Page.empty();
//
//        when(boatYardRepository.findAll(any(PageRequest.class))).thenReturn(page);
//
////        BasicRestResponse result = boatYardService.getBoatyard(pageNumber, pageSize, sortBy, sortDir);
//
////        assertNotNull(result);
////        assertEquals("All boatyard are fetched successfully", result.getMessage());
//    }
//
//    @Test
//    public void testGetById_InvalidId() {
//        Integer id = null;
//
//        assertThrows(RuntimeException.class, () -> boatYardService.getbyId(id));
//    }
//
//    @Test
//    public void testDeleteById_NonExistingId() {
//        Integer id = 999;
//
//        doThrow(DBOperationException.class).when(boatYardRepository).deleteById(id);
//
//        assertThrows(DBOperationException.class, () -> boatYardService.deleteBoatyardById(id));
//    }
//
//}