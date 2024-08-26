package com.marinamooringmanagement;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.mapper.metadata.WorkOrderPayStatusMapper;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.metadata.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.repositories.metadata.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@EnableTransactionManagement
public class MarinaMooringManagementApplication{
    public static void main(String[] args) {
        SpringApplication.run(MarinaMooringManagementApplication.class, args);
    }

}
