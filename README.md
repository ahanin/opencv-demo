[OpenCV](http://opencv.org) Demo Application
============================================

You can experiment with different filters by stacking them and playing with their settings. Try using your webcam too.

Installation
------------

Before running the demo you will need to

1. install OpenCV Java bindings:


    $ mvn install:install-file -DgroupId=opencv -DartifactId=opencv -Dversion=2.4.8 -Dpackaging=jar -Dfile=lib/opencv-248.jar


2. compile OpenCV library and put it to `lib` directory:

    *   [On Linux](http://docs.opencv.org/doc/tutorials/introduction/linux_install/linux_install.html)
    *   [On Windows](http://docs.opencv.org/doc/tutorials/introduction/windows_install/windows_install.html)

Running
-------

To run the example type:

    $ mvn exec:exec
