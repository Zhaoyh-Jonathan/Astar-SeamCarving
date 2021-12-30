package seamcarving;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import astar.AStarSolver;
import astar.AStarGraph;
import astar.WeightedEdge;

public class AStarSeamCarver implements SeamCarver {
    private Picture picture;

    private class Px {
        // (x, y) position in the image
        private int x;
        private int y;

        public Px(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {return true; }
            if (o == null || getClass() != o.getClass()) {return false; }
            Px px = (Px) o;
            return x == px.x &&
                    y == px.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private Px endPoint;
    private int verticalStatus;  // 1 - vertical , 0 - horizontal

    private class ImageGraph implements AStarGraph<Px> {

        @Override
        public List<WeightedEdge<Px>> neighbors(Px pixel) {
            List<WeightedEdge<Px>> neighbors = new ArrayList<>();
            int x = pixel.getX();
            int y = pixel.getY();

            // pixels representing start and end
            if (y == -1) {  // start pixel for vertical seam
                for (int i = 0; i < width(); i++) {
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(i, 0), energy(i, 0)));
                }
                return neighbors;
            } else if (x == -1) {  // start pixel for horizontal seam
                for (int i = 0; i < height(); i++) {
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(0, i), energy(0, i)));
                }
                return neighbors;
            }

            // regular pixels (pixels on the image graph)
            if (verticalStatus == 1) {
                if (y == height() - 1) {
                    neighbors.add(new WeightedEdge<Px>(pixel, endPoint, 0));
                    return neighbors;
                }

                neighbors.add(new WeightedEdge<Px>(pixel, new Px(x, y + 1), energy(x, y + 1)));
                if (width() == 1) {
                    return neighbors;
                }
                if (x == 0) {  // left column
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(x + 1, y + 1), energy(x + 1, y + 1)));
                } else if (x == width() - 1) {  // right column
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(x - 1, y + 1), energy(x - 1, y + 1)));
                } else {
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(x - 1, y + 1), energy(x - 1, y + 1)));
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(x + 1, y + 1), energy(x + 1, y + 1)));
                }
                return neighbors;
            } else {
                if (x == width() - 1) {
                    neighbors.add(new WeightedEdge<Px>(pixel, endPoint, 0));
                    return neighbors;
                }

                neighbors.add(new WeightedEdge<Px>(pixel, new Px(x+1, y), energy(x+1, y)));
                if (height() == 1) {
                    return neighbors;
                }
                if (y == 0) {
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(x+1, y+1), energy(x+1, y+1)));
                } else if (y == height()-1) {
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(x+1, y-1), energy(x+1, y-1)));
                } else {
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(x+1, y-1), energy(x+1, y-1)));
                    neighbors.add(new WeightedEdge<Px>(pixel, new Px(x+1, y+1), energy(x+1, y+1)));
                }
                return neighbors;
            }
        }

        @Override
        public double estimatedDistanceToGoal(Px s, Px goal) {
            return 0;
        }
    }

    public AStarSeamCarver(Picture picture) {
        if (picture == null) {
            throw new NullPointerException("Picture cannot be null.");
        }
        this.picture = new Picture(picture);
        endPoint = new Px(-10, -10);
    }

    public Picture picture() {
        return new Picture(picture);
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public Color get(int x, int y) {
        return picture.get(x, y);
    }

    public double energy(int x, int y) {
        int w = width();  // width of the image
        int h = height();  // height of the image
        // throw an exception if either x or y is outside its prescribed range.
        if (x < 0 || x >= w || y < 0 || y >= h) {
            throw new IndexOutOfBoundsException();
        }

        Color leftColor = get((w+x-1) % w, y);
        Color rightColor = get((w+x+1) % w, y);
        Color upColor = get(x, (h+y-1) % h);
        Color downColor = get(x, (h+y+1) % h);

        double xGradient = Math.pow(rightColor.getRed() - leftColor.getRed(), 2)
                + Math.pow(rightColor.getGreen() - leftColor.getGreen(), 2)
                + Math.pow(rightColor.getBlue() - leftColor.getBlue(), 2);
        double yGradient = Math.pow(downColor.getRed() - upColor.getRed(), 2)
                + Math.pow(downColor.getGreen() - upColor.getGreen(), 2)
                + Math.pow(downColor.getBlue() - upColor.getBlue(), 2);

        return Math.sqrt(xGradient + yGradient);
    }

    public int[] findHorizontalSeam() {
        verticalStatus = 0;
        ImageGraph ig = new ImageGraph();
        Px horizontalStart = new Px(-1, 0);
        AStarSolver<Px> solver = new AStarSolver<>(ig, horizontalStart, endPoint, 2000);
        List<Px> solution = solver.solution();
        int[] result = new int[solution.size()-2];
        for (int i = 1; i < solution.size()-1; i++) {
            result[i-1] = solution.get(i).getY();
        }
        return result;
    }

    public int[] findVerticalSeam() {
        verticalStatus = 1;
        ImageGraph ig = new ImageGraph();
        Px verticalStart = new Px(0, -1);
        AStarSolver<Px> solver = new AStarSolver<>(ig, verticalStart, endPoint, 2000);
        List<Px> solution = solver.solution();
        int[] result = new int[solution.size()-2];
        for (int i = 1; i < solution.size()-1; i++) {
            result[i-1] = solution.get(i).getX();
        }
        return result;
    }
}
