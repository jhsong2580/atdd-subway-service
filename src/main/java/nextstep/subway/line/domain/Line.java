package nextstep.subway.line.domain;

import static javax.persistence.FetchType.LAZY;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @Embedded
    private SectionsManager sections = new SectionsManager();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "UP_STATION_ID", foreignKey = @ForeignKey(name = "fk_line_up_station"))
    private Station upStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "DOWN_STATION_ID", foreignKey = @ForeignKey(name = "fk_line_down_station"))
    private Station downStation;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        updateUpDownStation(upStation, downStation);
        sections.add(new Section(this, upStation, downStation, distance));
    }

    private void updateUpDownStation(Station upStation, Station downStation) {
        if (isNotEmpty(upStation)) {
            this.upStation = upStation;
        }
        if (isNotEmpty(downStation)) {
            this.downStation = downStation;
        }
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }


    public void removeStation(Station stationForRemove) {
        Section baseSection = sections.removeSectionWithStation(stationForRemove);
        if (stationForRemove.equals(this.upStation)) {
            updateUpDownStation(baseSection.getDownStation(), null);
            return;
        }
        if (stationForRemove.equals(this.downStation)) {
            updateUpDownStation(null, baseSection.getUpStation());
            return;
        }
    }

    public void addStation(Station upStation, Station downStation, int distance) {
        sections.addStation(upStation, downStation, distance, this, this.upStation);
        if (this.upStation.equals(downStation)) {
            updateUpDownStation(upStation, null);
        }
        if (this.downStation.equals(upStation)) {
            updateUpDownStation(null, downStation);
        }
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return sections.getStationsOrdered(upStation);
    }
}
