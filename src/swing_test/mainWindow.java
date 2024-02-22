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
        Mesh cube = Mesh.cube(200);

        ArrayList<Mesh> objects = new ArrayList<>();
        objects.add(tetrahedron);
        objects.add(cube);

        tetrahedron.translate(-350, 0, 0);
        cube.translate(350, 0, 0);

        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                // draw background
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());

                renderMesh((Graphics2D) g, objects);

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
        int     maxX = getWidth() / 2,
                maxY = getHeight() / 2,
                minX = - maxX,
                minY = - maxY;

        //set origin in center of window
        g.translate(maxX, maxY);

        ArrayList<Triangle> polygons = new ArrayList<>();
        for( Mesh m : objects )
            polygons.addAll( m.getPolygons() );
        if(polygons.isEmpty())
            return;

        BufferedImage canvas = new BufferedImage(maxX - minX + 1, maxY - minY + 1, BufferedImage.TYPE_INT_ARGB);
        double[][] zBuffer = createZBuffer(canvas, Double.NEGATIVE_INFINITY);

        for(Triangle t : polygons){
            // make sure we don't try to draw outside of window
            int pMinY = t.getMinY(), pMaxY = t.getMaxY(),
                pMinX = t.getMinX(), pMaxX = t.getMaxX();
            if ( pMinY < minY )     pMinY = minY;
            if ( pMaxY > maxY )     pMaxY = maxY;
            if ( pMinX < minX )     pMinX = minX;
            if ( pMaxX > maxX )     pMaxX = maxX;

            for (int y = pMinY; y <= pMaxY; y++)
                for (int x = pMinX; x <= pMaxX; x++){
                    int normalX = x - minX, normalY = y - minY;
                    double z = t.DepthAt(x,y);
                    if( z > zBuffer[normalX][normalY] ){
                        canvas.setRGB(normalX, normalY, t.color.getRGB());
                        zBuffer[normalX][normalY] = z;
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
