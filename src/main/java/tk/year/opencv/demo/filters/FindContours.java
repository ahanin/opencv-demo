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

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import tk.year.opencv.demo.commons.ImageFilter;
import tk.year.opencv.demo.ui.annotations.FilterPanelFactory;

import java.util.ArrayList;
import java.util.List;

@FilterPanelFactory
public class FindContours implements ImageFilter {

    @Override
    public Mat filter(final Mat src) {

        final Mat dst = new Mat(src.rows(), src.cols(), src.type());
        src.copyTo(dst);

        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2GRAY);

        final List<MatOfPoint> points = new ArrayList<>();
        final Mat hierarchy = new Mat();
        Imgproc.findContours(dst, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_GRAY2BGR);

        return dst;
    }

    @Override
    public boolean isApplicable() {
        return true;
    }
}
