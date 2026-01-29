package com.acervo.api.repository.specs;

import com.acervo.api.domain.Album;
import com.acervo.api.domain.Artist;
import com.acervo.api.domain.ArtistType;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AlbumSpecs {

    public static Specification<Album> withFilter(String artistName, ArtistType artistType, Integer releaseYear) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(artistName) || artistType != null) {
                Join<Album, Artist> artistJoin = root.join("artists", JoinType.INNER);

                if (StringUtils.isNotBlank(artistName)) {
                    predicates.add(
                            builder.like(builder.lower(artistJoin.get("name")), "%" + artistName.toLowerCase() + "%"));
                }

                if (artistType != null) {
                    predicates.add(builder.equal(artistJoin.get("type"), artistType));
                }
            }

            if (releaseYear != null) {
                predicates.add(builder.equal(root.get("year"), releaseYear));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
