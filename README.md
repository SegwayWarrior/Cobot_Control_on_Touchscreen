# Cobot Control on Touchscreen

## Introduction
![main_pic](/images/main_pic2.jpg)
For my final project, my goal was to design a drive system for a haptic device called the Touchbot, which is currently being developed by Professor Colgate and his
team at Northwestern University.  The Touchbot allows a user to recieve haptic feedback on a single finger
while they are interacting with a touchscreen.  The drive system acts as a cariage for the Touchbot, as well as a cobot, letting the user push
the device in the direction the wheel is facing.  This is useful for directing the user to a location on screen without requiring a second motor do drive the cobot on its own.

## Specifications
![gallery](/images/gallery.png)
The Touchbot drive system had some unique specifications to consider during development:
- The cobot needs to be low cost and easily produced.  
- The height needs to be minimized, which led to the O-ring wheels being embedded into the gear(s) that support them. 
- Allow the possiblity of using multiple Touchbots, ideally one for each of the user's fingers.  This meant limiting the overall width of the drive system to under 30mm.  

While these challenges were reasonable, we found that including all of the required parts meant splitting our plans into two paths.  The first being a less restrictive design with the goal of actually making the drive system work with a touchpad.  The other being a more idealized solution that focuses on fitting everything into as small of a package as possible.


## Development Process
![cad_pic](/images/2_motor_cad.png)
- We decided on a single wheel design for our cobot. It provided a natrual feel, with a slight tilt perpendicular the wheel's orientation which helped with finding it.  
- To tell the ESP32 the location of the Touchbot relative to the screen, we had a couple of options to go with.  The first being a combination of an encoder, to read the wheel's direction, and an imu, to read the location and orientation of the drive system.  While this option did have some promise, we decided to take advantage of the fact that the Touchbot was travelling on a touchscreen.  We did so by creating conductive PLA leaves that ride along the screen, providing the position and orientation of the drive system at all times.
- Only one motor was required but since we wanted to put it next to the user's finger, we placed another one on it's oppisite side simply to counterbalance the first one about the single wheel.  The second motor's shaft is not actually attached to anything.

## Programming
![running_underside](/images/running_underside.gif)
The programming required for the drive system is split up into two sources, Arduino and Android Studio.  The Arduino code, bluetooth_simple10.ino, takes the position information from the Android app and is in charge of all of the controls.  As you can see below, the previous position is saved in order to calculate the trajectory of the cobot.  Then the rotational speed from the previous timestep is taken into account in order to predict the future position.  Once we know this, we can find the tangent angle of the previous position to the circle using the law of cosines.  Then we can compare this to the angle created from circle's center, previous, and future position.  If the angle is greater than the tangent angle, than the trajectory is moving away from the circle.  If it's less, than its moving into the circle.  
![explain_code](/images/explain_code.jpg)
Then, we check if the cobot is moving cw or ccw relative to the circle's center.  This is done by comparing the slopes of the lines created from the circle's center to the previous postion, and the circle's center to the future position.  If slope_future is greater than slope_previous, than the cobot is moving cw and visa versa.  With these two pieces of information (into/out of circle and cw/ccw) we can determine whether the wheel should turn cw or ccw.  It is true that we can not tell if the cobot is slippin on the screen, but if this is the case all we can do is continue to turn the wheel until the user starts following the wheel's direction, which this program will do for us. 
![explain_code2](/images/explain_code2.jpg)

The responsiblities of the Android Studio app are much simpler.  It is in charge of showing a circle on screen, keeping track of multiple touch positions, and sending those positions to the Arduino over bluetooth.  Despite this, the actual code required many different components, especially making the bluetooth capabilities work.  As a newcommer to Android Stuido and Java, this took about as much time to put together as the arduino code.
![running_topside](/images/running_topside.gif)

## Running Code
To run the android app, download multitouch_bluetooth.apk to your touchpad or phone.  With file manager, you just need to click on the file and it will ask if you want to install the app.  You may have to grant access for unkown apps, but it should automatically lead you to where you need to change the setting.  The source code for the android app is provided in the directory multitouch_bluetooth_source and can be checked out with android_studio. I used Android Studio 4.0.

