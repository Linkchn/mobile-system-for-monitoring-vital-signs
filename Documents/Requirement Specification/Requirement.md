### 								Requirement Specification

---

#### User Requirement

This mobile system is designed for the pregnant women to monitor their vital signs and help them keep healthy during the pregnancy. To be easy to use for most of the pregnant women, the application should be easy enough to operate and understand. 

- **Kernel Requirement**
  1. **Make Connection:** Users could use  this application to connect the wearable device (given types)
  2. **Bodyweight management:** Users would receive a caution every day to record the bodyweight and can type the body weight number manually.
  3. **Report receive：** 
     1. Users can view kinds of data in the last cycle.
     2. Users can view the visualized data diagrams.
     3. Users can be given guidances of life and health.
     4. User can see the long term data for a period of time.
- **Additional Requirement**
  1. Users can open the application directly without log in. (Easy to start using)
  2. Users can click the bottom menu to select module. 
  3. Users can store their reports for further use.
  4. Users can get help by application about operations.
  5. Users can enter the personal body information for more accurate forecast.



---

#### Functional Requirement

- **Data Capture module**
  1. The mobile system (Android Application) can connect with the wearable devices which include heart rate belts, brain ware, smart scale, temperature sensor, breathing rate sensor, blood pressure sensor.
  2. The vital signs will be measure every minute. The vital data will be sent from wearable device to mobile application every 30 minutes.
  3. The bodyweight data is captured by manual record.
  4. The year and height is recorded from users.
  5. The data will be sent to the integration module for the next step.
  6. The connection between device and system is blue-tooth.
- **Data integration module**
  1. The data will be updated every 30 minutes when the system collects data from wearable device.
  2. The data will be processed and temporarily store in the application's memory.
  3. Multiply types of data can be integration in the mobile system.
  4. The received data will be stored for a month in the application memory for users' views.
  5. The data will be split into different categories and shown to the users.
  6. The data will be sent to the analysis module for the next step.
- **Data report and analysis module**
  1. Application could send a piece of message (every-day report) about the vital status analysis and advices at 8 a.m. 
  2. The report is about his/her body vital data. The report will includes every-day vital signs data after processing and will give some advices according to the analysis algorithm such as having more sleep or having abnormal status and need to see doctor.
  3. The system could generate the report by the suggestion data and analysis algorithm daily and monthly (weekly).
  4. The report could be preserved by users and exported.
- **Data storage module**
  1. The data from users should be stored both in PC and mobile system.
  2. Considering the limited memory, the details of past data will be cleaned up and the daily report will be stored in the mobile system.
  3. The user could to connect with the PC monthly and send the data for the long-term storage.
- **Task management module (interface)**
  1. The system will push the every-day reports, suggestions and analysis at 8 a.m.
  2. The system will send a message to user if user does not. wear the devices.
  3. The data capture function can be switch on/off.
  4. The system will send a message if the user does not record the body weight until 8 p.m. every day.
  5. The user can view three mode of application: 
     - current/last measured data
     - today's report
     - Long term report
     - Settings



---

### Non-functional Requirement

- **Safety**
  2. The security of personal data should be guaranteed.
  4. The wearable device connections should be allowed by users. User can cancel the connections at any time.
- **Performance**
  1. The advice and message should be accurate and on time.
  2. The correctness of data analysis algorithm should be guaranteed.
- **compatibility**
  1. The application should be compatible for android platform.
  2. The application User interface should be compatible for different size of screen.
  3. The advice should be given according to user status
  4. System should deal with the exception of data.



### Technical Requirement

1. The mobile system should support the device types:
   - (Heart Rate) Chest belt: **Polar H10**
   - (Brain Wave) Brain belt: **NeuroSky TGAM**
   - Smart Scale: **Yunmai**
   - ...
2. The data transmission between wearable devices and mobile application is based on blue-tooth protocol.
3. The wearable devices official SDK is open-source for the development and will be used during the project.
4. The development platform is android studio and the development language is Java.
5. The mobile system will operate in PC for demonstration (simulator) and on android cellular phone with Android 11 system
6. The simulator of mobile system is genymotion/AVD emulator. It will be used to simulate the process of mobile system for demonstration and test.
7. The data to test is from the research group which include the weight change of pregnant women and references. 

