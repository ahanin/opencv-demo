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
package tk.year.opencv.demo.examples;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.Resources;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import tk.year.opencv.demo.commons.Container;
import tk.year.opencv.demo.commons.FilteringImageProvider;
import tk.year.opencv.demo.commons.ImageFilter;
import tk.year.opencv.demo.commons.ImageFilters;
import tk.year.opencv.demo.commons.Provider;
import tk.year.opencv.demo.filters.Contrast;
import tk.year.opencv.demo.filters.Dilate;
import tk.year.opencv.demo.filters.Erode;
import tk.year.opencv.demo.filters.FindContours;
import tk.year.opencv.demo.filters.Grayscale;
import tk.year.opencv.demo.filters.MorphologyEx;
import tk.year.opencv.demo.filters.Threshold;
import tk.year.opencv.demo.ui.ActionListenerSupport;
import tk.year.opencv.demo.ui.FilterControlFactoryBundle;
import tk.year.opencv.demo.ui.ImagePanel;
import tk.year.opencv.demo.ui.ImageProvider;
import tk.year.opencv.demo.ui.ListAdapter;
import tk.year.opencv.demo.ui.ListScrollPane;
import tk.year.opencv.demo.ui.UpdateActionInvoker;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class FilterDemo extends JFrame {

    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(".png", ".jpg", ".jpeg", ".bmp");

    private final JFileChooser fileChooser;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private Thread cameraThread;

    private static final Function<Object,String> OBJECT_STRING_FUNCTION = new Function<Object, String>() {
        @Override
        public String apply(final Object o) {
            return String.valueOf(o);
        }
    };

    public FilterDemo() {
        super("OpenCV Filter Demo");

        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File f) {
                if (f.isDirectory()) {
                    return true;
                }
                final int extPos = f.getName().lastIndexOf('.');
                return extPos > 0 && IMAGE_EXTENSIONS.contains(f.getName().substring(extPos).toLowerCase());
            }

            @Override
            public String getDescription() {
                return "Image files";
            }
        });

        final JMenuBar menubar = new JMenuBar();
        final JMenu mFile = new JMenu("File");
        final JMenuItem mFileOpen = new JMenuItem("Open...");
        mFile.add(mFileOpen);
        menubar.add(mFile);
        setJMenuBar(menubar);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));

        final Container<Mat> srcImageContainer = new Container<>();

        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        setContentPane(panel);

        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.2;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;

        final JCheckBox cbCameraInput = new JCheckBox("Camera input");
        cbCameraInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (cbCameraInput.isSelected()) {
                    final VideoCapture capture = new VideoCapture(0);

                    final int width = (int) capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH);
                    final int height = (int) capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT);
                    capture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, width);
                    capture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, height);

                    final MatOfByte m = new MatOfByte();

                    cameraThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!Thread.interrupted() && isDisplayable() && capture.read(m)) {
                                try {
                                    srcImageContainer.set(m);
                                    repaint();
                                    Thread.yield();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            capture.release();
                            cameraThread = null;
                        }
                    });
                    cameraThread.start();
                } else {
                    if (cameraThread != null) {
                        cameraThread.interrupt();
                    }
                }
            }
        });
        panel.add(cbCameraInput, c);

        final ImageFilters filters = new ImageFilters();
        final Container<ImageFilters> filterListProvider = new Container<>(filters);

        final FilterPanel filterPanel = new FilterPanel(filterListProvider);
        filterPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (e.getClass() == FilterActionEvent.class) {
                    final FilterActionEvent filterEvent = (FilterActionEvent) e;
                    switch (filterEvent.getActionCommand()) {
                        case FilterPanel.TOGGLE_FILTER:
                            final ImageFilters.Entry entry = filterEvent.getFilterEntry();
                            entry.setEnabled(!entry.isEnabled());
                            break;
                        case FilterPanel.MOVE_FILTER_DOWN:
                            filters.moveDown(filterEvent.getFilterEntry());
                            filterPanel.update();
                            break;
                        case FilterPanel.MOVE_FILTER_UP:
                            filters.moveUp(filterEvent.getFilterEntry());
                            filterPanel.revalidate();
                            filterPanel.update();
                            break;
                        case FilterPanel.REMOVE_FILTER:
                            filters.remove(filterEvent.getFilterEntry());
                            filterPanel.revalidate();
                            filterPanel.update();
                            break;
                    }
                }
            }
        });
        c.weighty = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = GridBagConstraints.REMAINDER;
        panel.add(filterPanel, c);

        // Right Panel
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.weightx = 0.7;
        c.gridx = 1;

        // Images
        c.gridy = 0;

        final ImagePanel srcImagePanel = new ImagePanel<>(new ImageProvider(srcImageContainer));
        panel.add(srcImagePanel, c);

        final ImagePanel dstImagePanel = new ImagePanel<>(new ImageProvider(
                new FilteringImageProvider(srcImageContainer, filterListProvider)));

        c.gridy = 1;
        panel.add(dstImagePanel, c);

        mFileOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (fileChooser.showOpenDialog(FilterDemo.this) == JFileChooser.APPROVE_OPTION) {
                    final File file = fileChooser.getSelectedFile();
                    srcImageContainer.set(Highgui.imread(file.getPath()));
                    repaint();
                }
            }
        });

        filterPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dstImagePanel.repaint();
            }
        });
    }

    public void start() {
        setVisible(true);
    }

    public class FilterPanel extends JPanel implements ActionListenerSupport {

        public static final String MOVE_FILTER_UP = "moveFilterUp";
        public static final String MOVE_FILTER_DOWN = "moveFilterDown";
        public static final String REMOVE_FILTER = "removeFilter";
        public static final String TOGGLE_FILTER = "toggleFilter";

        private final ListScrollPane<ImageFilters.Entry> filterPane;

        public FilterPanel(final Provider<ImageFilters> filters) {
            final UpdateActionInvoker updateActionInvoker = new UpdateActionInvoker() {
                @Override
                public void invokeUpdateAction(final Object source) {
                    for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
                        listener.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, "update"));
                    }
                }
            };

            final FilterControlFactoryBundle filterControlFactoryBundle =
                    new FilterControlFactoryBundle(updateActionInvoker);

            setBorder(BorderFactory.createTitledBorder("Filters"));
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.weightx = 1;
            c.weighty = 0;
            c.gridx = 0;
            c.gridy = 0;

            final JPanel toolBar = new JPanel();
            toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
            final Button btnAdd = new Button("Add");
            final Button btnClear = new Button("Clear");
            toolBar.add(btnAdd);
            toolBar.add(btnClear);
            add(toolBar, c);

            final ImageFilterListModel listModel = new ImageFilterListModel(filters, filterControlFactoryBundle, listenerList);

            filterPane = new ListScrollPane<>(listModel);

            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1;
            c.gridy = 1;
            add(filterPane, c);

            final Properties props = new Properties();
            try {
                props.load(Resources.getResource("filters.properties").openStream());
            } catch (IOException e) {
                throw new RuntimeException("Unable to load list of filters.", e);
            }

            final String[] filterClassNames = Collections2.transform(props.values(), OBJECT_STRING_FUNCTION).toArray(new String[props.size()]);

            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final String klass = (String) JOptionPane.showInputDialog(
                            FilterPanel.this,
                            "Filter:",
                            "Add filter",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            filterClassNames,
                            "ham");

                    if (klass != null) {
                        final Class<? extends ImageFilter> aClass;
                        try {
                            aClass = (Class<? extends ImageFilter>) Class.forName(klass);
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException("Failed to resolve filter class: " + klass, ex);
                        }

                        final ImageFilter filter;
                        try {
                            filter = aClass.newInstance();
                        } catch (InstantiationException | IllegalAccessException ex) {
                            throw new RuntimeException("Failed to create filter: " + klass, ex);
                        }

                        filters.get().add(filter);
                        update();
                        FilterDemo.this.repaint();
                    }
                }
            });
            btnClear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    filters.get().clear();
                    update();
                    FilterDemo.this.repaint();
                }

            });
        }

        public void update() {
            filterPane.update();
            filterPane.validate();
        }

        private class ImageFilterListModel implements ListAdapter<ImageFilters.Entry> {

            private final Provider<ImageFilters> filters;
            private final FilterControlFactoryBundle filterControlFactoryBundle;
            private final EventListenerList eventListenerList;

            private ImageFilterListModel(final Provider<ImageFilters> filters,
                                         final FilterControlFactoryBundle filterControlFactoryBundle,
                                         final EventListenerList eventListenerList) {
                this.filters = filters;
                this.filterControlFactoryBundle = filterControlFactoryBundle;
                this.eventListenerList = eventListenerList;
            }

            @Override
            public int getSize() {
                return filters.get().getCount();
            }

            @Override
            public ImageFilters.Entry getElementAt(final int pos) {
                return filters.get().getEntryAt(pos);
            }

            @Override
            public Component getComponent(final ImageFilters.Entry entry) {
                final JPanel panel = new JPanel(new GridBagLayout());
                panel.setBorder(BorderFactory.createTitledBorder(entry.getFilter().getClass().getSimpleName()));

                final GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.BOTH;
                c.gridx = 0;
                c.gridy = 0;
                c.weightx = 0;
                c.weighty = 1;

                final JPanel controlPanel = new JPanel();
                controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
                final JCheckBox cbEnabled = new JCheckBox("Enabled", entry.isEnabled());
                cbEnabled.addActionListener(createFilterEventEmitter(TOGGLE_FILTER, entry, cbEnabled));
                controlPanel.add(cbEnabled);

                final BufferedImage imgUp = readImage("arrow-up-icon.png");
                final JButton btnUp = new JButton();
                btnUp.setIcon(new ImageIcon(imgUp));
                btnUp.setSize(16, 16);
                btnUp.addActionListener(createFilterEventEmitter(MOVE_FILTER_UP, entry, btnUp));
                controlPanel.add(btnUp);

                final JButton btnDown = new JButton();
                final BufferedImage imgDown = readImage("arrow-down-icon.png");
                btnDown.setIcon(new ImageIcon(imgDown));
                btnDown.addActionListener(createFilterEventEmitter(MOVE_FILTER_DOWN, entry, btnDown));
                btnDown.setSize(16, 16);
                controlPanel.add(btnDown);

                final JButton btnDelete = new JButton();
                final BufferedImage imgDelete = readImage("remove-icon.png");
                btnDelete.setIcon(new ImageIcon(imgDelete));
                btnDelete.addActionListener(createFilterEventEmitter(REMOVE_FILTER, entry, btnDelete));
                btnDelete.setSize(16, 16);
                controlPanel.add(btnDelete);

                panel.add(controlPanel, c);

                c.gridx = 1;
                c.weightx = 1;

                final Component component = filterControlFactoryBundle.createFilterControl(entry.getFilter());
                panel.add(component, c);

                return panel;
            }

            private ActionListener createFilterEventEmitter(final String action, final ImageFilters.Entry entry, final Object source) {
                return new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final ActionListener[] listeners = eventListenerList.getListeners(ActionListener.class);
                        for (ActionListener listener : listeners) {
                            final FilterActionEvent actionEvent = new FilterActionEvent(action, source);
                            actionEvent.setFilterEntry(entry);
                            listener.actionPerformed(actionEvent);
                        }
                    }
                };
            }

            private BufferedImage readImage(final String filename) {
                final BufferedImage imgUp;
                try {
                    imgUp = ImageIO.read(getClass().getClassLoader().getResourceAsStream(filename));
                } catch (IOException e) {
                    throw new RuntimeException("Error reading icon image", e);
                }
                return imgUp;
            }
        }

        public void addActionListener(ActionListener l) {
            listenerList.add(ActionListener.class, l);
        }

    }

    public static class FilterActionEvent extends ActionEvent {

        private static final long serialVersionUID = 3081977270494285160L;

        private ImageFilters.Entry filterEntry;

        public FilterActionEvent(final String action, final Object source) {
            super(source, 0, action);
        }

        public void setFilterEntry(final ImageFilters.Entry filterEntry) {
            this.filterEntry = filterEntry;
        }

        public ImageFilters.Entry getFilterEntry() {
            return filterEntry;
        }
    }
}
