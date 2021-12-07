package nextstep.subway.line.domain;

import nextstep.subway.common.exception.ServiceException;
import nextstep.subway.line.exception.DuplicateBothStationException;
import nextstep.subway.line.exception.NotMatchedStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    public static final int MIN_SECTION_COUNT = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Line line, Station upStation, Station downStation, int distance) {
        List<Station> stations = getStations();
        validateAddSection(upStation, downStation, stations);

        findOverlapSection(upStation, downStation)
                .ifPresent(it -> it.updateStation(upStation, downStation, distance));

        sections.add(new Section(line, upStation, downStation, distance));
    }

    private Optional<Section> findOverlapSection(Station upStation, Station downStation) {
        Optional<Section> findUpStation = findByUpStation(upStation);
        if (findUpStation.isPresent()) {
            return findUpStation;
        }

        return sections.stream()
                .filter(it -> it.getDownStation() == downStation)
                .findFirst();
    }

    private void validateAddSection(Station upStation, Station downStation, List<Station> stations) {
        boolean isUpStationExisted = stations.stream().anyMatch(it -> it == upStation);
        boolean isDownStationExisted = stations.stream().anyMatch(it -> it == downStation);

        if (isUpStationExisted && isDownStationExisted) {
            throw new DuplicateBothStationException(upStation, downStation);
        }

        boolean hasAnyMatchedStations = stations.isEmpty() ||
                stations.stream().anyMatch(it -> it == upStation) ||
                stations.stream().anyMatch(it -> it == downStation);
        if (!hasAnyMatchedStations) {
            throw new NotMatchedStationException(upStation, downStation);
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station currentStation = findFirstStation();
        stations.add(currentStation);

        Optional<Section> nextLineStation;
        while ((nextLineStation = findByUpStation(currentStation)).isPresent()) {
            currentStation = nextLineStation.get().getDownStation();
            stations.add(currentStation);
        }

        return stations;
    }

    private Station findFirstStation() {
        Station firstStation = sections.get(0).getUpStation();
        Optional<Section> nextLineStation;
        while ((nextLineStation = findByDownStation(firstStation)).isPresent()) {
            firstStation = nextLineStation.get().getUpStation();
        }

        return firstStation;
    }

    public void deleteSectionBy(Line line, Station station) {
        validateDeleteSection();

        Optional<Section> upLineStation = findByUpStation(station);
        Optional<Section> downLineStation = findByDownStation(station);

        if (upLineStation.isPresent() && downLineStation.isPresent()) {
            addMergedSection(line, upLineStation.get(), downLineStation.get());
        }

        upLineStation.ifPresent(it -> sections.remove(it));
        downLineStation.ifPresent(it -> sections.remove(it));
    }

    private void addMergedSection(Line line, Section upLineStation, Section downLineStation) {
        Station newUpStation = downLineStation.getUpStation();
        Station newDownStation = upLineStation.getDownStation();
        int newDistance = upLineStation.getDistance() + downLineStation.getDistance();
        sections.add(new Section(line, newUpStation, newDownStation, newDistance));
    }

    private void validateDeleteSection() {
        if (sections.size() <= MIN_SECTION_COUNT) {
            throw new ServiceException("남은 구간은 삭제할 수 없습니다");
        }
    }

    private Optional<Section> findByDownStation(Station station) {
        return sections.stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst();
    }

    private Optional<Section> findByUpStation(Station station) {
        return sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst();
    }

    public boolean hasStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasStation(station));
    }

    public boolean containsStation(List<Station> stations) {
        return getStations().stream()
                .anyMatch(stations::contains);
    }
}
