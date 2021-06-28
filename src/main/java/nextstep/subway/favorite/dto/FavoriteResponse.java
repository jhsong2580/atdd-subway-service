package nextstep.subway.favorite.dto;

import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class FavoriteResponse {
    private Long id;
    private StationResponse source;
    private StationResponse target;

    public FavoriteResponse() {
    }

    public FavoriteResponse(Long id, StationResponse source, StationResponse target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }

    public static FavoriteResponse from(Favorite favorite) {
        StationResponse source = StationResponse.of(favorite.getSource());
        StationResponse target = StationResponse.of(favorite.getTarget());
        return new FavoriteResponse(favorite.getId(), source, target);
    }

    public static List<FavoriteResponse> fromToCollection(List<Favorite> favorites) {
        return favorites.stream()
                .map(FavoriteResponse::from)
                .collect(toList());
    }

    public Long getId() {
        return id;
    }

    public StationResponse getSource() {
        return source;
    }

    public StationResponse getTarget() {
        return target;
    }
}
