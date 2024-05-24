package com.marinamooringmanagement;

import com.marinamooringmanagement.model.entity.Country;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.entity.State;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.repositories.CountryRepository;
import com.marinamooringmanagement.repositories.RoleRepository;
import com.marinamooringmanagement.repositories.StateRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class MarinaMooringManagementApplication implements CommandLineRunner {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(MarinaMooringManagementApplication.class, args);
	}

	/**
	 * This method runs the application after it has been started by the Spring Boot framework.
	 *
	 * @param args Command line arguments passed to the application.
	 * @throws Exception If an error occurs during the execution.
	 */
	@Override
	public void run(String... args) throws Exception {
		final String roleSql = "SELECT * FROM role";
		final List<Role> roleList = jdbcTemplate.query(roleSql, (resultSet, rowNum) ->
				null);

		final Role roleOwner = Role.builder().build();

		if(roleList.isEmpty()) {
			roleOwner.setId(1);
			roleOwner.setDescription("Administrator");
			roleOwner.setName("ADMINISTRATOR");
			roleOwner.setCreationDate(new Date(System.currentTimeMillis()));
			roleOwner.setLastModifiedDate(new Date(System.currentTimeMillis()));
			roleRepository.save(roleOwner);

			final Role roleCustomerAdmin = Role.builder().build();
			roleCustomerAdmin.setId(2);
			roleCustomerAdmin.setDescription("Customer Owner");
			roleCustomerAdmin.setName("CUSTOMER OWNER");
			roleCustomerAdmin.setCreationDate(new Date(System.currentTimeMillis()));
			roleCustomerAdmin.setLastModifiedDate(new Date(System.currentTimeMillis()));
			roleRepository.save(roleCustomerAdmin);

			final Role roleFinance = Role.builder().build();
			roleFinance.setId(3);
			roleFinance.setDescription("Finance");
			roleFinance.setName("FINANCE");
			roleFinance.setCreationDate(new Date(System.currentTimeMillis()));
			roleFinance.setLastModifiedDate(new Date(System.currentTimeMillis()));
			roleRepository.save(roleFinance);

			final Role roleTechnician = Role.builder().build();
			roleTechnician.setId(4);
			roleTechnician.setDescription("Technician");
			roleTechnician.setName("TECHNICIAN");
			roleTechnician.setCreationDate(new Date(System.currentTimeMillis()));
			roleTechnician.setLastModifiedDate(new Date(System.currentTimeMillis()));
			roleRepository.save(roleTechnician);
		}

		State savedState = null;
		final String stateSql = "SELECT * FROM state";
		final List<Role> stateList = jdbcTemplate.query(stateSql, (resultSet, rowNum) ->
				null);

		if(stateList.isEmpty()) {
			State state = State.builder()
					.name("New York")
					.label("New York")
					.build();

			savedState = stateRepository.save(state);
		}

		Country savedCountry = null;
		final String countrySql = "SELECT * FROM country";
		final List<Role> countryList = jdbcTemplate.query(countrySql, (resultSet, rowNum) ->
				null);

		if(countryList.isEmpty()) {
			Country country = Country.builder()
					.name("USA")
					.label("United States of America")
					.build();

			savedCountry = countryRepository.save(country);
		}

		final String userSql = "SELECT * FROM _user";
		final List<User> userList = jdbcTemplate.query(userSql, (resultSet, rowNum) ->
				null);

		if(userList.isEmpty()) {
			User user = User.builder().build();
			user.setId(1);
			user.setCreationDate(new Date(System.currentTimeMillis()));
			user.setLastModifiedDate(new Date(System.currentTimeMillis()));
			user.setName("Matt Pussey");
			user.setUserID("O0001");
			user.setEmail("matt.pussey@moorfind.com");
			user.setPassword(passwordEncoder.encode("1234"));
			user.setPhoneNumber("123456789");
			user.setRole(roleOwner);
			user.setCreatedBy("System");
			user.setLastModifiedBy("System");
			user.setState(savedState);
			user.setCountry(savedCountry);
			userRepository.save(user);
		}

	}
}
