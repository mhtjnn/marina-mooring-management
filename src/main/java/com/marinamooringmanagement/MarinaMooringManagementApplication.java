package com.marinamooringmanagement;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
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

    @Autowired
    private MooringStatusRepository mooringStatusRepository;

    @Autowired
    private BoatTypeRepository boatTypeRepository;

    @Autowired
    private SizeOfWeightRepository sizeOfWeightRepository;

    @Autowired
    private TypeOfWeightRepository typeOfWeightRepository;

    @Autowired
    private TopChainConditionRepository topChainConditionRepository;

    @Autowired
    private EyeConditionRepository eyeConditionRepository;

    @Autowired
    private BottomChainConditionRepository bottomChainConditionRepository;

    @Autowired
    private ShackleSwivelConditionRepository shackleSwivelConditionRepository;

    @Autowired
    private PennantConditionRepository pennantConditionRepository;

	@Autowired
	private InventoryTypeRepository inventoryTypeRepository;

    @Autowired
    private WorkOrderStatusRepository workOrderStatusRepository;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

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

        if (roleList.isEmpty()) {
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

        if (stateList.isEmpty()) {
            List<State> states = Arrays.asList(
                    State.builder().name("Alabama").label("Alabama").build(),
                    State.builder().name("Alaska").label("Alaska").build(),
                    State.builder().name("Arizona").label("Arizona").build(),
                    State.builder().name("Arkansas").label("Arkansas").build(),
                    State.builder().name("California").label("California").build(),
                    State.builder().name("Colorado").label("Colorado").build(),
                    State.builder().name("Connecticut").label("Connecticut").build(),
                    State.builder().name("Delaware").label("Delaware").build(),
                    State.builder().name("Florida").label("Florida").build(),
                    State.builder().name("Georgia").label("Georgia").build(),
                    State.builder().name("Hawaii").label("Hawaii").build(),
                    State.builder().name("Idaho").label("Idaho").build(),
                    State.builder().name("Illinois").label("Illinois").build(),
                    State.builder().name("Indiana").label("Indiana").build(),
                    State.builder().name("Iowa").label("Iowa").build(),
                    State.builder().name("Kansas").label("Kansas").build(),
                    State.builder().name("Kentucky").label("Kentucky").build(),
                    State.builder().name("Louisiana").label("Louisiana").build(),
                    State.builder().name("Maine").label("Maine").build(),
                    State.builder().name("Maryland").label("Maryland").build(),
                    State.builder().name("Massachusetts").label("Massachusetts").build(),
                    State.builder().name("Michigan").label("Michigan").build(),
                    State.builder().name("Minnesota").label("Minnesota").build(),
                    State.builder().name("Mississippi").label("Mississippi").build(),
                    State.builder().name("Missouri").label("Missouri").build(),
                    State.builder().name("Montana").label("Montana").build(),
                    State.builder().name("Nebraska").label("Nebraska").build(),
                    State.builder().name("Nevada").label("Nevada").build(),
                    State.builder().name("New Hampshire").label("New Hampshire").build(),
                    State.builder().name("New Jersey").label("New Jersey").build(),
                    State.builder().name("New Mexico").label("New Mexico").build(),
                    State.builder().name("New York").label("New York").build(),
                    State.builder().name("North Carolina").label("North Carolina").build(),
                    State.builder().name("North Dakota").label("North Dakota").build(),
                    State.builder().name("Ohio").label("Ohio").build(),
                    State.builder().name("Oklahoma").label("Oklahoma").build(),
                    State.builder().name("Oregon").label("Oregon").build(),
                    State.builder().name("Pennsylvania").label("Pennsylvania").build(),
                    State.builder().name("Rhode Island").label("Rhode Island").build(),
                    State.builder().name("South Carolina").label("South Carolina").build(),
                    State.builder().name("South Dakota").label("South Dakota").build(),
                    State.builder().name("Tennessee").label("Tennessee").build(),
                    State.builder().name("Texas").label("Texas").build(),
                    State.builder().name("Utah").label("Utah").build(),
                    State.builder().name("Vermont").label("Vermont").build(),
                    State.builder().name("Virginia").label("Virginia").build(),
                    State.builder().name("Washington").label("Washington").build(),
                    State.builder().name("West Virginia").label("West Virginia").build(),
                    State.builder().name("Wisconsin").label("Wisconsin").build(),
                    State.builder().name("Wyoming").label("Wyoming").build()
            );

            stateRepository.saveAll(states);
            savedState = stateRepository.findByName("New York").orElseThrow(() -> new RuntimeException("No state found with name: New York"));
        }

        Country savedCountry = null;
        final String countrySql = "SELECT * FROM country";
        final List<Role> countryList = jdbcTemplate.query(countrySql, (resultSet, rowNum) ->
                null);

        if (countryList.isEmpty()) {
            List<Country> countries = Arrays.asList(
                    Country.builder().name("Afghanistan").label("Afghanistan").build(),
                    Country.builder().name("Albania").label("Albania").build(),
                    Country.builder().name("Algeria").label("Algeria").build(),
                    Country.builder().name("American Samoa").label("American Samoa").build(),
                    Country.builder().name("Andorra").label("Andorra").build(),
                    Country.builder().name("Angola").label("Angola").build(),
                    Country.builder().name("Anguilla").label("Anguilla").build(),
                    Country.builder().name("Antarctica").label("Antarctica").build(),
                    Country.builder().name("Antigua and Barbuda").label("Antigua and Barbuda").build(),
                    Country.builder().name("Argentina").label("Argentina").build(),
                    Country.builder().name("Armenia").label("Armenia").build(),
                    Country.builder().name("Aruba").label("Aruba").build(),
                    Country.builder().name("Australia").label("Australia").build(),
                    Country.builder().name("Austria").label("Austria").build(),
                    Country.builder().name("Azerbaijan").label("Azerbaijan").build(),
                    Country.builder().name("Bahamas").label("Bahamas").build(),
                    Country.builder().name("Bahrain").label("Bahrain").build(),
                    Country.builder().name("Bangladesh").label("Bangladesh").build(),
                    Country.builder().name("Barbados").label("Barbados").build(),
                    Country.builder().name("Belarus").label("Belarus").build(),
                    Country.builder().name("Belgium").label("Belgium").build(),
                    Country.builder().name("Belize").label("Belize").build(),
                    Country.builder().name("Benin").label("Benin").build(),
                    Country.builder().name("Bermuda").label("Bermuda").build(),
                    Country.builder().name("Bhutan").label("Bhutan").build(),
                    Country.builder().name("Bolivia").label("Bolivia").build(),
                    Country.builder().name("Bosnia and Herzegovina").label("Bosnia and Herzegovina").build(),
                    Country.builder().name("Botswana").label("Botswana").build(),
                    Country.builder().name("Brazil").label("Brazil").build(),
                    Country.builder().name("Brunei").label("Brunei").build(),
                    Country.builder().name("Bulgaria").label("Bulgaria").build(),
                    Country.builder().name("Burkina Faso").label("Burkina Faso").build(),
                    Country.builder().name("Burundi").label("Burundi").build(),
                    Country.builder().name("Cabo Verde").label("Cabo Verde").build(),
                    Country.builder().name("Cambodia").label("Cambodia").build(),
                    Country.builder().name("Cameroon").label("Cameroon").build(),
                    Country.builder().name("Canada").label("Canada").build(),
                    Country.builder().name("Central African Republic").label("Central African Republic").build(),
                    Country.builder().name("Chad").label("Chad").build(),
                    Country.builder().name("Chile").label("Chile").build(),
                    Country.builder().name("China").label("China").build(),
                    Country.builder().name("Colombia").label("Colombia").build(),
                    Country.builder().name("Comoros").label("Comoros").build(),
                    Country.builder().name("Congo, Democratic Republic of the").label("Congo, Democratic Republic of the").build(),
                    Country.builder().name("Congo, Republic of the").label("Congo, Republic of the").build(),
                    Country.builder().name("Costa Rica").label("Costa Rica").build(),
                    Country.builder().name("Croatia").label("Croatia").build(),
                    Country.builder().name("Cuba").label("Cuba").build(),
                    Country.builder().name("Cyprus").label("Cyprus").build(),
                    Country.builder().name("Czech Republic").label("Czech Republic").build(),
                    Country.builder().name("Denmark").label("Denmark").build(),
                    Country.builder().name("Djibouti").label("Djibouti").build(),
                    Country.builder().name("Dominica").label("Dominica").build(),
                    Country.builder().name("Dominican Republic").label("Dominican Republic").build(),
                    Country.builder().name("Ecuador").label("Ecuador").build(),
                    Country.builder().name("Egypt").label("Egypt").build(),
                    Country.builder().name("El Salvador").label("El Salvador").build(),
                    Country.builder().name("Equatorial Guinea").label("Equatorial Guinea").build(),
                    Country.builder().name("Eritrea").label("Eritrea").build(),
                    Country.builder().name("Estonia").label("Estonia").build(),
                    Country.builder().name("Eswatini").label("Eswatini").build(),
                    Country.builder().name("Ethiopia").label("Ethiopia").build(),
                    Country.builder().name("Fiji").label("Fiji").build(),
                    Country.builder().name("Finland").label("Finland").build(),
                    Country.builder().name("France").label("France").build(),
                    Country.builder().name("Gabon").label("Gabon").build(),
                    Country.builder().name("Gambia").label("Gambia").build(),
                    Country.builder().name("Georgia").label("Georgia").build(),
                    Country.builder().name("Germany").label("Germany").build(),
                    Country.builder().name("Ghana").label("Ghana").build(),
                    Country.builder().name("Greece").label("Greece").build(),
                    Country.builder().name("Grenada").label("Grenada").build(),
                    Country.builder().name("Guatemala").label("Guatemala").build(),
                    Country.builder().name("Guinea").label("Guinea").build(),
                    Country.builder().name("Guinea-Bissau").label("Guinea-Bissau").build(),
                    Country.builder().name("Guyana").label("Guyana").build(),
                    Country.builder().name("Haiti").label("Haiti").build(),
                    Country.builder().name("Honduras").label("Honduras").build(),
                    Country.builder().name("Hungary").label("Hungary").build(),
                    Country.builder().name("Iceland").label("Iceland").build(),
                    Country.builder().name("India").label("India").build(),
                    Country.builder().name("Indonesia").label("Indonesia").build(),
                    Country.builder().name("Iran").label("Iran").build(),
                    Country.builder().name("Iraq").label("Iraq").build(),
                    Country.builder().name("Ireland").label("Ireland").build(),
                    Country.builder().name("Israel").label("Israel").build(),
                    Country.builder().name("Italy").label("Italy").build(),
                    Country.builder().name("Jamaica").label("Jamaica").build(),
                    Country.builder().name("Japan").label("Japan").build(),
                    Country.builder().name("Jordan").label("Jordan").build(),
                    Country.builder().name("Kazakhstan").label("Kazakhstan").build(),
                    Country.builder().name("Kenya").label("Kenya").build(),
                    Country.builder().name("Kiribati").label("Kiribati").build(),
                    Country.builder().name("Korea, North").label("Korea, North").build(),
                    Country.builder().name("Korea, South").label("Korea, South").build(),
                    Country.builder().name("Kuwait").label("Kuwait").build(),
                    Country.builder().name("Kyrgyzstan").label("Kyrgyzstan").build(),
                    Country.builder().name("Laos").label("Laos").build(),
                    Country.builder().name("Latvia").label("Latvia").build(),
                    Country.builder().name("Lebanon").label("Lebanon").build(),
                    Country.builder().name("Lesotho").label("Lesotho").build(),
                    Country.builder().name("Liberia").label("Liberia").build(),
                    Country.builder().name("Libya").label("Libya").build(),
                    Country.builder().name("Liechtenstein").label("Liechtenstein").build(),
                    Country.builder().name("Lithuania").label("Lithuania").build(),
                    Country.builder().name("Luxembourg").label("Luxembourg").build(),
                    Country.builder().name("Madagascar").label("Madagascar").build(),
                    Country.builder().name("Malawi").label("Malawi").build(),
                    Country.builder().name("Malaysia").label("Malaysia").build(),
                    Country.builder().name("Maldives").label("Maldives").build(),
                    Country.builder().name("Mali").label("Mali").build(),
                    Country.builder().name("Malta").label("Malta").build(),
                    Country.builder().name("Marshall Islands").label("Marshall Islands").build(),
                    Country.builder().name("Mauritania").label("Mauritania").build(),
                    Country.builder().name("Mauritius").label("Mauritius").build(),
                    Country.builder().name("Mexico").label("Mexico").build(),
                    Country.builder().name("Micronesia").label("Micronesia").build(),
                    Country.builder().name("Moldova").label("Moldova").build(),
                    Country.builder().name("Monaco").label("Monaco").build(),
                    Country.builder().name("Mongolia").label("Mongolia").build(),
                    Country.builder().name("Montenegro").label("Montenegro").build(),
                    Country.builder().name("Morocco").label("Morocco").build(),
                    Country.builder().name("Mozambique").label("Mozambique").build(),
                    Country.builder().name("Myanmar").label("Myanmar").build(),
                    Country.builder().name("Namibia").label("Namibia").build(),
                    Country.builder().name("Nauru").label("Nauru").build(),
                    Country.builder().name("Nepal").label("Nepal").build(),
                    Country.builder().name("Netherlands").label("Netherlands").build(),
                    Country.builder().name("New Zealand").label("New Zealand").build(),
                    Country.builder().name("Nicaragua").label("Nicaragua").build(),
                    Country.builder().name("Niger").label("Niger").build(),
                    Country.builder().name("Nigeria").label("Nigeria").build(),
                    Country.builder().name("North Macedonia").label("North Macedonia").build(),
                    Country.builder().name("Norway").label("Norway").build(),
                    Country.builder().name("Oman").label("Oman").build(),
                    Country.builder().name("Pakistan").label("Pakistan").build(),
                    Country.builder().name("Palau").label("Palau").build(),
                    Country.builder().name("Panama").label("Panama").build(),
                    Country.builder().name("Papua New Guinea").label("Papua New Guinea").build(),
                    Country.builder().name("Paraguay").label("Paraguay").build(),
                    Country.builder().name("Peru").label("Peru").build(),
                    Country.builder().name("Philippines").label("Philippines").build(),
                    Country.builder().name("Poland").label("Poland").build(),
                    Country.builder().name("Portugal").label("Portugal").build(),
                    Country.builder().name("Qatar").label("Qatar").build(),
                    Country.builder().name("Romania").label("Romania").build(),
                    Country.builder().name("Russia").label("Russia").build(),
                    Country.builder().name("Rwanda").label("Rwanda").build(),
                    Country.builder().name("Saint Kitts and Nevis").label("Saint Kitts and Nevis").build(),
                    Country.builder().name("Saint Lucia").label("Saint Lucia").build(),
                    Country.builder().name("Saint Vincent and the Grenadines").label("Saint Vincent and the Grenadines").build(),
                    Country.builder().name("Samoa").label("Samoa").build(),
                    Country.builder().name("San Marino").label("San Marino").build(),
                    Country.builder().name("Sao Tome and Principe").label("Sao Tome and Principe").build(),
                    Country.builder().name("Saudi Arabia").label("Saudi Arabia").build(),
                    Country.builder().name("Senegal").label("Senegal").build(),
                    Country.builder().name("Serbia").label("Serbia").build(),
                    Country.builder().name("Seychelles").label("Seychelles").build(),
                    Country.builder().name("Sierra Leone").label("Sierra Leone").build(),
                    Country.builder().name("Singapore").label("Singapore").build(),
                    Country.builder().name("Slovakia").label("Slovakia").build(),
                    Country.builder().name("Slovenia").label("Slovenia").build(),
                    Country.builder().name("Solomon Islands").label("Solomon Islands").build(),
                    Country.builder().name("Somalia").label("Somalia").build(),
                    Country.builder().name("South Africa").label("South Africa").build(),
                    Country.builder().name("South Sudan").label("South Sudan").build(),
                    Country.builder().name("Spain").label("Spain").build(),
                    Country.builder().name("Sri Lanka").label("Sri Lanka").build(),
                    Country.builder().name("Sudan").label("Sudan").build(),
                    Country.builder().name("Suriname").label("Suriname").build(),
                    Country.builder().name("Sweden").label("Sweden").build(),
                    Country.builder().name("Switzerland").label("Switzerland").build(),
                    Country.builder().name("Syria").label("Syria").build(),
                    Country.builder().name("Taiwan").label("Taiwan").build(),
                    Country.builder().name("Tajikistan").label("Tajikistan").build(),
                    Country.builder().name("Tanzania").label("Tanzania").build(),
                    Country.builder().name("Thailand").label("Thailand").build(),
                    Country.builder().name("Timor-Leste").label("Timor-Leste").build(),
                    Country.builder().name("Togo").label("Togo").build(),
                    Country.builder().name("Tonga").label("Tonga").build(),
                    Country.builder().name("Trinidad and Tobago").label("Trinidad and Tobago").build(),
                    Country.builder().name("Tunisia").label("Tunisia").build(),
                    Country.builder().name("Turkey").label("Turkey").build(),
                    Country.builder().name("Turkmenistan").label("Turkmenistan").build(),
                    Country.builder().name("Tuvalu").label("Tuvalu").build(),
                    Country.builder().name("Uganda").label("Uganda").build(),
                    Country.builder().name("Ukraine").label("Ukraine").build(),
                    Country.builder().name("United Arab Emirates").label("United Arab Emirates").build(),
                    Country.builder().name("United Kingdom").label("United Kingdom").build(),
                    Country.builder().name("United States").label("United States of America").build(),
                    Country.builder().name("Uruguay").label("Uruguay").build(),
                    Country.builder().name("Uzbekistan").label("Uzbekistan").build(),
                    Country.builder().name("Vanuatu").label("Vanuatu").build(),
                    Country.builder().name("Vatican City").label("Vatican City").build(),
                    Country.builder().name("Venezuela").label("Venezuela").build(),
                    Country.builder().name("Vietnam").label("Vietnam").build(),
                    Country.builder().name("Yemen").label("Yemen").build(),
                    Country.builder().name("Zambia").label("Zambia").build(),
                    Country.builder().name("Zimbabwe").label("Zimbabwe").build()
            );

            countryRepository.saveAll(countries);
            savedCountry = countryRepository.findByName("United States").orElseThrow(() -> new RuntimeException("No country found with name: United States"));
        }

        final String userSql = "SELECT * FROM _user";
        final List<User> userList = jdbcTemplate.query(userSql, (resultSet, rowNum) -> null);

        if (userList.isEmpty()) {
            User user = User.builder().build();
            user.setId(1);
            user.setCreationDate(new Date(System.currentTimeMillis()));
            user.setLastModifiedDate(new Date(System.currentTimeMillis()));
            user.setName("Matt Pusey");
            user.setEmail("matt.pusey@moorfind.com");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setPhoneNumber("1234567890");
            user.setStreet("17 street");
            user.setApt("apt");
            user.setZipCode("12345");
            user.setRole(roleOwner);
            user.setCreatedBy("System");
            user.setLastModifiedBy("System");
            user.setState(savedState);
            user.setCountry(savedCountry);
            userRepository.save(user);
        }

        final String mooringStatusSql = "SELECT * FROM mooring_status";
        final List<MooringStatus> mooringStatusList = jdbcTemplate.query(mooringStatusSql, (resultSet, rowNum) -> null);

        if (mooringStatusList.isEmpty()) {
            MooringStatus mooringStatusGEAR_IN = MooringStatus.builder()
                    .status(AppConstants.Status.GEAR_IN)
                    .description("Refers to the equipment and tools used for securing a vessel to a dock, pier, or mooring buoy. This gear includes a variety of items that work together to ensure that the vessel remains stationary and safe while docked or anchored.")
                    .build();

            mooringStatusRepository.save(mooringStatusGEAR_IN);

            MooringStatus mooringStatusGEAR_OFF = MooringStatus.builder()
                    .status(AppConstants.Status.GEAR_OFF)
                    .description("Refers to disengaging the engine's gear, which stops the propeller from turning and the boat from moving forward or backward. This action is part of controlling the boat's movement and is essential for safe docking, anchoring, or stopping.")
                    .build();

            mooringStatusRepository.save(mooringStatusGEAR_OFF);

            MooringStatus mooringStatusNEED_INSPECTION = MooringStatus.builder()
                    .status(AppConstants.Status.NEED_INSPECTION)
                    .description("Refers to a status or a condition indicating that an inspection is required. This could apply to various components of the boat or mooring equipment to ensure they are in good working order and safe to use.")
                    .build();

            mooringStatusRepository.save(mooringStatusNEED_INSPECTION);

            MooringStatus mooringStatusNOT_IN_USE = MooringStatus.builder()
                    .status(AppConstants.Status.NOT_IN_USE)
                    .description("Indicating that a particular piece of equipment, a specific part of the vessel, or even the entire vessel is not currently being used. This status can help in organizing, maintaining, and ensuring safety in operations.")
                    .build();

            mooringStatusRepository.save(mooringStatusNOT_IN_USE);
        }

        final String boatTypeSql = "SELECT * FROM boat_type";
        final List<BoatType> boatTypeList = jdbcTemplate.query(boatTypeSql, (resultSet, rowNum) -> null);

        if (boatTypeList.isEmpty()) {
            List<BoatType> boatTypes = List.of(
                    BoatType.builder()
                            .boatType("Sailboats")
                            .description("A sailboat is a type of boat that is primarily propelled by sails. Here are the key aspects and components of a sailboat.")
                            .build(),
                    BoatType.builder()
                            .boatType("Powerboats")
                            .description("A powerboat is a boat that is powered by an engine. Powerboats are fast and used for activities like cruising, waterskiing, and fishing.")
                            .build(),
                    BoatType.builder()
                            .boatType("Fishing Boats")
                            .description("Fishing boats are designed for fishing activities. They come in various sizes and are equipped with features like bait wells and fishing rod holders.")
                            .build(),
                    BoatType.builder()
                            .boatType("Personal Watercraft (PWC)")
                            .description("Personal Watercraft, like Jet Skis, are small, fast watercraft powered by a jet drive and are used for recreation.")
                            .build(),
                    BoatType.builder()
                            .boatType("Houseboats")
                            .description("Houseboats are boats designed for living on the water, equipped with full living amenities like kitchens, bathrooms, and bedrooms.")
                            .build(),
                    BoatType.builder()
                            .boatType("Inflatables")
                            .description("Inflatable boats are lightweight and buoyant. They come in rigid (RIB) and soft (SIB) variants and are used for various purposes including as tenders.")
                            .build(),
                    BoatType.builder()
                            .boatType("Commercial and Utility Boats")
                            .description("Commercial and utility boats, such as tugboats, ferries, and pilot boats, are designed for specific tasks like towing, transporting passengers, and guiding ships.")
                            .build(),
                    BoatType.builder()
                            .boatType("Specialty Boats")
                            .description("Specialty boats include unique types like catamarans, trimarans, dinghies, kayaks, and canoes, each designed for specific activities and conditions.")
                            .build(),
                    BoatType.builder()
                            .boatType("Amphibious Boats")
                            .description("Amphibious boats are capable of operation on both land and water, often used for military or rescue purposes.")
                            .build()
            );
            boatTypeRepository.saveAll(boatTypes);
        }

//        final String sizeOfWeightSql = "SELECT * FROM size_of_weight";
//        final List<SizeOfWeight> sizeOfWeightList = jdbcTemplate.query(sizeOfWeightSql, (resultSet, rowNum) -> null);
//
//        if (sizeOfWeightList.isEmpty()) {
//            List<SizeOfWeight> sizeOfWeights = List.of(
//                    SizeOfWeight.builder().weight("100-400").unitType("kg").build(), // Dinghy
//                    SizeOfWeight.builder().weight("14-32").unitType("kg").build(), // Kayak
//                    SizeOfWeight.builder().weight("23-45").unitType("kg").build(), // Canoe
//                    SizeOfWeight.builder().weight("45-225").unitType("kg").build(), // Jon Boat
//                    SizeOfWeight.builder().weight("680-1800").unitType("kg").build(), // Runabout
//                    SizeOfWeight.builder().weight("900-2700").unitType("kg").build(), // Bowrider
//                    SizeOfWeight.builder().weight("900-3600").unitType("kg").build(), // Center Console
//                    SizeOfWeight.builder().weight("680-2300").unitType("kg").build(), // Pontoon Boat
//                    SizeOfWeight.builder().weight("450-1100").unitType("kg").build(), // Bass Boat
//                    SizeOfWeight.builder().weight("2300-9000").unitType("kg").build(), // Cabin Cruiser
//                    SizeOfWeight.builder().weight("6800-31800").unitType("kg").build(), // Trawler
//                    SizeOfWeight.builder().weight("3600-36300").unitType("kg").build(), // Sportfishing Boat
//                    SizeOfWeight.builder().weight("9000-36300").unitType("kg").build(), // Houseboat
//                    SizeOfWeight.builder().weight("45-225").unitType("kg").build(), // Small Sailboat (Dinghy)
//                    SizeOfWeight.builder().weight("900-6800").unitType("kg").build(), // Sloop
//                    SizeOfWeight.builder().weight("1800-9000").unitType("kg").build(), // Cutter
//                    SizeOfWeight.builder().weight("3600-18000").unitType("kg").build(), // Ketch/Yawl
//                    SizeOfWeight.builder().weight("1400-13600").unitType("kg").build(), // Catamaran
//                    SizeOfWeight.builder().weight("1400-11300").unitType("kg").build(), // Trimaran
//                    SizeOfWeight.builder().weight("36300-227000").unitType("kg").build(), // Luxury Yacht
//                    SizeOfWeight.builder().weight("227000+").unitType("kg").build(), // Mega Yacht
//                    SizeOfWeight.builder().weight("22700-227000").unitType("kg").build(), // Tugboat
//                    SizeOfWeight.builder().weight("45300-2268000").unitType("kg").build(), // Ferry
//                    SizeOfWeight.builder().weight("9000-36300").unitType("kg").build() // Pilot Boat
//            );
//
//            sizeOfWeightRepository.saveAll(sizeOfWeights);
//        }

        final String typeOfWeightSql = "SELECT * FROM type_of_weight";
        final List<TypeOfWeight> typeOfWeightList = jdbcTemplate.query(typeOfWeightSql, (resultSet, rowNum) -> null);

        if (typeOfWeightList.isEmpty()) {
            List<TypeOfWeight> typeOfWeights = Arrays.asList(
                    TypeOfWeight.builder()
                            .type("Helix")
                            .description("Description for Helix weight type.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Pyramid")
                            .description("Description for Pyramid weight type.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Mushroom")
                            .description("Description for Mushroom weight type.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Counterweight")
                            .description("Description for Counterweight type.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Trainwheel")
                            .description("Description for Trainwheel weight type.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Other")
                            .description("Description for Other weight type.")
                            .build()
            );

            typeOfWeightRepository.saveAll(typeOfWeights);
        }

        final String topChainConditionSql = "SELECT * FROM top_chain_condition";
        final List<TopChainCondition> topChainConditionList = jdbcTemplate.query(topChainConditionSql, (resultSet, rowNum) -> null);

        if (topChainConditionList.isEmpty()) {
            List<TopChainCondition> topChainConditions = List.of(
                    TopChainCondition.builder()
                            .condition("New")
                            .description("The chain is brand new, with no signs of wear or corrosion.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Worn Condition")
                            .description("The chain has been used and shows signs of wear. There are significant rust spots or weakened links.")
                            .build()
            );

            topChainConditionRepository.saveAll(topChainConditions);
        }

        final String eyeConditionSql = "SELECT * FROM eye_condition";
        final List<EyeCondition> eyeConditionList = jdbcTemplate.query(eyeConditionSql, (resultSet, rowNum) -> null);

        if (eyeConditionList.isEmpty()) {
            List<EyeCondition> eyeConditions = List.of(
                    EyeCondition.builder()
                            .condition("New")
                            .description("The chain is brand new, with no signs of wear or corrosion.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Worn")
                            .description("The eye exhibits noticeable wear, including rust, minor bending, or light pitting. Some loss of protective coating might be evident.")
                            .build()
            );

            eyeConditionRepository.saveAll(eyeConditions);
        }

        final String bottomChainConditionSql = "SELECT * FROM bottom_chain_condition";
        final List<BottomChainCondition> bottomChainConditionList = jdbcTemplate.query(bottomChainConditionSql, (resultSet, resultNum) -> null);

        if (bottomChainConditionList.isEmpty()) {
            List<BottomChainCondition> bottomChainConditions = List.of(
                    BottomChainCondition.builder()
                            .condition("New")
                            .description("The bottom chain is brand new with no signs of wear, corrosion, or damage.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Worn")
                            .description("The bottom chain exhibits noticeable wear, including rust, weakened links, and loss of galvanization.")
                            .build()
            );

            bottomChainConditionRepository.saveAll(bottomChainConditions);
        }

        final String shackleSwivelConditionSql = "SELECT * FROM shackle_swivel_condition";
        final List<ShackleSwivelCondition> shackleSwivelConditionList = jdbcTemplate.query(shackleSwivelConditionSql, (resultSet, resultNum) -> null);

        if (shackleSwivelConditionList.isEmpty()) {
            List<ShackleSwivelCondition> shackleSwivelConditions = List.of(
                    ShackleSwivelCondition.builder()
                            .condition("New Condition")
                            .description("The shackle swivel is brand new, with no signs of wear or corrosion.")
                            .build(),
                    ShackleSwivelCondition.builder()
                            .condition("Good Condition")
                            .description("The shackle swivel has been used but shows minimal signs of wear. There are no significant rust spots or deformations.")
                            .build(),
                    ShackleSwivelCondition.builder()
                            .condition("Moderate Condition")
                            .description("The shackle swivel shows some signs of wear and light corrosion but remains structurally sound.")
                            .build(),
                    ShackleSwivelCondition.builder()
                            .condition("Worn Condition")
                            .description("The shackle swivel exhibits noticeable wear, including rust, deformation, or loss of functionality.")
                            .build(),
                    ShackleSwivelCondition.builder()
                            .condition("Corroded Condition")
                            .description("The shackle swivel has significant rust and corrosion. The metal may be flaking, and functionality may be compromised.")
                            .build(),
                    ShackleSwivelCondition.builder()
                            .condition("Damaged Condition")
                            .description("The shackle swivel has visible damage such as cracks, bends, or breakage.")
                            .build(),
                    ShackleSwivelCondition.builder()
                            .condition("Repaired Condition")
                            .description("The shackle swivel has undergone repairs to fix damaged or worn sections. Repairs may include welding or replacing components.")
                            .build(),
                    ShackleSwivelCondition.builder()
                            .condition("Stretched Condition")
                            .description("The shackle swivel has elongated due to excessive load or wear. Functionality may be compromised.")
                            .build(),
                    ShackleSwivelCondition.builder()
                            .condition("Well-Maintained Condition")
                            .description("Regular inspections, cleaning, and maintenance have been performed. The shackle swivel may show minor wear but is overall in excellent condition.")
                            .build(),
                    ShackleSwivelCondition.builder()
                            .condition("Neglected Condition")
                            .description("The shackle swivel has been ignored and not maintained properly. Heavy rust, corrosion, and wear are present.")
                            .build()
            );

            shackleSwivelConditionRepository.saveAll(shackleSwivelConditions);
        }

        final String pennantConditionSql = "SELECT * FROM pennant_condition";
        final List<PennantCondition> pennatConditionList = jdbcTemplate.query(pennantConditionSql, (resultSet, resultNum) -> null);

        if (pennatConditionList.isEmpty()) {
            List<PennantCondition> pennantConditions = List.of(
                    PennantCondition.builder()
                            .condition("New Condition")
                            .description("The pennant is brand new, with no signs of wear, fraying, or damage.")
                            .build(),
                    PennantCondition.builder()
                            .condition("Good Condition")
                            .description("The pennant has been used but shows minimal signs of wear. There are no significant frays, cuts, or weakened areas.")
                            .build(),
                    PennantCondition.builder()
                            .condition("Moderate Condition")
                            .description("The pennant shows some signs of wear, such as minor fraying or abrasions, but remains structurally sound.")
                            .build(),
                    PennantCondition.builder()
                            .condition("Worn Condition")
                            .description("The pennant exhibits noticeable wear, including fraying, cuts, or abrasions. Some sections may be weakened or damaged.")
                            .build(),
                    PennantCondition.builder()
                            .condition("Damaged Condition")
                            .description("The pennant has visible damage, such as significant fraying, cuts, or broken strands. It may be weakened or compromised.")
                            .build(),
                    PennantCondition.builder()
                            .condition("Severely Corroded Condition")
                            .description("If the pennant is made of wire, it may suffer from severe corrosion, leading to weakened or rusted sections.")
                            .build(),
                    PennantCondition.builder()
                            .condition("Tangled Condition")
                            .description("The pennant may become tangled or twisted, affecting its functionality and ease of use.")
                            .build()
            );

            pennantConditionRepository.saveAll(pennantConditions);
        }

        final String inventoryTypeSql = "SELECT * FROM inventory_type";
        final List<PennantCondition> inventoryTypeList = jdbcTemplate.query(inventoryTypeSql, (resultSet, resultNum) -> null);

        if (inventoryTypeList.isEmpty()) {
            List<InventoryType> inventoryTypes = List.of(
                    InventoryType.builder()
                            .type("Raw Materials Inventory")
                            .description("Basic materials used to produce goods")
                            .build(),
                    InventoryType.builder()
                            .type("Work-in-Progress (WIP) Inventory")
                            .description("Items that are in the process of being manufactured but are not yet complete")
                            .build(),
                    InventoryType.builder()
                            .type("Finished Goods Inventory")
                            .description("Completed products that are ready for sale")
                            .build(),
                    InventoryType.builder()
                            .type("Maintenance, Repair, and Operations (MRO) Inventory")
                            .description("Items used to support and maintain the production process and infrastructure")
                            .build(),
                    InventoryType.builder()
                            .type("Buffer Inventory (Safety Stock)")
                            .description("Extra inventory kept to prevent stockouts due to demand fluctuations or supply chain uncertainties")
                            .build(),
                    InventoryType.builder()
                            .type("Cycle Inventory")
                            .description("Inventory that is ordered and replenished on a regular basis as part of normal business operations")
                            .build(),
                    InventoryType.builder()
                            .type("Decoupling Inventory")
                            .description("Inventory kept to decouple or separate different parts of the production process to avoid dependencies and delays")
                            .build(),
                    InventoryType.builder()
                            .type("Transit Inventory (Pipeline Inventory)")
                            .description("Inventory that is currently being transported from one location to another")
                            .build(),
                    InventoryType.builder()
                            .type("Anticipation Inventory")
                            .description("Inventory accumulated in anticipation of future demand increases, such as seasonal spikes or promotional events")
                            .build(),
                    InventoryType.builder()
                            .type("Consignment Inventory")
                            .description("Inventory that is stored at the customer’s location but remains owned by the supplier until used or sold")
                            .build(),
                    InventoryType.builder()
                            .type("Perpetual Inventory")
                            .description("A system that tracks inventory levels in real-time through the use of technology and software")
                            .build(),
                    InventoryType.builder()
                            .type("Deadstock")
                            .description("Inventory that is not expected to be sold or used, often due to obsolescence or lack of demand")
                            .build()
            );

			inventoryTypeRepository.saveAll(inventoryTypes);
        }

        final String workOrderStatusSql = "SELECT * FROM work_order_status";
        final List<WorkOrderStatus> workOrderStatusList = jdbcTemplate.query(workOrderStatusSql, (resultSet, resultNum) -> null);

        if(workOrderStatusList.isEmpty()) {
            List<WorkOrderStatus> workOrderStatuses = List.of(
                    WorkOrderStatus.builder()
                            .status("New Request")
                            .description("The work order has been created and is awaiting assignment.")
                            .build(),
                    WorkOrderStatus.builder()
                            .status("Work in Progress")
                            .description("The work order is currently being worked on.")
                            .build(),
                    WorkOrderStatus.builder()
                            .status("Parts on Order")
                            .description("The required parts for the work order have been ordered and are awaited.")
                            .build(),
                    WorkOrderStatus.builder()
                            .status("Waiting on Inspection")
                            .description("The work has been completed and is awaiting inspection.")
                            .build(),
                    WorkOrderStatus.builder()
                            .status("On Hold")
                            .description("The work order is temporarily on hold.")
                            .build(),
                    WorkOrderStatus.builder()
                            .status("Pending Approval")
                            .description("The work order is awaiting approval from the relevant authority.")
                            .build(),
                    WorkOrderStatus.builder()
                            .status("Close")
                            .description("The work order has been completed and closed.")
                            .build()
            );

            workOrderStatusRepository.saveAll(workOrderStatuses);
        }

        final String customerTypeSql = "SELECT * FROM customer_type";
        final List<WorkOrderStatus> customerTypeList = jdbcTemplate.query(customerTypeSql, (resultSet, resultNum) -> null);

        if(customerTypeList.isEmpty()) {
            List<CustomerType> customerTypes = Arrays.asList(
                    CustomerType.builder()
                            .type(AppConstants.CustomerTypeConstants.NEW_INSTALL)
                            .description("New installation of services")
                            .build(),
                    CustomerType.builder()
                            .type(AppConstants.CustomerTypeConstants.RETAIL)
                            .description("Retail customer")
                            .build(),
                    CustomerType.builder()
                            .type(AppConstants.CustomerTypeConstants.INTERNET)
                            .description("Internet services customer")
                            .build(),
                    CustomerType.builder()
                            .type(AppConstants.CustomerTypeConstants.FORMER)
                            .description("Former customer")
                            .build(),
                    CustomerType.builder()
                            .type(AppConstants.CustomerTypeConstants.DOCK)
                            .description("Dock service customer")
                            .build()
            );

            customerTypeRepository.saveAll(customerTypes);
        }
    }
}
