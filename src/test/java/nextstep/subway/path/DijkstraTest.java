package nextstep.subway.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.subway.station.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Test;

public class DijkstraTest {

    @Test
    public void dijkstraBasicTest() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);

        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        DijkstraShortestPath dijkstraShortestPath
            = new DijkstraShortestPath(graph);
        List<String> shortestPath
            = dijkstraShortestPath.getPath("v3", "v1").getVertexList();

        assertThat(shortestPath.size()).isEqualTo(3);

    }

    @Test
    public void dijkstraStationTest() {
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 서초역 = new Station("서초역");
        Station 잠실역 = new Station("잠실역");

        WeightedMultigraph<String, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);

        graph.addVertex(강남역.getName());
        graph.addVertex(양재역.getName());
        graph.addVertex(서초역.getName());
        graph.addVertex(잠실역.getName());

        graph.setEdgeWeight(graph.addEdge(강남역.getName(), 양재역.getName()), 1);
        graph.setEdgeWeight(graph.addEdge(양재역.getName(), 서초역.getName()), 2);
        graph.setEdgeWeight(graph.addEdge(양재역.getName(), 잠실역.getName()), 3);

        DijkstraShortestPath dijkstraShortestPath
            = new DijkstraShortestPath(graph);
        List<String> shortestPath
            = dijkstraShortestPath.getPath(강남역.getName(), 잠실역.getName()).getVertexList();
        double weight = dijkstraShortestPath.getPath(강남역.getName(), 잠실역.getName()).getWeight();

        assertAll(
            () -> assertThat(shortestPath)
                .hasSize(3)
                .containsExactly("강남역", "양재역", "잠실역"),
            () -> assertThat(weight).isEqualTo(4.0)
        );
    }
}