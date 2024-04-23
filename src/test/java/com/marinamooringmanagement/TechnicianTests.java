package com.marinamooringmanagement;

import com.marinamooringmanagement.mapper.TechnicianMapper;
import com.marinamooringmanagement.model.dto.TechnicianDto;
import com.marinamooringmanagement.model.entity.Technician;
import com.marinamooringmanagement.model.request.TechnicianRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.TechnicianRepository;
import com.marinamooringmanagement.service.impl.TechnicianServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TechnicianTests {

    @Mock
    private TechnicianRepository technicianRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private TechnicianMapper technicianMapper;

    @InjectMocks
    private TechnicianServiceImpl technicianService;

    @Test
    public void saveTechnician_Successful() {

        TechnicianRequestDto mockTechnicianRequestDto = mock(TechnicianRequestDto.class);

        when(technicianMapper.toEntity(any(), any())).thenReturn(mock(Technician.class));

        technicianService.saveTechnician(mockTechnicianRequestDto);

        verify(technicianRepository, times(1)).save(any());
    }

    @Test
    public void testGetTechnicians() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";
        String sortDir = "asc";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        List<Technician> technicianList = Arrays.asList(new Technician(), new Technician());

        when(technicianRepository.findAll(pageable)).thenReturn(new PageImpl<>(technicianList));

        List<TechnicianDto> result = technicianService.getTechnicians(pageNumber, pageSize, sortBy, sortDir);
        assertEquals(technicianList.size(), result.size());
    }

    @Test
    public void testDeleteById() {
        Integer id = 1;
        assertDoesNotThrow(() -> technicianService.deleteTechnicianbyId(id));
        verify(technicianRepository, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateTechnician() {
        Integer id = 1;
        TechnicianRequestDto technicianRequestDto = new TechnicianRequestDto();
        technicianRequestDto.setId(id);
        technicianRequestDto.setTechnicianName("Updated Name");

        Technician technician = Technician.builder().id(id).technicianName("John Doe").build();
        Optional<Technician> optionalTechnician = Optional.of(technician);

        when(technicianRepository.findById(id)).thenReturn(optionalTechnician);
        when(technicianMapper.mapToTechnician(technician, technicianRequestDto)).thenReturn(technician);

        BasicRestResponse response = technicianService.updateTechnician(technicianRequestDto, id);
        Assertions.assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Technician with the given technician id updated successfully!!!", response.getMessage());
    }
}
