package swing_test;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static swing_test.Mesh.CUBE;
import static swing_test.Mesh.TETRAHEDRON;


public class MainWindow extends JFrame {




    public MainWindow(){
        JFrame main = this;
        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());

        // create components
        RenderPanel renderPanel = new RenderPanel();

        JComboBox<String> objectSelect = new JComboBox<>(renderPanel.getObjectNames());
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton removeButton = new JButton("Remove");
        JSpinner xSpinner = new JSpinner();
        JSpinner ySpinner = new JSpinner();
        JSpinner zSpinner = new JSpinner();

        // set up component functions
        addButton.addActionListener(e -> {
            JDialog addDialog = new AddDialog(renderPanel, main, objectSelect);
            addDialog.setVisible(true);
        });

        removeButton.addActionListener(e -> {
            String name = (String) objectSelect.getSelectedItem();

            int result = JOptionPane.showConfirmDialog(
                    main,
                    "Are you sure you want to remove '" + name + "'?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION)
                renderPanel.removeObject(name);
        });



        ySpinner.setMinimumSize(new Dimension(100, 10));



        // add components to ui
        pane.add(renderPanel, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new FlowLayout());

        controlPanel.add(new Label("Select Object: "));
        controlPanel.add(objectSelect);
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(removeButton);

        controlPanel.add(new Label("            x:"));
        controlPanel.add(xSpinner);

        controlPanel.add(new Label("    y:"));
        controlPanel.add(ySpinner);

        controlPanel.add(new Label("    z:"));
        controlPanel.add(zSpinner);



//        pane.add(controlPanel, BorderLayout.NORTH);

        setSize(1200, 600 + controlPanel.getHeight());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);



    }


    private class AddDialog extends JDialog{
        public JComboBox<String> objectType = new JComboBox<>(new String[]{TETRAHEDRON, CUBE});
        public JTextField nameField = new JTextField();
        public JCheckBox sphereCheck = new JCheckBox();
        public JSpinner radiusSpinner = new JSpinner();
        public JSpinner subdivisionSpinner = new JSpinner();
        public JSpinner sizeSpinner = new JSpinner();

        public JButton okButton = new JButton("OK");

        public AddDialog(RenderPanel renderPanel, JFrame main, JComboBox objectSelect) {
            super(main, "Add Object", true);
            JPanel panel = new JPanel(new GridLayout(7, 2));

            panel.add(new Label("Object type: "));
            panel.add(objectType);

            panel.add(new Label("Name: "));
            panel.add(nameField);

            panel.add(new Label("Sphere: "));
            panel.add(sphereCheck);

            panel.add(new Label("Radius: "));
            panel.add(radiusSpinner);

            panel.add(new Label("Subdivisions: "));
            panel.add(subdivisionSpinner);

            panel.add(new Label("Size: "));
            panel.add(sizeSpinner);

            panel.add(okButton);

            okButton.addActionListener(e -> {
                renderPanel.addObject(
                        (String) Objects.requireNonNull(objectType.getSelectedItem()),
                        nameField.getText(),
                        sphereCheck.isSelected(),
                        (int) radiusSpinner.getValue(),
                        (int) subdivisionSpinner.getValue(),
                        (int) sizeSpinner.getValue()
                );
                objectSelect.setModel(new DefaultComboBoxModel<>(renderPanel.getObjectNames()));
                dispose();
            });

            getContentPane().add(panel);
            pack();
            setLocationRelativeTo(main);
        }
    };






}
