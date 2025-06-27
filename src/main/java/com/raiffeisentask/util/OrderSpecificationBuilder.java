package com.raiffeisentask.util;

import com.raiffeisentask.model.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderSpecificationBuilder {

    public static Specification<Order> build(
            String orderNumber,
            Long productId,
            Integer quantityMin,
            Integer quantityMax,
            LocalDateTime createdAfter
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (orderNumber != null) {
                predicates.add(cb.like(cb.lower(root.get("orderNumber")),
                        "%" + orderNumber.toLowerCase() + "%"));
            }

            if (productId != null) {
                predicates.add(cb.equal(root.get("product").get("id"), productId));
            }

            if (quantityMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("quantity"), quantityMin));
            }

            if (quantityMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("quantity"), quantityMax));
            }

            if (createdAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
