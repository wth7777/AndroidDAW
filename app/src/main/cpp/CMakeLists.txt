cmake_minimum_required(VERSION 3.10.2)
project("AudioDAW")

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/oboe oboe)

add_library(
        audio_engine
        SHARED
        audio_engine.cpp )

# Find the log library
find_library(
        log-lib
        log )

target_link_libraries(
        audio_engine
        oboe
        ${log-lib} )
