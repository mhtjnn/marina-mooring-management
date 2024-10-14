package com.marinamooringmanagement.model.request;

import com.marinamooringmanagement.constants.AppConstants;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

/**
 * Represents a base class for defining search criteria in various search requests.
 *
 * <p>This class is annotated with {@code MappedSuperclass} to indicate that its fields should be persisted in subclasses' tables.
 * It provides common fields and methods for pagination and sorting in search requests.
 *
 * @see MappedSuperclass
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseSearchRequest {

    /**
     * The size of each page in paginated results.
     */
    private Integer pageSize = Integer.valueOf(AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE);

    /**
     * The page number to retrieve in paginated results.
     */
    private Integer pageNumber = Integer.valueOf(AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM);

    /**
     * The sort order for results.
     */
    private String sortBy;

    /**
     * The sort direction for results.
     */
    private String sortDir;
}