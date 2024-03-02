package swing_test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static swing_test.Mesh.CUBE;
import static swing_test.Mesh.TETRAHEDRON;

public class RenderPanel extends JPanel {
    public static final Vertex
            lightDirection = new Vertex(10,-10,20);

    public static final Color
            backgroundColor = new Color(32, 32, 32);

    public static final double minShade = getBrightness(backgroundColor);



    public Mesh object;
    private double rotationX, rotationY;

    public RenderPanel(){
        rotationX = 0;
        rotationY = 0;
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

                rotationX += dx;
                rotationY += dy;

                object.rotate(dy, dx);

                lastX = e.getX();
                lastY = e.getY();

                repaint();
            }
        };
        addMouseMotionListener(rotationAdapter);
    }

    @Override
    public void paintComponent(Graphics g) {
        // draw background
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        if( object == null )
            return;

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

        for( Triangle p : object.getPolygons() ){
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
                                shadeColor( p.color, p.getNormalCos(lightDirection) ).getRGB() );
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

    public static Color shadeColor(Color color, double shade) {
        if (shade < minShade)
            shade = minShade;

        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[2] = (float)(Math.max( 0, hsb[2] * shade ));

        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static double getBrightness(Color color){
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return hsb[2];
    }

    public void changeObject(String objectType, double size, boolean isSphere, int subdivisions ){
        if( size < 1 )
            throw new IllegalArgumentException("size must be > 0");
        if( subdivisions < 0 )
            throw new IllegalArgumentException("subdivisions must be >= 0");

        object = switch (objectType){
            case TETRAHEDRON -> Mesh.tetrahedron(size);
            case CUBE -> Mesh.cube(size);
            default -> throw new IllegalArgumentException("Invalid object type");
        };

        if( isSphere )
            object.subdivide(subdivisions).inflate(size);

        object.rotate(rotationX, rotationY);
        repaint();

    }

    public int getPolygonCount(){
        return object.getPolygons().size();
    }
}