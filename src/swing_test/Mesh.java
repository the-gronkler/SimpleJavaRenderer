package swing_test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Mesh {
    public List<Triangle> mesh;
    // distance from the origin along each axis
    private double dx, dy, dz;


    public Mesh(Triangle... polygons){
        this.mesh = Arrays.asList(polygons);
        dx = 0;
        dy = 0;
        dz = 0;
    }

    public static Mesh tetrahedron(double size){
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
                1, 0, 0,
                0, cos, -sin,
                0, sin, cos
        );
    }
    public static Matrix3D createRotationMatrixY(double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        return new Matrix3D(
                cos, 0, sin,
                0, 1, 0,
                -sin, 0, cos
        );
    }

    public void rotate(double rotationX, double rotationY) {
        double radiansX = Math.toRadians(rotationX);
        double radiansY = Math.toRadians(rotationY);

        Matrix3D rotationMatrixX = createRotationMatrixX(-radiansX);
        Matrix3D rotationMatrixY = createRotationMatrixY(radiansY);

        mesh.forEach( t -> t.translate(-dx, -dy, -dz) );

        for (Triangle t : mesh){
            t.transform(rotationMatrixX);
            t.transform(rotationMatrixY);
        }

        mesh.forEach( t -> t.translate(dx, dy, dz) );
    }
    public void translate(double dx, double dy, double dz){
        this.dx += dx;
        this.dy += dy;
        this.dz += dz;
        mesh.forEach( t -> t.translate(dx, dy, dz) );
    }

    public void drawWireframe(Graphics2D g){
        mesh.forEach( t -> t.drawWireframe(g) );
    }

    public List<Triangle> getPolygons() {
        return mesh;
    }
}
