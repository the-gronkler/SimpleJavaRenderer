package swing_test;

import java.awt.*;
import java.awt.geom.Path2D;

public class Triangle {
    public Vertex[] vertices;
    public Color color;

    public Triangle(Color color, Vertex... vertices) {
        if (vertices.length != 3)
            throw new IllegalArgumentException("expected 3 vertices, found " + vertices.length);

        this.vertices = vertices;
        this.color = color;
    }

    public static Triangle build(
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3,
            Color color
    ) {
        return new Triangle(
                color,
                new Vertex(x1, y1, z1),
                new Vertex(x2, y2, z2),
                new Vertex(x3, y3, z3)

        );
    }

    public void transform(Matrix3D transformationMatrix){
        for(Vertex v : vertices)
            v.transform(transformationMatrix);
    }
    public void translate(double dx, double dy, double dz){
        for(Vertex v : vertices)
            v.translate(dx, dy, dz);
    }

    public void drawWireframe(Graphics2D g) {
        Path2D path = new Path2D.Double();
        path.moveTo(vertices[0].x, vertices[0].y);

        path.lineTo(vertices[1].x, vertices[1].y);
        path.lineTo(vertices[2].x, vertices[2].y);

        path.closePath();
        g.draw(path);
    }

    public double DepthAt(double x, double y) {
        // Calculate edge vectors
        Vertex edgeAB = vertices[0].getVectorTo( vertices[1] );
        Vertex edgeBC = vertices[1].getVectorTo( vertices[2] );
        Vertex edgeCA = vertices[2].getVectorTo( vertices[0] );

        // Calculate vectors from each vertex to the test point
        Vertex AP = vertices[0].getVectorTo(x, y);
        Vertex BP = vertices[1].getVectorTo(x, y);
        Vertex CP = vertices[2].getVectorTo(x, y);

        // Calculate cross products for triangle orientation
        double test1 = edgeAB.crossProduct( AP ).z;
        double test2 = edgeBC.crossProduct( BP ).z;
        double test3 = edgeCA.crossProduct( CP) .z;

        boolean containsPoint =
                ( test1 >= 0 && test2 >= 0 && test3 >= 0 ) ||
                ( test1 <= 0 && test2 <= 0 && test3 <= 0 );

        if (!containsPoint)
            return Double.NaN;

        // Calculate normal vector of the plane
        Vertex normal = edgeAB.crossProduct(edgeCA).normalise();

        // Calculate distance from the origin along the normal vector
        double d =  - ( normal.x * vertices[0].x +
                        normal.y * vertices[0].y +
                        normal.z * vertices[0].z );

        // return z coordinate of the point on the plane
        return ( - normal.x * x - normal.y * y - d ) / normal.z;
    }

    public int getMinX() {
        return (int) Math.floor(Math.min(vertices[0].x, Math.min(vertices[1].x, vertices[2].x)));

    }
    public int getMaxX() {
        return (int) Math.ceil(Math.max(vertices[0].x, Math.max(vertices[1].x, vertices[2].x)));

    }
    public int getMinY() {
        return (int) Math.floor(Math.min(vertices[0].y, Math.min(vertices[1].y, vertices[2].y)));

    }
    public int getMaxY() {
        return (int) Math.ceil(Math.max(vertices[0].y, Math.max(vertices[1].y, vertices[2].y)));
    }

    public Vertex[] getVertices() {
        return  vertices;
    }
}
