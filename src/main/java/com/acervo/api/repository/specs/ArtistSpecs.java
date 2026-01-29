package com.acervo.api.repository.specs;

import com.acervo.api.domain.Artist;
import com.acervo.api.domain.ArtistType;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ArtistSpecs {

    public static Specification<Artist> withFilter(String name, ArtistType type) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(name)) {
                predicates.add(builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (type != null) {
                predicates.add(builder.equal(root.get("type"), type));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
