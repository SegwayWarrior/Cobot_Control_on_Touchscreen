# Final_Project

## Problem Statement:
For my final project, my goal was to design a drive system for a haptic device called the Touchbot, which is currently being developed by Professor Colgate and his
team at Northwestern University.  Without getting into the details of his system, the Touchbot is designed to allow a user to get haptic feedback on a single finger
while they are interacting with a touchscreen.  The drive system would act as a cariage for the Touchbot, as well as a cobot like device, letting the user push
the device in one direction, while the cobot only changes the direction the wheel(s) are facing, aka the direction the user can move in.

## Specifications
The Touchbot drive system had some unique specifications to consider during development.  First of all, Professor Colgate wanted the touchbot to be as close to
the screen as possible, which led to the O-ring wheel's being embedded into the gear(s) that support them. Another specification was to allow the possiblity of using multiple touchbots, ideally one for each of the user's fingers.  This means limiting the overall width of the drive system to under 30mm.  While all of these challenges are plausible, we found that fitting all the parts required meant splitting our plans into two paths.  The first being a less restrictive design with the goal of actually making the drive system work with a touchpad.  The other being a more idealized solution that focuses on fitting everyhing into as small of a package as possible.

## Development Process
In order to locate the Touchbot relative to the screen, we had a couple of options to go with.  The first being a combination of an encoder to read the wheel's direction and imu to read the location and orientation of the drive system.  While this option did have some promise, we decided to take advantage of the fact that the Touchbot was travelling on a touchscreen by creating conductive PLA leaves that ride along the screen, providing the position and orientation of the drive system.  

## Programming
The programming required for the drive system is split up into two sources, Arduino and Android Studio.  The arduino code, bluetooth_simple10.ino, takes the position information from the android app and is in charge of all of the controls.  As you can see below, the previous position is saved in order to calculate the trajectory of the cobot.  Then the rotational speed from the previous timestep is taken into account in order to predict the future position.  Once we know this, we can find the tangent angle of the previous position to the circle using the law of cosines.  Then we can compare this to the angle created from circle's center, previous, and future position.  If the angle is greater than the tangent angle, than the trajectory is moving away from the circle.  If it's less, than its moving into the circle.  Then, we check if the cobot is moving cw or ccw relative to the circle's center.  This is done by comparing the slopes of the lines created from the circle's center to the previous postion, and the circle's center to the future position.  If slope_future is greater than slope_previous, than the cobot is moving cw and visa versa.  With these two pieces of information (into/out of circle and cw/ccw) we can determine whether the wheel should turn cw or ccw.  It is true that we can not tell if the cobot is slippin on the screen, but if this is the case all we can do is continue to turn the wheel until the user starts following the wheel's direction, which this program will do for us. 

The responsiblities of the Android Studio app are much simpler.  It is in charge of showing a circle on screen, keeping track of multiple touch positions, and sending those positions to the Arduino over bluetooth.  Despite this, the actual code required to make this work required many different components, especially making the bluetooth capabilities work.  As a newcommer to Android Stuido and Java, this took about as much time to put together as the arduino code.

## Cycloidal Gear


## Insights and Future Work
