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

        if (countryList.isEmpty()) {
            Country country = Country.builder()
                    .name("USA")
                    .label("United States of America")
                    .build();

            savedCountry = countryRepository.save(country);
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

        final String sizeOfWeightSql = "SELECT * FROM size_of_weight";
        final List<SizeOfWeight> sizeOfWeightList = jdbcTemplate.query(sizeOfWeightSql, (resultSet, rowNum) -> null);

        if (sizeOfWeightList.isEmpty()) {
            List<SizeOfWeight> sizeOfWeights = List.of(
                    SizeOfWeight.builder().weight("100-400").unitType("kg").build(), // Dinghy
                    SizeOfWeight.builder().weight("14-32").unitType("kg").build(), // Kayak
                    SizeOfWeight.builder().weight("23-45").unitType("kg").build(), // Canoe
                    SizeOfWeight.builder().weight("45-225").unitType("kg").build(), // Jon Boat
                    SizeOfWeight.builder().weight("680-1800").unitType("kg").build(), // Runabout
                    SizeOfWeight.builder().weight("900-2700").unitType("kg").build(), // Bowrider
                    SizeOfWeight.builder().weight("900-3600").unitType("kg").build(), // Center Console
                    SizeOfWeight.builder().weight("680-2300").unitType("kg").build(), // Pontoon Boat
                    SizeOfWeight.builder().weight("450-1100").unitType("kg").build(), // Bass Boat
                    SizeOfWeight.builder().weight("2300-9000").unitType("kg").build(), // Cabin Cruiser
                    SizeOfWeight.builder().weight("6800-31800").unitType("kg").build(), // Trawler
                    SizeOfWeight.builder().weight("3600-36300").unitType("kg").build(), // Sportfishing Boat
                    SizeOfWeight.builder().weight("9000-36300").unitType("kg").build(), // Houseboat
                    SizeOfWeight.builder().weight("45-225").unitType("kg").build(), // Small Sailboat (Dinghy)
                    SizeOfWeight.builder().weight("900-6800").unitType("kg").build(), // Sloop
                    SizeOfWeight.builder().weight("1800-9000").unitType("kg").build(), // Cutter
                    SizeOfWeight.builder().weight("3600-18000").unitType("kg").build(), // Ketch/Yawl
                    SizeOfWeight.builder().weight("1400-13600").unitType("kg").build(), // Catamaran
                    SizeOfWeight.builder().weight("1400-11300").unitType("kg").build(), // Trimaran
                    SizeOfWeight.builder().weight("36300-227000").unitType("kg").build(), // Luxury Yacht
                    SizeOfWeight.builder().weight("227000+").unitType("kg").build(), // Mega Yacht
                    SizeOfWeight.builder().weight("22700-227000").unitType("kg").build(), // Tugboat
                    SizeOfWeight.builder().weight("45300-2268000").unitType("kg").build(), // Ferry
                    SizeOfWeight.builder().weight("9000-36300").unitType("kg").build() // Pilot Boat
            );

            sizeOfWeightRepository.saveAll(sizeOfWeights);
        }

        final String typeOfWeightSql = "SELECT * FROM type_of_weight";
        final List<TypeOfWeight> typeOfWeightList = jdbcTemplate.query(typeOfWeightSql, (resultSet, rowNum) -> null);

        if (typeOfWeightList.isEmpty()) {
            List<TypeOfWeight> typeOfWeights = List.of(
                    TypeOfWeight.builder()
                            .type("Displacement")
                            .description("The weight of the water a boat displaces when it is floating. This is equivalent to the boat's overall weight.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Dry Weight")
                            .description("The weight of the boat without any fluids, passengers, cargo, or additional equipment. It includes the hull, engine, and basic fixtures.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Gross Weight")
                            .description("The total weight of the boat including fluids (like fuel and water), passengers, cargo, and any additional equipment.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Load Capacity")
                            .description("The maximum weight a boat is designed to carry, including passengers, cargo, and equipment.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Ballast")
                            .description("Weight added to a boat to improve stability and balance. Often found in sailboats to counteract the force of the wind on the sails.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Trailer Weight")
                            .description("The weight of the boat plus the weight of the trailer used to transport it.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Net Weight")
                            .description("The weight of the boat excluding certain removable items like fuel, water, cargo, and sometimes even passengers.")
                            .build(),
                    TypeOfWeight.builder()
                            .type("Heeling Weight")
                            .description("The effective weight experienced by a boat due to the force of the wind and the angle of heel.")
                            .build()
            );

            typeOfWeightRepository.saveAll(typeOfWeights);
        }

        final String topChainConditionSql = "SELECT * FROM top_chain_condition";
        final List<TopChainCondition> topChainConditionList = jdbcTemplate.query(topChainConditionSql, (resultSet, rowNum) -> null);

        if (topChainConditionList.isEmpty()) {
            List<TopChainCondition> topChainConditions = List.of(
                    TopChainCondition.builder()
                            .condition("New Condition")
                            .description("The chain is brand new, with no signs of wear or corrosion.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Good Condition")
                            .description("The chain has been used but shows minimal signs of wear. There are no significant rust spots or weakened links.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Moderate Condition")
                            .description("The chain shows some signs of wear and light corrosion but remains structurally sound. Some galvanization may have worn off.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Worn Condition")
                            .description("The chain exhibits noticeable wear, including rust, weakened links, and loss of galvanization.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Corroded Condition")
                            .description("The chain has significant rust and corrosion. The metal may be flaking, and some links may be compromised.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Damaged Condition")
                            .description("The chain has visible damage such as bent or broken links, severe rust, and possible deformation.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Repaired Condition")
                            .description("The chain has undergone repairs to fix damaged or worn sections. Repairs may include welding, replacing individual links, or re-galvanizing.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Stretched Condition")
                            .description("The chain has elongated due to excessive load. Links may appear longer and thinner.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Well-Maintained Condition")
                            .description("Regular inspections, cleaning, and maintenance have been performed. The chain may show minor wear but is overall in excellent condition.")
                            .build(),
                    TopChainCondition.builder()
                            .condition("Neglected Condition")
                            .description("The chain has been ignored and not maintained properly. Heavy rust, corrosion, and wear are present.")
                            .build()
            );

            topChainConditionRepository.saveAll(topChainConditions);
        }

        final String eyeConditionSql = "SELECT * FROM eye_condition";
        final List<EyeCondition> eyeConditionList = jdbcTemplate.query(eyeConditionSql, (resultSet, rowNum) -> null);

        if (eyeConditionList.isEmpty()) {
            List<EyeCondition> eyeConditions = List.of(
                    EyeCondition.builder()
                            .condition("New Condition")
                            .description("The eye is brand new with no signs of wear, corrosion, or damage.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Good Condition")
                            .description("The eye has been used but shows minimal signs of wear. No significant rust, bending, or damage is present.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Moderate Condition")
                            .description("The eye shows some signs of wear and light corrosion but remains structurally sound. Some minor wear or surface rust may be present.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Worn Condition")
                            .description("The eye exhibits noticeable wear, including rust, minor bending, or light pitting. Some loss of protective coating might be evident.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Corroded Condition")
                            .description("The eye has significant rust and corrosion. Metal may be flaking, and there may be visible weakening.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Damaged Condition")
                            .description("The eye has visible damage such as severe rust, cracks, bending, or deformation.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Repaired Condition")
                            .description("The eye has undergone repairs to fix damage or wear. Repairs may include welding, straightening, or re-coating.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Stretched Condition")
                            .description("The eye has elongated or been deformed due to excessive load. The eye may appear misshapen.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Well-Maintained Condition")
                            .description("Regular inspections, cleaning, and maintenance have been performed. The eye may show minor wear but is overall in excellent condition.")
                            .build(),
                    EyeCondition.builder()
                            .condition("Neglected Condition")
                            .description("The eye has been ignored and not maintained properly. Heavy rust, corrosion, wear, and possible damage are present.")
                            .build()
            );

            eyeConditionRepository.saveAll(eyeConditions);
        }

        final String bottomChainConditionSql = "SELECT * FROM bottom_chain_condition";
        final List<BottomChainCondition> bottomChainConditionList = jdbcTemplate.query(bottomChainConditionSql, (resultSet, resultNum) -> null);

        if (bottomChainConditionList.isEmpty()) {
            List<BottomChainCondition> bottomChainConditions = List.of(
                    BottomChainCondition.builder()
                            .condition("New Condition")
                            .description("The bottom chain is brand new with no signs of wear, corrosion, or damage.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Good Condition")
                            .description("The bottom chain has been used but shows minimal signs of wear. There are no significant rust spots or weakened links.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Moderate Condition")
                            .description("The bottom chain shows some signs of wear and light corrosion but remains structurally sound. Some galvanization may have worn off.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Worn Condition")
                            .description("The bottom chain exhibits noticeable wear, including rust, weakened links, and loss of galvanization.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Corroded Condition")
                            .description("The bottom chain has significant rust and corrosion. Metal may be flaking, and some links may be compromised.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Damaged Condition")
                            .description("The bottom chain has visible damage such as severe rust, cracks, bending, or deformation.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Repaired Condition")
                            .description("The bottom chain has undergone repairs to fix damaged or worn sections. Repairs may include welding, replacing individual links, or re-galvanizing.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Stretched Condition")
                            .description("The bottom chain has elongated due to excessive load. Links may appear longer and thinner.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Well-Maintained Condition")
                            .description("Regular inspections, cleaning, and maintenance have been performed. The bottom chain may show minor wear but is overall in excellent condition.")
                            .build(),
                    BottomChainCondition.builder()
                            .condition("Neglected Condition")
                            .description("The bottom chain has been ignored and not maintained properly. Heavy rust, corrosion, wear, and possible damage are present.")
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
    }
}
