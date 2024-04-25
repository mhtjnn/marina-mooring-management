package com.marinamooringmanagement.model.request;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Sort;

/**
 * Represents a base search request with common pagination and sorting parameters.
 *
 * <p>This class is annotated with {@code @MappedSuperclass} to indicate that it should be mapped to the corresponding fields in subclasses.
 *
 * <p>It provides properties for pagination (page number, page size) and sorting criteria.
 *
 * @see Sort
 */
@Data
@ToString
@EqualsAndHashCode
@MappedSuperclass
public class BaseSearchRequest {

    /**
     * The page number for pagination.
     */
    private Integer pageNumber;

    /**
     * The page size for pagination.
     */
    private Integer pageSize;

    /**
     * The sorting criteria for the search results.
     */
    private Sort sort;

    /**
     * Constructs a {@code Sort} object based on the provided sorting parameters.
     *
     * @param sortBy   The field to sort by.
     * @param sortDir  The direction of sorting (either "asc" for ascending or "desc" for descending).
     * @return A {@code Sort} object representing the sorting criteria.
     */
    public Sort getSort(String sortBy, String sortDir) {
        return sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    }
}
