cmake_minimum_required(VERSION 3.4.1)

add_library(
             native-lib
             SHARED
             src/main/cpp/wrapper.cpp
             src/main/cpp/application.cpp
             src/main/cpp/roi.cpp
             src/main/cpp/calibration_object.cpp
             src/main/cpp/hand/hand.cpp
             src/main/cpp/hand/finger.cpp
             src/main/cpp/utils/functions.cpp
)

find_library(
            log-lib
            log
            android-lib
            android
)

target_link_libraries(
                       native-lib
                       log
                       android
)

set(OpenCV_DIR "../OpenCVLibrary/src/sdk/native/jni")
find_package(OpenCV REQUIRED)
message(STATUS "OpenCV libraries: ${OpenCV_LIBS}")
target_link_libraries(native-lib ${OpenCV_LIBS})