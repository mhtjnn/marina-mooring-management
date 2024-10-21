package com.marinamooringmanagement.utils;

import org.springframework.data.domain.Sort;

public class SortUtils {

    /**
     * Constructs a {@code Sort} object based on the provided sorting parameters.
     *
     * @param sortBy   The field to sort by.
     * @param sortDir  The direction of sorting (either "asc" for ascending or "desc" for descending).
     * @return A {@code Sort} object representing the sorting criteria.
     */
    public static Sort getSort(final String sortBy, final String sortDir) {
        return sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    }
}
