package com.marinamooringmanagement;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.CustomerMapper;
import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.CustomerRepository;
import com.marinamooringmanagement.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveCustomer_Successful() {

        CustomerRequestDto mockCustomerDto = mock(CustomerRequestDto.class);

        when(customerMapper.toEntity(any(), any())).thenReturn(mock(Customer.class));

        customerService.saveCustomer(mockCustomerDto);

        verify(customerRepository, times(1)).save(any());
    }

    @Test
    void testUpdateCustomer_ExceptionDuringSave() {

        Integer customerId = 1;
        CustomerRequestDto customerRequestDto = new CustomerRequestDto();
        customerRequestDto.setId(customerId);
        Customer customer = new Customer();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        doThrow(new RuntimeException("Database error")).when(customerRepository).save(customer);


        Exception exception = assertThrows(DBOperationException.class, () -> {
            customerService.updateCustomer(customerRequestDto, customerId);
        });

        assertTrue(exception.getCause() instanceof RuntimeException);

    }

    @Test
    void testGetCustomers() {

        Customer customer1 = new Customer();
        customer1.setId(1);

        List<Customer> customerList = List.of(customer1);

        Page<Customer> page = mock(Page.class);
        when(page.getContent()).thenReturn(customerList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("customerName").ascending());

        when(customerRepository.findAll(pageable)).thenReturn(page);

        when(customerMapper.toDto(any())).thenReturn(new CustomerDto());

        BasicRestResponse response = customerService.getCustomers(0, 10, "customerName", "asc");

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus().intValue());
        assertEquals("All customers are fetched successfully", response.getMessage());
        assertNotNull(response.getContent());

        assertTrue(response.getContent() instanceof List);
        List<?> contentList = (List<?>) response.getContent();
        assertTrue(contentList.size() >= 1);
    }


    @Test
    void testGetById_WhenCustomerExists() {

        Integer customerId = 1;
        Customer customerEntity = new Customer();
        customerEntity.setId(customerId);
        CustomerDto expectedCustomerDto = new CustomerDto();
        expectedCustomerDto.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));

        when(customerMapper.toDto(customerEntity)).thenReturn(expectedCustomerDto);

        CustomerDto result = customerService.getbyId(customerId);

        assertNotNull(result);
        assertEquals(expectedCustomerDto.getId(), result.getId());
    }

    @Test
    void testGetById_WhenCustomerDoesNotExist() {

        Integer customerId = 1;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(DBOperationException.class, () -> customerService.getbyId(customerId));
    }

    @Test
    void testGetById_WithNullId() {
        assertThrows(ResourceNotFoundException.class, () -> customerService.getbyId(null));
    }

    @Test
    void testUpdateCustomer_NotFound() {

        Integer customerId = 1;
        CustomerRequestDto customerRequestDto = new CustomerRequestDto();
        customerRequestDto.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DBOperationException.class, () -> {
            customerService.updateCustomer(customerRequestDto, customerId);
        });
        assertTrue(exception.getMessage().contains("No value present"));
    }

    @Test
    void testUpdateCustomer_IdNotProvided() {

        CustomerRequestDto customerRequestDto = new CustomerRequestDto();

        DBOperationException exception = assertThrows(DBOperationException.class, () -> {
            customerService.updateCustomer(customerRequestDto, null);
        });
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
        assertTrue(exception.getMessage().contains("Customer Id not provided for update request"));
    }


    @Test
    void testUpdateCustomer_Successful() {

        Integer customerId = 1;
        CustomerRequestDto customerRequestDto = new CustomerRequestDto();
        customerRequestDto.setId(customerId);
        Customer customer = new Customer();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        BasicRestResponse response = customerService.updateCustomer(customerRequestDto, customerId);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Customer with the given customer id updated successfully!!!", response.getMessage());
    }

}
