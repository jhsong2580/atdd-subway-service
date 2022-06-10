package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    Station 첫번째역 = new Station("첫번째역");
    Station 두번째역 = new Station("두번째역");
    Station 세번째역 = new Station("세번째역");

    @DisplayName("첫번째역 ~ 세번째역 사이에 두번째역을 끼워넣는다")
    @Test
    public void 섹션안에서_후방에_끼워넣기() {
        //given
        Section 구간 = new Section(null, 세번째역, 첫번째역, 10);

        //when
        Section 추가할_구간 = new Section(null, 세번째역, 두번째역, 3);
        구간.insert(추가할_구간);

        //then
        assertAll(
            () -> assertThat(구간).extracting("upStation").isEqualTo(두번째역),
            () -> assertThat(구간).extracting("downStation").isEqualTo(첫번째역),
            () -> assertThat(구간).extracting("distance.distance").isEqualTo(10 - 3)
        );
    }

    @DisplayName("첫번째역 ~ 세번째역 사이에 두번째역을 끼워넣는다")
    @Test
    public void 섹션안에서_전방에_끼워넣기() {
        //given
        Section 구간 = new Section(null, 세번째역, 첫번째역, 10);

        //when
        Section 추가할_구간 = new Section(null, 두번째역, 첫번째역, 3);
        구간.insert(추가할_구간);

        //then
        assertAll(
            () -> assertThat(구간).extracting("upStation").isEqualTo(세번째역),
            () -> assertThat(구간).extracting("downStation").isEqualTo(두번째역),
            () -> assertThat(구간).extracting("distance.distance").isEqualTo(10 - 3)
        );
    }

    @DisplayName("삽입하는 섹션의 거리가 기존 섹션 거리보다 같거나 클시 에러가 발생한다")
    @Test
    public void 섹션에_끼워넣기_거리초과에러발생() {
        //given
        Section 구간 = new Section(null, 세번째역, 첫번째역, 10);

        //when
        Section 추가할_구간 = new Section(null, 두번째역, 첫번째역, 10);

        //then
        assertThatThrownBy(() -> 구간.insert(추가할_구간)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void 섹션합치기_섹션과_섹션() {
        //given
        Section 앞_구간 = new Section(null, 두번째역, 첫번째역, 3);
        Section 뒤_구간 = new Section(null, 세번째역, 두번째역, 4);

        //when
        Section 합친_구간 = Section.mergeOf(앞_구간, 뒤_구간);

        //then
        assertAll(
            () -> assertThat(합친_구간).extracting("downStation").isEqualTo(첫번째역),
            () -> assertThat(합친_구간).extracting("upStation").isEqualTo(세번째역),
            () -> assertThat(합친_구간).extracting("distance.distance").isEqualTo(3 + 4)
        );
    }

    @Test
    public void 섹션합치기_섹션과_빈섹션() {
        //given
        Section 앞_구간 = new Section(null, 두번째역, 첫번째역, 3);
        Section 뒤_구간 = Section.emptyOf(null);

        //when
        Section 합친_구간 = Section.mergeOf(앞_구간, 뒤_구간);

        //then
        assertAll(
            () -> assertThat(합친_구간).extracting("downStation").isEqualTo(첫번째역),
            () -> assertThat(합친_구간).extracting("upStation").isEqualTo(null),
            () -> assertThat(합친_구간).extracting("distance.distance").isEqualTo(3)
        );
    }

    @Test
    public void 섹션합치기_빈섹션과_섹션() {
        //given
        Section 뒤_구간 = new Section(null, 두번째역, 첫번째역, 3);
        Section 앞_구간 = Section.emptyOf(null);

        //when
        Section 합친_구간 = Section.mergeOf(앞_구간, 뒤_구간);

        //then
        assertAll(
            () -> assertThat(합친_구간).extracting("downStation").isEqualTo(null),
            () -> assertThat(합친_구간).extracting("upStation").isEqualTo(두번째역),
            () -> assertThat(합친_구간).extracting("distance.distance").isEqualTo(3)
        );
    }
}
