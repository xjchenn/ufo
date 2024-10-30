# UW Facilities Optimizer (UFO)

## Overview
The UW Facilities Optimizer (UFO) is a comprehensive project designed to optimize the usage of facilities. The project consists of an Android application and a backend API server.

## Project Structure

. ├── Android/ │ ├── .gitignore │ ├── .idea/ │ ├── app/ │ │ ├── .gitignore │ │ ├── build.gradle │ │ ├── proguard-rules.pro │ │ └── src/ │ ├── build.gradle │ ├── gradle/ │ ├── gradle.properties │ ├── gradlew │ ├── gradlew.bat │ └── settings.gradle ├── api/ │ ├── .gitignore │ ├── auth.py │ ├── generateData.py │ ├── Makefile │ ├── models.py │ ├── readme.txt │ ├── requirements.txt │ ├── server.py │ ├── static/ │ ├── templates/ │ └── wsgi.py └── InternalProject/ └── InternalProject/


## Android Application
The Android application is located in the `Android/` directory. It includes the following key components:

- **`API.java`**: Handles backend API queries and populates frontend models.
- **`Model.java`**: Manages various Android components like notifications, intents, and services.
- **`FacilityInfo.java`**: Manages facility information, including navigation and view updates.

### Build Instructions
To build the Android application, navigate to the `Android/` directory and run:
```sh
./gradlew build
```

Key Dependencies
com.android.support:appcompat-v7:23.2.1
com.loopj.android:android-async-http:1.4.9
com.github.nkzawa:socket.io-client:0.3.0
Backend API
The backend API is located in the api/ directory. It includes the following key components:

auth.py: Handles authentication.
generateData.py: Script to generate data.
models.py: Defines data models.
server.py: Main server script.
wsgi.py: Entry point for WSGI-compatible web servers.
Running the Server
To run the server, follow these steps (tested on Linux):

Install pip:
```
easy_install pip
```
Install virtualenv:
```
pip install virtualenv
```
Create virtualenv:
```
virtualenv venv
source venv/bin/activate
```
Install dependencies:
```
pip install -r requirements.txt
```
Run server:

```
pip install -r requirements.txt
```

Internal Project
The InternalProject/ directory contains internal project files and configurations.

License
This project is licensed under the MIT License - see the LICENSE file for details.

Acknowledgments
Special thanks to the contributors of UW for their support.
Contact
For any inquiries, please contact the project maintainers.