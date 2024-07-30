package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.dto.metadata.CountryDto;
import com.marinamooringmanagement.model.dto.metadata.StateDto;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a vendor data transfer object (DTO) used for transferring vendor-related information.
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class VendorDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 55026860357976L;

    private Integer id;

    private String vendorName;

    /**
     * Phone number of the company associated with the vendor.
     */
    private String companyPhoneNumber;

    /**
     * Website URL of the company associated with the vendor.
     */
    private String website;

    /**
     * Street address of the vendor.
     */
    private String address;

    /**
     * State where the vendor is located.
     */
    private StateDto stateDto;

    /**
     * Country where the vendor is located.
     */
    private CountryDto countryDto;

    /**
     * ZIP code of the vendor's location.
     */
    private String zipCode;

    /**
     * Email address of the company associated with the vendor.
     */
    private String companyEmail;

    /**
     * Account number associated with the vendor.
     */
    private String accountNumber;

    private String remitAddress;

    private StateDto remitStateDto;

    private CountryDto remitCountryDto;

    private String remitZipCode;

    private String remitEmailAddress;

    /**
     * First name of the primary contact person associated with the vendor.
     */
    private String firstName;

    /**
     * Last name of the primary contact person associated with the vendor.
     */
    private String lastName;

    /**
     * Phone number of the sales representative associated with the vendor.
     */
    private String salesRepPhoneNumber;

    /**
     * Email address of the sales representative associated with the vendor.
     */
    private String salesRepEmail;

    /**
     * Note or additional information about the sales representative associated with the vendor.
     */
    private String salesRepNote;

    private List<InventoryDto> inventoryDtoList;
}
