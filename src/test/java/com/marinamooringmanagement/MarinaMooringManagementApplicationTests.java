package com.marinamooringmanagement;


class MarinaMooringManagementApplicationTests {























//    @Test
//    void testUpdateCustomer_ExceptionDuringSave() {
//        // Arrange
//        Integer customerId = 1;
//        CustomerDto customerDto = new CustomerDto();
//        customerDto.setId(customerId); // Ensure ID matches the method's expectations
//        Customer customer = new Customer();
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
//        doThrow(new RuntimeException("Database error")).when(customerRepository).save(customer);
//
//        // Act & Assert
//        Exception exception = assertThrows(DBOperationException.class, () -> {
//            customerService.updateCustomer(customerDto, customerId);
//        });
//
//        assertTrue(exception.getCause() instanceof RuntimeException);
//        assertTrue(exception.getMessage().contains("Error updating customer: Database error"));
//    }





//    @Test
//    public void saveTechnician_Successful() {
//        // Create a mock CustomerDto object
//        TechnicianDto mockTechnicianDto = mock(TechnicianDto.class);
//
//        // Stub the behavior of customerMapper.toEntity() method
//        // Replace this with the actual behavior you want to stub
//        when(technicianMapper.toEntity(any(), any())).thenReturn(mock(Technician.class));
//
//        // Call the method under test
//        technicianService.saveTechnician(mockTechnicianDto);
//
//        // Verify that customerRepository.save() was called exactly once with any Customer object as an argument
//        verify(technicianRepository, times(1)).save(any());
//    }


//    @Test
//    public void testGetById() {
//        // Initialize mocks
//        MockitoAnnotations.initMocks(this);
//
//        Integer id = 1;
//        Technician technician = Technician.builder().id(id).technicianName("John Doe").build();
//        Optional<Technician> optionalTechnician = Optional.of(technician);
//
//        // Configure mock behavior
//        when(technicianRepository.findById(id)).thenReturn(optionalTechnician);
//        when(technicianMapper.toDto(technician)).thenReturn(new TechnicianDto()); // Use a constructor that doesn't take Technician
//
//        // Call the method under test
//        TechnicianDto result = technicianService.getbyId(id);
//
//        // Assertions
//        assertNotNull(result); // Check that the result is not null
//        assertEquals(technician.getTechnicianName(), result.getTechnicianName());
//    }


//    @Test
//    public void testSaveBoatYard() {
//        BoatYardDto boatYardDto = new BoatYardDto();
//        boatYardDto.setMooringName("Sample BoatYard");
//
//        BoatYard boatYard = BoatYard.builder().build();
//        when(boatYardMapper.toEntity(boatYardDto, new BoatYard())).thenReturn(boatYard);
//
//        assertDoesNotThrow(() -> boatYardService.saveBoatYard(boatYardDto));
//
//        verify(boatYardRepository, times(1)).save(boatYard);
//    }

//    @Test
//    public void testGetById() {
//        Integer id = 1;
//        BoatYard boatYard = BoatYard.builder().id(id).mooringName("Sample BoatYard").build();
//        Optional<BoatYard> optionalBoatYard = Optional.of(boatYard);
//
//        when(boatYardRepository.findById(id)).thenReturn(optionalBoatYard);
//        when(boatYardMapper.toDto(boatYard)).thenReturn(new BoatYardDto(boatYard));
//
//        BoatYardDto result = boatYardService.getbyId(id);
//
//        assertNotNull(result);
//        assertEquals(boatYard.getMooringName(), result.getMooringName());
//    }

}

