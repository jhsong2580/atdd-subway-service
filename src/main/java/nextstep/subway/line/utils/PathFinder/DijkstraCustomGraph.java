package nextstep.subway.line.utils.PathFinder;

import nextstep.subway.line.domain.Line;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.WeightedMultigraph;

public class DijkstraCustomGraph extends WeightedMultigraph {

    public DijkstraCustomGraph(EdgeFactory ef) {
        super(ef);
    }

    public DijkstraCustomGraph(Class edgeClass) {
        super(edgeClass);
    }

    public void setEdgeWeight(Object o, double weight, Line line) {
        super.setEdgeWeight(o, weight);
        ((DijkstraWeightedEdgeWithLine) o).setLine(line);
    }
}
