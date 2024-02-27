package swing_test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import static swing_test.Mesh.CUBE;
import static swing_test.Mesh.TETRAHEDRON;



public class RenderPanel extends JPanel {
//    public ArrayList<Mesh> objects;
    private final HashMap<String, Mesh> objects;



    public static final Vertex
            lightDirection = new Vertex(10,-10,20);

    public static final Color
            backgroundColor = getShade( Color.darkGray, 0.5);

    public static final double
            minShade = (backgroundColor.getRed() + backgroundColor.getBlue() + backgroundColor.getGreen()) / 800.0 ;


    public RenderPanel(){
        objects = new HashMap<>();

        System.out.println(minShade);

        Mesh tetrahedron = Mesh.tetrahedron(100);
        tetrahedron.translate(-350, 0, 0);

        Mesh cube = Mesh.cube(200);
        cube.translate(350, 0, 0);

        Mesh ballCube = Mesh.cube(1)
                .subdivide(2)
                .inflate( 120);

        Mesh ballTetrahedron = Mesh.tetrahedron(1).formSphere(130, 3);


//        objects.put("Tet1", tetrahedron);
//        objects.put("Cube1", cube);
//        objects.put("ball1", ballCube);
        objects.put("ball2", ballTetrahedron);

        MouseAdapter rotationAdapter = new MouseAdapter() {
            int lastX, lastY;

            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;

                objects.values().forEach( mesh -> mesh.rotate(dy, dx));

                lastX = e.getX();
                lastY = e.getY();

                repaint();
            }
        };

        addMouseMotionListener( rotationAdapter );
    }

    @Override
    public void paintComponent(Graphics g) {
        // draw background
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        //define bounds of usable space and set origin in center of window
        int     maxX = this.getWidth() / 2,
                maxY = this.getHeight() / 2,
                minX = - maxX,
                minY = - maxY;
        g.translate(maxX, maxY);

        BufferedImage canvas = new BufferedImage(
                maxX - minX + 1,
                maxY - minY + 1,
                BufferedImage.TYPE_INT_ARGB
        );
        double[][] zBuffer = createZBuffer( canvas, Double.NEGATIVE_INFINITY );


        for( Mesh m : objects.values() )
            for( Triangle p : m.getPolygons() ){
                // define bounds for drawing the polygon and
                // make sure we don't try to draw outside the window
                int     pMinY = Math.max( p.getMinY(), minY ),
                        pMinX = Math.max( p.getMinX(), minX ),
                        pMaxY = Math.min( p.getMaxY(), maxY ),
                        pMaxX = Math.min( p.getMaxX(), maxX );

                for (int y = pMinY; y <= pMaxY; y++)
                    for (int x = pMinX; x <= pMaxX; x++){

                        int normalX = x - minX, normalY = y - minY;
                        double z = p.depthAt(x,y);
                        if( z > zBuffer[ normalX ][ normalY ] ){
                            canvas.setRGB( normalX, normalY,
                                    getShade( p.color, p.getNormalCos(lightDirection) ).getRGB() );
                            zBuffer[ normalX ][ normalY ] = z;
                        }

                    }
            }




        g.drawImage(canvas, minX, minY, null);

    }
    private double[][] createZBuffer(BufferedImage image, double drawDistance) {
        double[][] zBuffer = new double[image.getWidth()][image.getHeight()];
        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++)
                zBuffer[x][y] = drawDistance;
        return zBuffer;
    }
    public static Color getShade(Color color, double shade) {
//        double minShade = 0;
        if (shade < minShade){
            shade = minShade;
        }

        int     red   = (int) ( color.getRed()   * shade ),
                green = (int) ( color.getGreen() * shade ),
                blue  = (int) ( color.getBlue()  * shade );
        return new Color( red, green, blue );
    }


    public String[] getObjectNames(){
        return objects.keySet().toArray(new String[0]);
    }
    public void addObject(String type, String name, boolean isSphere, double radius, int subdivisions, double size){
        if (size < 1 || name.isEmpty())
            return;

        Mesh mesh = switch (type){
            case TETRAHEDRON -> Mesh.tetrahedron(size);
            case CUBE -> Mesh.cube(size);
            default -> throw new IllegalArgumentException("Invalid object type");
        };

        if( isSphere && radius > 0 )
            mesh.subdivide(subdivisions).inflate(radius);

        objects.put(name, mesh);
        repaint();
    }
    public void removeObject(String name){
        objects.remove(name);
        repaint();
    }

}
