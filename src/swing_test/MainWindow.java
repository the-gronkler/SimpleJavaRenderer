package swing_test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import static swing_test.Mesh.CUBE;
import static swing_test.Mesh.TETRAHEDRON;

public class MainWindow extends JFrame {

    public static String defaultObjectType = TETRAHEDRON;
    public static double defaultSize = 150;
    public static boolean defaultIsSphere = true;
    public static int defaultSubdivisions = 3;

    RenderPanel renderPanel;

    JComboBox<String> objectType;
    JSpinner sizeSpinner;
    JCheckBox isSphere;
    JSpinner subdivisionSpinner;
    JLabel polygonCount;

    public MainWindow(){
        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());

        // create components
        renderPanel = new RenderPanel();
        renderPanel.changeObject(
                defaultObjectType,
                defaultSize,
                defaultIsSphere,
                defaultSubdivisions
        );

        objectType = new JComboBox<>(new String[]{ CUBE, TETRAHEDRON });
        objectType.setSelectedItem(defaultObjectType);

        sizeSpinner = new JSpinner(new SpinnerNumberModel(
                defaultSize, 0, Double.MAX_VALUE, 10) );
        sizeSpinner.setPreferredSize(new Dimension(80, 20));

        isSphere = new JCheckBox();
        isSphere.setSelected(defaultIsSphere);

        subdivisionSpinner = new JSpinner( new SpinnerNumberModel(
                defaultSubdivisions, 0, Integer.MAX_VALUE, 1) );
        subdivisionSpinner.setPreferredSize(new Dimension(80, 20));

        NumberFormat format = NumberFormat.getInstance();
        polygonCount = new JLabel(format.format( renderPanel.getPolygonCount() ));

        ActionListener changeObjectListener = e -> {
            renderPanel.changeObject(
                    (String) objectType.getSelectedItem(),
                    (double) sizeSpinner.getValue(),
                    isSphere.isSelected(),
                    (int) subdivisionSpinner.getValue()
            );
            polygonCount.setText(format.format( renderPanel.getPolygonCount() ));
        };

        objectType.addActionListener( changeObjectListener );
        sizeSpinner.addChangeListener(e -> changeObjectListener.actionPerformed(null));
        isSphere.addActionListener( changeObjectListener );
        subdivisionSpinner.addChangeListener(e -> changeObjectListener.actionPerformed(null));


        // add components to ui
        JPanel controlPanel = new JPanel(new GridLayout(5, 2));

        controlPanel.add(new Label("Object Type: "));
        controlPanel.add(objectType);

        controlPanel.add(new Label("Size: "));
        controlPanel.add(sizeSpinner);

        controlPanel.add(new Label("isSphere: "));
        controlPanel.add(isSphere);

        controlPanel.add(new Label("Subdivisions: "));
        controlPanel.add(subdivisionSpinner);

        controlPanel.add(new Label("Polygons: "));
        controlPanel.add(polygonCount);


        JPanel menuContainer = new JPanel();

        menuContainer.add(controlPanel);
        menuContainer.setPreferredSize(new Dimension(215, 130));
        pane.add(menuContainer, BorderLayout.EAST);

        pane.add(renderPanel);

        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);



    }






}