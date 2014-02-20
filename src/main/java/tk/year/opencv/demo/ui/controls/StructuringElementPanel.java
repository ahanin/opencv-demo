/*
 * OpenCV Demo
 * Copyright (C) 2014  Alexey Hanin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package tk.year.opencv.demo.ui.controls;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import tk.year.opencv.demo.commons.PropertyAdapter;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class StructuringElementPanel extends JPanel {

    public StructuringElementPanel(final PropertyAdapter<Mat> propertyAdapter) {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.3d;
        c.weighty = 0.3d;
        c.gridx = 0;
        c.gridy = 0;

        panel.add(new JLabel("Shape"), c);

        c.gridy = 1;
        panel.add(new JLabel("Width"), c);

        c.gridy = 2;
        panel.add(new JLabel("Height"), c);

        c.weightx = 0.7;
        c.gridy = 0;
        c.gridx = 1;

        final Map<Integer, String> shapeMap = new LinkedHashMap<>();
        shapeMap.put(Imgproc.CV_SHAPE_CROSS, "Cross");
        shapeMap.put(Imgproc.CV_SHAPE_ELLIPSE, "Ellipse");
        shapeMap.put(Imgproc.CV_SHAPE_RECT, "Rectangle");

        final DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<>(new Vector<>(shapeMap.keySet()));
        final JComboBox<Integer> cbShape = new JComboBox<>(model);
        final JSlider sShapeWidth = new JSlider(0, 1000, 0);
        final JSlider sShapeHeight = new JSlider(0, 1000, 0);

        final Runnable updater = new Runnable() {
            @Override
            public void run() {
                propertyAdapter.set(Imgproc.getStructuringElement(
                        (Integer) cbShape.getSelectedItem(),
                        new Size(30.0 * sShapeWidth.getValue() / 1000, 30.0 * sShapeHeight.getValue() / 1000)));
            }
        };

        cbShape.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updater.run();
            }
        });


        cbShape.setRenderer(new ListCellRenderer<Integer>() {
            @Override
            public Component getListCellRendererComponent(final JList<? extends Integer> list, final Integer value,
                                                          final int index, final boolean isSelected,
                                                          final boolean cellHasFocus) {
                return new JLabel(shapeMap.get(value));
            }
        });

        panel.add(cbShape, c);

        sShapeWidth.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                if (!sShapeWidth.getValueIsAdjusting()) {
                    updater.run();
                }
            }
        });
        c.gridy = 1;
        panel.add(sShapeWidth, c);

        sShapeHeight.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                if (!sShapeHeight.getValueIsAdjusting()) {
                    updater.run();
                }
            }
        });
        c.gridy = 2;
        panel.add(sShapeHeight, c);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(panel);
    }

}
