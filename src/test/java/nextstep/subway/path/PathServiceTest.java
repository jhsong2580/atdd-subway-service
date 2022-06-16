package nextstep.subway.path;

import static nextstep.subway.utils.ReflectionHelper.역_ID_설정하기;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.PathPriceCalculator;
import nextstep.subway.line.application.PathServiceFacade;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.PathResponse;
import nextstep.subway.member.application.MemberService;
import nextstep.subway.member.domain.Member;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class PathServiceTest {
    @Mock
    StationService stationService;
    @Mock
    LineService lineService;
    @Mock
    PathPriceCalculator pathPriceCalculator;
    @Mock
    MemberService memberService;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    Station 강남역;
    Station 교대역;
    Station 남부터미널역;
    Station 양재역;

    @BeforeEach
    public void init() throws NoSuchFieldException, IllegalAccessException {
        강남역 = new Station("강남역");

        교대역 = new Station("교대역");
        남부터미널역 = new Station("남부터미널역");
        양재역 = new Station("양재역");

        이호선 = new Line("이호선", "녹색", 강남역, 교대역, 10);
        신분당선 = new Line("신분당선", "빨강색", 강남역, 양재역, 9);
        삼호선 = new Line("삼호선", "주황색", 남부터미널역, 양재역, 8);
        삼호선.addStation(교대역, 남부터미널역, 7);

        when(lineService.findAllLines()).thenReturn(Arrays.asList(이호선, 신분당선, 삼호선));
        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(4L)).thenReturn(양재역);
        when(memberService.findById(1L)).thenReturn(new Member("email", "password", 18));
        역_ID_설정하기(1L, 교대역);
        역_ID_설정하기(4L, 양재역);
    }

    /**
     * 교대역    --- *2호선*(10) ---   강남역
     * |                        |
     * *3호선*(7)                   *신분당선*(9)
     * |                        |
     * 남부터미널역  --- *3호선*(8) ---   양재
     */
    @Test
    public void 정상_경로찾기() {
        //given
        PathServiceFacade pathService = new PathServiceFacade(stationService, lineService, pathPriceCalculator, memberService);

        //when
        PathResponse pathResponse = pathService.findPath(교대역.getId(), 양재역.getId(), 1L);

        //then
        assertAll(() -> assertThat(pathResponse.getDistance()).isEqualTo(15),
            () -> assertThat(pathResponse.getStations()).extracting("name")
                .containsExactly("교대역", "남부터미널역", "양재역"));
    }

}
