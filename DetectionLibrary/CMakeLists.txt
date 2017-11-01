cmake_minimum_required(VERSION 3.4.1)

add_library(
             native-lib
             SHARED
             src/main/cpp/wrapper.cpp
             src/main/cpp/detector.cpp
)

find_library(
            log-lib
            log
)


target_link_libraries(
                       native-lib
                       ${log-lib}
)

set(OpenCV_DIR "../OpenCVLibrary/src/sdk/native/jni")
find_package(OpenCV REQUIRED)
message(STATUS "OpenCV libraries: ${OpenCV_LIBS}")
target_link_libraries(native-lib ${OpenCV_LIBS})