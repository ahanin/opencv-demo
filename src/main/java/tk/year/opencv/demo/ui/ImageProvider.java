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

import org.opencv.core.Mat;

import org.opencv.imgproc.Imgproc;

import tk.year.opencv.demo.commons.Provider;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageProvider implements Provider<Image> {

    private final Provider<Mat> matProvider;

    public ImageProvider(final Provider<Mat> matProvider) {
        this.matProvider = matProvider;
    }

    @Override
    public Image get() {

        final Mat src = matProvider.get();
        final Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2BGR);

        byte[] bytes = new byte[dst.cols() * dst.rows() * (int) dst.elemSize()];
        dst.get(0, 0, bytes);

        final BufferedImage out = new BufferedImage(dst.cols(),
                                                    dst.rows(),
                                                    BufferedImage.TYPE_3BYTE_BGR);
        out.getRaster().setDataElements(0, 0, dst.cols(), dst.rows(), bytes);

        return out;
    }

    @Override
    public boolean present() {
        return matProvider.present();
    }
}
