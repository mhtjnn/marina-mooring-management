package com.marinamooringmanagement;

import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.repositories.RoleRepository;
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

		final Role roleAdmin = Role.builder().build();

		if(roleList.isEmpty()) {
			Role roleUser = Role.builder().build();
			roleUser.setDescription("Normal User");
			roleUser.setName("USER");
			roleUser.setCreationDate(new Date(System.currentTimeMillis()));
			roleUser.setLastModifiedDate(new Date(System.currentTimeMillis()));
			roleRepository.save(roleUser);

			roleAdmin.setDescription("Administrator User");
			roleAdmin.setName("ADMINISTRATOR");
			roleAdmin.setCreationDate(new Date(System.currentTimeMillis()));
			roleAdmin.setLastModifiedDate(new Date(System.currentTimeMillis()));
			roleRepository.save(roleAdmin);
		}

		final String userSql = "SELECT * FROM user";
		final List<User> userList = jdbcTemplate.query(userSql, (resultSet, rowNum) ->
				null);

		if(userList.isEmpty()) {
			User user = User.builder().build();
			user.setCreationDate(new Date(System.currentTimeMillis()));
			user.setLastModifiedDate(new Date(System.currentTimeMillis()));
			user.setFirstname("ADMIN");
			user.setLastname("USER");
			user.setEmail("adminuser@gmail.com");
			user.setPassword(passwordEncoder.encode("1234"));
			user.setPhoneNumber("123456789");
			user.setRole(roleAdmin);

			userRepository.save(user);
		}
	}
}
