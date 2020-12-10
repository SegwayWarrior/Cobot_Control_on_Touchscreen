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

## Future Work
