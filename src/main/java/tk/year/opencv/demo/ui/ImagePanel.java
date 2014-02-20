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
package tk.year.opencv.demo.ui;

import tk.year.opencv.demo.commons.Container;
import tk.year.opencv.demo.commons.Provider;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;

public class ImagePanel<I extends Image> extends JPanel {

    private final Provider<I> imageProvider;

    private final Container<I> imageContainer = new Container<>();

    public ImagePanel() {
        this.imageProvider = imageContainer;
    }

    public ImagePanel(final Provider<I> imageProvider) {
        this.imageProvider = imageProvider;
    }

    public void setImage(I image) {
        this.imageContainer.set(image);
    }

    @Override
    protected final void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (imageProvider.present()) {
            g.drawImage(processImage(imageProvider.get()), 0, 0, null);
        }
    }

    protected I processImage(final I image) {
        return image;
    }
}
