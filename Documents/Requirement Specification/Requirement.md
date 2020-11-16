## 																Requirement Specification

#### Brief

This mobile system is designed for the pregnant women to monitor their vital signs and help them keep healthy during the pregnancy. To be easy to use for most of the pregnant women, the application should be easy enough to operate and understand. And the system should be long-term running to support the data mining. 



### User Requirement

This part specify the requirements considered by the target users: pregnant women. And here are the kernel requirements and additional requirements  from researches and meeting communication with customers.

- **Kernel Requirement**
  1. Users want to see the current heart rate,  brain wave by this system.
  2. Pregnant users is concerned about their own healthy conditions and want to know if they are in the safe status.
  3. Users want to see their bodyweight change during the whole pregnancy. 
  4. Users want to get some information about their body data which is visualized and easy to understand.
  5. Users want get some advice about becoming healthy during pregnancy.
- **Additional Requirement**
  1. Users do not want to wear the wearable devices too long throughout the day.
  2. Users want a low threshold to use the application.
  3. Users want to be reminded to use the function of application.



### System Requirement 

#### Functional Requirement

- **Data Capture Function**
  
  1. The mobile system (Android Application) can connect with the wearable devices which include heart rate belts, brain ware, smart scale, temperature sensor, breathing rate sensor, blood pressure sensor.
  
  2. The vital data will be sent from wearable device to mobile application every minutes.
  
  3. The bodyweight data is captured by manual record.
  
  4. The year and height is recorded from users.
  
  5. The data will be sent to the integration module for the next step.
  
  6. When the application detects that the data signals is interrupt or zero, the capturing will stop.
  
- **Data integration Function**

  1. The data will be processed and temporarily store in the application's memory.

  2. The initial data will be checked and the error data will be cleaned.

  3. Multiply types of data can be integration in the mobile system.

  4. The data will be split into different categories and shown to the users.

  5. The data will be sent to the analysis module for the next step.

- **Data report and analysis Function**
  
  1. Application could send a piece of message (every-day report) about the vital status analysis and advices at 8 a.m. 
  
  2. The report is about his/her body vital data. The report will includes every-day vital signs data after processing and will give some advices according to the analysis algorithm such as having more sleep or having abnormal status and need to see doctor.
  
  3. The system could generate the report by the suggestion data and analysis algorithm daily and monthly (weekly).
  
  4. The report could be preserved by users and exported.
  
- **Data storage Function**
  
  1. The data from users should be stored both in PC and mobile system.
  
  2. The received data will be stored for a month in the application memory for users' views.
  
  3. Considering the limited memory, the details of past data will be cleaned up and the daily report will be stored in the mobile system.
  
  4. The user could to connect with the PC monthly and send the data for the long-term storage.
  
- **Task management (interface)**
  
  1. The system will push the every-day reports, suggestions and analysis at 8 a.m.
  2. The system will send a message to user if user does not. wear the devices.
  3. The data capture function can be switch on/off.
  4. When the application can not find the data, application will remind user to check the wearable device wearing.
  5. The system will send a message if the user does not record the body weight until 8 p.m. every day.
  6. The user can view four mode of application: 
     - current/last measured data
     - today's report
     - Long term report
     - Settings



#### Non-functional Requirement

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



### Technical Specification

1. The mobile system should support the device types:
   - (Heart Rate) Chest belt: **Polar H10**
   - (Brain Wave) Brain belt: **NeuroSky TGAM**
   - Smart Scale: **Yunmai**
   - ...
2. The data transmission between wearable devices and mobile application is based on blue-tooth protocol.
3. The wearable devices official SDK is open-source for the development and will be used during the project.
4. The development platform is android studio and the development language is Java.
5. The mobile system will operate in PC for demonstration (simulator) and on android cellular phone with Android 11 system.
6. The simulator of mobile system is GenyMotion/AVD emulator. It will be used to simulate the process of mobile system for demonstration and test.
7. The data to test is from the research group which include the weight change of pregnant women and references. 

