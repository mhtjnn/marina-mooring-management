package com.marinamooringmanagement.model.request;

import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.springframework.data.domain.Sort;

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
    private Integer pageSize;

    /**
     * The page number to retrieve in paginated results.
     */
    private Integer pageNumber;

    /**
     * The sort order for results.
     */
    private String sortBy;

    /**
     * The sort direction for results.
     */
    private String sortDir;
}