package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.response.metadata.VendorMetadataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Vendor entities.
 */
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {

    Page<Vendor> findAll(final Specification<Vendor> spec, final Pageable pageable);

    List<Vendor> findAll(final Specification<Vendor> spec);

    Optional<Vendor> findByCompanyEmail(final String companyEmail);

    Optional<Vendor> findByRemitEmailAddress(final String remitEmailAddress);

    Optional<Vendor> findBySalesRepEmail(final String salesRepEmail);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Vendor(" +
            "v.id, v.vendorName, v.companyPhoneNumber, v.website, v.address, " +
            "s.id, s.name, c.id, c.name, v.zipCode, v.companyEmail, v.accountNumber, " +
            "v.remitAddress, rs.id, rs.name, rc.id, rc.name, " +
            "v.remitZipCode, v.remitEmailAddress, v.firstName, v.lastName, v.salesRepPhoneNumber, " +
            "v.salesRepEmail, v.salesRepNote, u.id, u.firstName, u.lastName) " +
            "FROM Vendor v " +
            "LEFT JOIN v.state s " +
            "LEFT JOIN v.country c " +
            "LEFT JOIN v.remitState rs " +
            "LEFT JOIN v.remitCountry rc " +
            "LEFT JOIN v.user u " +
            "WHERE v.id = :userId")
    Optional<Vendor> findById(@Param("userId") Integer id);

    @Query("SELECT new com.marinamooringmanagement.model.response.metadata.VendorMetadataResponse(" +
            "v.id, v.vendorName) " +
            "FROM Vendor v " +
            "LEFT JOIN v.user u " +
            "WHERE u.id = :userId")
    List<VendorMetadataResponse> findAllByUserIdMetadata(
            @Param("userId") Integer id
    );
}