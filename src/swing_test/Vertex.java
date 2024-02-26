package swing_test;

public class Vertex {
    public double x, y, z;

    public Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void transform(Matrix3D transformationMatrix){
        double[] m = transformationMatrix.values;
        double y1 = x * m[1] + y * m[4] + z * m[7],
                z1 = x * m[2] + y * m[5] + z * m[8];

        x = x * m[0] + y * m[3] + z * m[6];
        y = y1;
        z = z1;
    }
    public void translate(double dx, double dy, double dz){
        x += dx;
        y += dy;
        z += dz;
    }


    public Vertex crossProduct(Vertex other) {
        return new Vertex(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }
    public Vertex getVectorTo(Vertex target){
        return new Vertex(
                target.x - x,
                target.y - y,
                target.z - z
        );
    }
    public Vertex getVectorTo(double x, double y ){
        return new Vertex(
                x - this.x,
                y - this.y,
                this.z
        );
    }
    public Vertex normalise(){
        double length = getVectorLength();
        x /= length;
        y /= length;
        z /= length;
        return this;
    }

    public double getVectorLength(){
        return Math.sqrt( x*x + y*y + z*z );
    }

    public static Vertex midpoint(Vertex v1, Vertex v2){
        return new Vertex(
                ( v1.x + v2.x ) / 2.0,
                ( v1.y + v2.y ) / 2.0,
                ( v1.z + v2.z ) / 2.0
        );
    }

    public void inflate(double radius) {
        double l  = getVectorLength() / radius;
        x /= l;
        y /= l;
        z /= l;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vertex vertex)) return false;

        if (Double.compare(vertex.x, x) != 0) return false;
        if (Double.compare(vertex.y, y) != 0) return false;
        return Double.compare(vertex.z, z) == 0;
    }

    public Vertex clone(){
        return new Vertex(x, y, z);
    }



}
