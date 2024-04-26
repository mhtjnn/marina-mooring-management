package com.marinamooringmanagement.model.request;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
@MappedSuperclass
@ToString
@EqualsAndHashCode
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
    private Sort sort;

    /**
     * Constructs a {@code Sort} object based on the provided sorting parameters.
     *
     * @param sortBy   The field to sort by.
     * @param sortDir  The direction of sorting (either "asc" for ascending or "desc" for descending).
     * @return A {@code Sort} object representing the sorting criteria.
     */
    public Sort getSort(final String sortBy, final String sortDir) {
        return sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    }
}