package swing_test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class mainWindow extends JFrame {

    public mainWindow(){
        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());

        Mesh tetrahedron = Mesh.tetrahedron(100);
        tetrahedron.translate(-350, 0, 0);

        Mesh cube = Mesh.cube(200);
        cube.translate(350, 0, 0);

        Mesh ballCube = Mesh.cube(1)
                .subdivide(2)
                .inflate( 120);

        Mesh ballTetrahedron = Mesh.tetrahedron(1)
                .subdivide(3)
                .inflate( 130);

        ArrayList<Mesh> objects = new ArrayList<>();
        objects.add(tetrahedron);
        objects.add(cube);
//        objects.add(ballCube);
        objects.add(ballTetrahedron);



        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                // draw background
                g.setColor(Color.darkGray);
                g.fillRect(0, 0, getWidth(), getHeight());

                renderMesh( (Graphics2D) g, objects );

            }
        };
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

                objects.forEach( mesh -> mesh.rotate(dy, dx));

                lastX = e.getX();
                lastY = e.getY();

                renderPanel.repaint();
            }
        };


        renderPanel.addMouseMotionListener( rotationAdapter );

        pane.add(renderPanel, BorderLayout.CENTER);

        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    private void renderMesh(Graphics2D g, ArrayList<Mesh> objects){

        //define bounds of usable space and set origin in center of window
        int     maxX = this.getWidth() / 2,
                maxY = this.getHeight() / 2,
                minX = - maxX,
                minY = - maxY;
        g.translate(maxX, maxY);

        ArrayList<Triangle> polygons = new ArrayList<>();
        for( Mesh m : objects )
            polygons.addAll( m.getPolygons() );
        if(polygons.isEmpty())
            return;

        BufferedImage canvas = new BufferedImage(
                maxX - minX + 1,
                maxY - minY + 1,
                BufferedImage.TYPE_INT_ARGB
        );
        double[][] zBuffer = createZBuffer( canvas, Double.NEGATIVE_INFINITY );

        for(Triangle p : polygons){
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
                                getShade( p.color, p.getNormalCos()  ).getRGB() );
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
        int red   = (int) ( color.getRed()   * shade ),
            green = (int) ( color.getGreen() * shade ),
            blue  = (int) ( color.getBlue()  * shade );
        return new Color( red, green, blue );
    }


}
