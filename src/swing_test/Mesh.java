package swing_test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mesh {
    public List<Triangle> polygons;
    // distance from the origin along each axis
    private double dx, dy, dz;


    public Mesh(Triangle... polygons){
        this.polygons = Arrays.asList(polygons);
        dx = 0;
        dy = 0;
        dz = 0;
    }

    public static Mesh tetrahedron(double size){
        // size = 1/2 edge lengths of a cube the vertices of which the tetrahedron shares
        return new Mesh(
                Triangle.build(
                        size, size, size,
                        -size, -size, size,
                        -size, size, -size,
                        Color.ORANGE
                ),
                Triangle.build(
                        size, size, size,
                        -size, -size, size,
                        size, -size, -size,
                        Color.RED
                ),
                Triangle.build(
                        -size, size, -size,
                        size, -size, -size,
                        size, size, size,
                        Color.GREEN
                ),
                Triangle.build(
                        -size, size, -size,
                        size, -size, -size,
                        -size, -size, size,
                        Color.BLUE
                )
        );
    }
    public static Mesh cube(double edgeLength) {
        double size = edgeLength / 2.0;
        return new Mesh(
                // Front face
                Triangle.build(
                        -size, -size, size,
                        size, -size, size,
                        size, size, size,
                        Color.RED
                ),
                Triangle.build(
                        -size, -size, size,
                        size, size, size,
                        -size, size, size,
                        Color.RED
                ),
                // Back face
                Triangle.build(
                        size, -size, -size,
                        -size, -size, -size,
                        -size, size, -size,
                        Color.GREEN
                ),
                Triangle.build(
                        size, -size, -size,
                        -size, size, -size,
                        size, size, -size,
                        Color.GREEN
                ),
                // Left face
                Triangle.build(
                        -size, -size, -size,
                        -size, -size, size,
                        -size, size, size,
                        Color.BLUE
                ),
                Triangle.build(
                        -size, -size, -size,
                        -size, size, size,
                        -size, size, -size,
                        Color.BLUE
                ),
                // Right face
                Triangle.build(
                        size, -size, size,
                        size, -size, -size,
                        size, size, -size,
                        Color.YELLOW
                ),
                Triangle.build(
                        size, -size, size,
                        size, size, -size,
                        size, size, size,
                        Color.YELLOW
                ),
                // Top face
                Triangle.build(
                        -size, size, size,
                        size, size, size,
                        size, size, -size,
                        Color.CYAN
                ),
                Triangle.build(
                        -size, size, size,
                        size, size, -size,
                        -size, size, -size,
                        Color.CYAN
                ),
                // Bottom face
                Triangle.build(
                        -size, -size, size,
                        size, -size, -size,
                        size, -size, size,
                        Color.MAGENTA
                ),
                Triangle.build(
                        -size, -size, size,
                        -size, -size, -size,
                        size, -size, -size,
                        Color.MAGENTA
                )
        );
    }


    public static Matrix3D createRotationMatrixX(double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        return new Matrix3D(
                1.0, 0.0, 0.0,
                0.0, cos, -sin,
                0.0, sin, cos
        );
    }
    public static Matrix3D createRotationMatrixY(double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        return new Matrix3D(
                cos, 0.0, sin,
                0.0, 1.0, 0.0,
                -sin, 0.0, cos
        );
    }

    public void rotate(double rotationX, double rotationY) {
        double radiansX = Math.toRadians(rotationX);
        double radiansY = Math.toRadians(rotationY);

        Matrix3D rotationMatrixX = createRotationMatrixX(-radiansX);
        Matrix3D rotationMatrixY = createRotationMatrixY(radiansY);

        normaliseOrigin();

        for (Triangle t : polygons){
            t.transform(rotationMatrixX);
            t.transform(rotationMatrixY);
        }

        denormalizeOrigin();
    }
    public void translate(double dx, double dy, double dz){
        this.dx += dx;
        this.dy += dy;
        this.dz += dz;
        polygons.forEach(t -> t.translate(dx, dy, dz) );
    }

    public void drawWireframe(Graphics2D g){
        polygons.forEach(t -> t.drawWireframe(g) );
    }

    public List<Triangle> getPolygons() {
        return polygons;
    }

    public Mesh subdivide(){
        List<Triangle> newPolygons = new ArrayList<>(polygons.size() * 4 );
        for( Triangle p : polygons )
            newPolygons.addAll(Arrays.asList( p.subdivide() ));
        this . polygons = newPolygons;
        return this;
    }

    public Mesh subdivide(int iterations){
        if (iterations < 1)
            throw new IllegalArgumentException();

        for(int i = 0; i < iterations; i++)
            this.subdivide();

        return this;
    }

    public Mesh inflate(double radius){
        normaliseOrigin();

        polygons.forEach(t -> t.inflate( radius ));

        denormalizeOrigin();
        return this;
    }

    private void normaliseOrigin(){
        polygons.forEach(t -> t.translate(-dx, -dy, -dz) );
    }

    private void denormalizeOrigin(){
        polygons.forEach(t -> t.translate(dx, dy, dz) );
    }


}