The code for the arduino is touchpad_control.ino in its respective directory.  In order to run the code, make sure that the arduino software on your computer is configured for an ESP32 board.  You can follow [this link](https://randomnerdtutorials.com/installing-the-esp32-board-in-arduino-ide-windows-instructions/) to get that set up.  Once that's done, you just need to load it onto the ESP32 and you're good to go.  

To start running the two together, turn on the ESP32 with the code installed and start up multitouch_bluetooth.apk.  Hit the search button to find your device and hit connect.  You'll need to connect to the ESP32 before you can see the next screen.  You'll then be led to a screen with a circle in the middle.  You should also see the positions of the first two touch points from mulitouch.  The center of these two points are being sent to the arduino in order  direct the wheel of the cobot.  With the cobot on screen, you should notice the wheel turning in response to your movement.  Try using two fingers instead of the leaves in order to look at the wheel while it's moving.  Notice how the wheel changes direction based on what direction you're moving around the circle, as well as whether or not you're moving into or out of the circle.  When using the cobot on screen, you should always be lead twords the circle while also being lead out of it when inside.  This means the controls are working as intended.  


## Cycloidal Gear Box
![cycloid](/images/cycloid.gif)
![cycloid_running](/images/cycloid_running.gif)
After getting a design put together that worked with the touchpad, we decided to start working on a more ideal version where we could put a small motor directly underneath the user's finger, allowing us to make room for touchbots under multiple fingers. This created a challenge, because the typical gearbox required for this design was a planetary gearbox, which uses small gears that could not be easily produced.  Another option could have been a hormonic gearbox, but it had its own challenges with small gears and flexable components.  In the end, we decided to use a cycloidal drive as shown below.  This gives us similar benifits of a harmonic drive, but it dosen't require any flexible parts, and the outer gear requires only half the number of teeth of a harmonic drive with a simliar gear ratio.  The one downside is that a small vibration is common with cycloidal gears because of their offset.  This can be balanced with another cycloidal gear which is oppisitly offset, although I did not find the vibrations much different than nomral motor usage.  Even though this design showed promise, we did run into issues related to dealing with the axial loads.  Initially, this would cause the gearbox to completly stop running, but with the implimentation of some thrust bearings, we were able to keep the gearbox running under load as shown below.  Unfortunatly, the speed reduction from the axial loads were still a problem.  If this were to be our final solution, we would need to spend some time trying to midigate the axial loads as much as possible.
![cycloid_cad1](/images/cycloid_cad1.jpg)
![cycloid_cad2](/images/cycloid_cad2.jpg)

## Insights and Future Work
While the program worked with a circle, it would be more difficult to make this work with other shapes.  We would either need to make maps of the screen, where each section has it's own arc with it's own center.  (Straight lines would have very large radii)  We would also need to fine tune how fast the wheel rotates based on it's distance from the arc's center. Another option we considered was putting conductive leaves directly on the center gear in order to read the direction of the wheel.  This may be possible, but it would increase the axial load on the cycloidal gearbox which, as stated above, is not ideal.  We could also simply use an encoder with the conductive leaves in order to use a more traditional control algorithm, but this may be a problem in the long run since minimizing space is a priority.
![cycloid_cad3](/images/cycloid_cad3.jpg)

As is common with new ideas, some of the specifications changed while we were wrapping up the project.  Now we need the touchbot to have direct access to the touchscreen underneath it, as well as being as close to the touchscreen as possible.  Unfortunatly, this means that having a motor, gearbox, and single wheel directly underneath the touchbot will no longer be viable.  Fortunatly, we did go through several iterations of designs, and one of them included a Synchro (like) drive, shown below.  The concept is simple, the rotation of all the wheels are controlled by one motor, with a common design being a larger, central gear that rotates with outer gears, each attached to a wheel.  Usually, this is done with three equally spaced wheels, but since we were trying to minimize the total width, we decided to use four wheels with two pairs being spaced as close together as possible.  While this is a great solution to minimize actuators, the Synchro drive does have the downside of not being able to control the orientation of it's body to the surface it resides on.  With the conductive leaves, we are able to measure the body's orientation, but since it's not rotationlly symmetric, it does cause the issue of not being able to fix the orientation.  The Synchro drive does otherwise seem to be an ideal solution to our problem, so moving forward we will try to make the cobot rotationlly symmetric so the user will not notice the orientation offset.  This relates to our original problems of fitting all of the compenents into a small space. As this project continues, I suspect that Professor Colgate will continue with the Synchro drive, while slowly making things smaller and more symmetric.
![Synchro_cad1](/images/synchro_cad1.jpg)

One thing that suprised me was how well the conductive PLA worked with the touchscreen.  It even allowed us to identify when the user did or didn't have their finger on the cobot.  In addition, we also tested some conductive O-rings, which were a bit tricky to get our hands on.  As you can see below, we did have some initial success with conductive wheels being recognized on my cell phone, but my touchpad was not as sensitive.  Even though we couldn't use these with our specific equipment, they did show promise.  Perhaps by using bigger wheels for a larger contact patch, they could work consistently enough to remove the conductive leaves all together.  
![cond_wheels](/images/cond_wheels.gif)

## Special Thanks
A big thanks to Professor Colgate for allowing me to work with him on this innovative idea.  Also, thank you to my advisors, Professor Matthew Elwin and Bill Strong for their consistant support and creative ideas during the entire process.
