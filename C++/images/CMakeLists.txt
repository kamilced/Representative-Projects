cmake_minimum_required(VERSION 3.15)
project(images)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_COMPILER /usr/bin/g++-8)



set(GCC_COVERAGE_COMPILE_FLAGS "-Wall -Wextra -pedantic -O3 -std=c++2a -fconcepts")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${GCC_COVERAGE_COMPILE_FLAGS}" )

add_executable(test test.cc)
add_executable(example color.cc coordinate.cc bmp.cc example.cc)
