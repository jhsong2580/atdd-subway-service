package nextstep.subway.line.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationPathDTO;

public class PathResponse {

    private List<StationPathDTO> stations = new LinkedList<>();
    private int distance;

    public static PathResponse of(List<Station> stations, int distance){
        return new PathResponse(
            stations.stream()
                .map(StationPathDTO::of)
                .collect(Collectors.toList()),
            distance
        ) ;

    }

    protected PathResponse(List<StationPathDTO> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationPathDTO> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}