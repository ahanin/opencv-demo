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
package tk.year.opencv.demo.filters;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.opencv.imgproc.Imgproc;

import tk.year.opencv.demo.commons.ImageFilter;
import tk.year.opencv.demo.ui.annotations.FilterPanelFactory;
import tk.year.opencv.demo.ui.controls.StructuringElement;

@FilterPanelFactory
public class Erode implements ImageFilter {

    @StructuringElement
    private Mat structuringElement;

    @Override
    public Mat filter(final Mat src) {

        final Mat dst = new Mat(src.cols(), src.rows(), CvType.CV_8UC3);
        src.copyTo(dst);

        Imgproc.erode(dst, dst, structuringElement);

        return dst;
    }

    @Override
    public boolean isApplicable() {
        return structuringElement != null;
    }
}
