package com.marinamooringmanagement.service;

import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import com.marinamooringmanagement.repositories.MooringRepository;
import com.marinamooringmanagement.service.impl.MooringServiceImpl;
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

import static org.mockito.Mockito.*;

public class MooringServiceImplTest {

    @Mock
    private MooringRepository mooringRepo;

    @InjectMocks
    private MooringServiceImpl service;

    @Mock
    private MooringMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_successfully_save_mooring() {
        MooringRequestDto mooringRequestDto = newMooringRequestDtoInstance();

        Mooring mooring = newMooringInstance();

        when(mapper.mapToMooring(any(Mooring.class), any(MooringRequestDto.class))).thenReturn(mooring);

        when(mooringRepo.save(any(Mooring.class))).thenReturn(mooring);

        Mooring returnedMooring = service.performSave(mooringRequestDto, mooring, null);

        Assertions.assertEquals(mooringRequestDto.getMooringNumber(), returnedMooring.getMooringNumber());
        Assertions.assertEquals(mooringRequestDto.getBoatName(), returnedMooring.getBoatName());
        Assertions.assertEquals(mooringRequestDto.getBoatSize(), returnedMooring.getBoatSize());

        verify(mapper, times(1)).mapToMooring(mooring, mooringRequestDto);
        verify(mooringRepo, times(1)).save(mooring);
    }

    @Test
    public void should_return_all_moorings() {
        List<MooringResponseDto> mooringResponseDtoList = new ArrayList<>();
        mooringResponseDtoList.add(newMooringResponseDtoInstance());

        List<Mooring> moorings = new ArrayList<>();
        moorings.add(newMooringInstance());

        Page<Mooring> pagedMooringList = new PageImpl<>(moorings);

        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setContent(mooringResponseDtoList);

        when(mooringRepo.findAll(any(Pageable.class))).thenReturn(pagedMooringList);

        when(mapper.mapToMooringResponseDto(any(MooringResponseDto.class),any(Mooring.class)))
                .thenReturn(newMooringResponseDtoInstance());


        BasicRestResponse getResponse = service.fetchMoorings(1,10,"id","asc");

        Assertions.assertEquals(response.getContent(), getResponse.getContent());
        verify(mooringRepo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void should_successfully_update_mooring() {
        MooringRequestDto mooringRequestDto = newMooringRequestDtoInstance();
        mooringRequestDto.setBoatName("changed");

        Mooring oldMooring = newMooringInstance();

        Mooring updatedMooring = newMooringInstance();
        updatedMooring.setBoatName("changed");

        MooringResponseDto mooringResponseDto = newMooringResponseDtoInstance();

        BasicRestResponse response = BasicRestResponse.builder().build();
        mooringResponseDto.setBoatName("changed");
        response.setContent(mooringResponseDto);

        when(mapper.mapToMooring(any(Mooring.class), any(MooringRequestDto.class))).thenReturn(updatedMooring);

        when(mooringRepo.save(any(Mooring.class))).thenReturn(updatedMooring);

        Mooring returnedMooring = service.performSave(mooringRequestDto, oldMooring, 1);

        Assertions.assertEquals(mooringResponseDto.getBoatName(), returnedMooring.getBoatName());

        verify(mapper,times(1)).mapToMooring(any(Mooring.class), any(MooringRequestDto.class));
        verify(mooringRepo,times(1)).save(any(Mooring.class));
    }

    public MooringRequestDto newMooringRequestDtoInstance() {
        return MooringRequestDto.builder()
                .mooringNumber("123")
                .boatName("test")
                .boatSize("234")
                .build();
    }

    public Mooring newMooringInstance() {
        return Mooring.builder()
                .mooringNumber("123")
                .boatName("test")
                .boatSize("234")
                .build();
    }

    public MooringResponseDto newMooringResponseDtoInstance() {
        return MooringResponseDto.builder()
                .id(1)
                .mooringNumber("123")
                .boatName("changed")
                .boatSize("234")
                .build();
    }



}
