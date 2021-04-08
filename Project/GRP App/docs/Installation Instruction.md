# Installation Instruction

> Required Android version: Android 8.0+

## Install by released APK file

The released APK file is located at [./app/release](./app/release), named "app-release.apk". It has full signature (APK Signature Scheme v1 and v2). So you can install the application directly on your Android devices or emulator.



## Install by Build the project

The other way to install the application is to build the project manually. Here is the instruction using [Android Studio](https://developer.android.com/studio).

1. Click **File** - **Open** to open the project.
2. Waiting for the Gradle to index the project and download necessary dependencies. (The progress is shown at the right bottom corner).
3. (Without Android device) Click **Tool** - **AVD Manager**, and create an Android virtual device. 
4. (With Android device) Connect android device to the computer using a cable, and make sure give the permission of debugging. 
5. Click **Run** - **Run 'app'** to run the application on the emulator or the Android device. 