package nextstep.subway.line.domain;

import java.util.List;

public class AdjacentSections {

    private final List<Section> value;

    private AdjacentSections(List<Section> sections) {
        if (sections.size() != 2){
            throw new RuntimeException("adjacentSections size: " + sections.size());
        }
        this.value = sections;
    }

    public static AdjacentSections of(List<Section> collect) {
        return new AdjacentSections(collect);
    }

    public void merge(List<Section> values) {
        Section section = this.value.get(0);
        Section nextSection = this.value.get(1);
        section.changeDownStation(nextSection.getDownStation());
        section.setDistance(section.getDistance() +nextSection.getDistance());
        values.remove(nextSection);
    }
}