cmake_minimum_required(VERSION 3.0.0)
project(csv-parser VERSION 0.1.0)

# Compiler settings
set(CMAKE_CXX_STANDARD 20)
<<<<<<< HEAD
set(GCC_DEBUG_WARNINGS "-Wall;-Wextra;-Wpedantic;-Winit-self;-Wold-style-cast;-Wcast-qual;-Wundef;-Woverloaded-virtual;-Wconversion;-Wlogical-op;-Wzero-as-null-pointer-constant")
=======
set(GCC_DEBUG_WARNINGS "-Wall;-Wextra;-Wpedantic;-Winit-self;-Wold-style-cast;-Wcast-qual;-Wundef;-Woverloaded-virtual;-Wconversion;-Wzero-as-null-pointer-constant")
>>>>>>> 24e14f120f26fd321cfa155d6f13cf689c942430
set(GCC_DEBUG_SANITIZE "-fsanitize=address;-fsanitize=undefined;-fsanitize=leak;-fsanitize=float-cast-overflow;-fno-omit-frame-pointer")
set(GCC_LIKE "$<COMPILE_LANG_AND_ID:CXX,ARMClang,AppleClang,Clang,GNU,LCC>")
add_compile_options("$<$<CONFIG:DEBUG>:$<${GCC_LIKE}:${GCC_DEBUG_WARNINGS}>>")
add_compile_options("$<$<CONFIG:DEBUG>:$<${GCC_LIKE}:${GCC_DEBUG_SANITIZE}>>")
add_link_options("$<$<CONFIG:DEBUG>:$<${GCC_LIKE}:${GCC_DEBUG_SANITIZE}>>")

# Main program
set(SRC_DIR ${PROJECT_SOURCE_DIR}/src)
set(SRC_FILES ${SRC_DIR}/parser.cpp)
add_executable(main ${SRC_FILES} ${SRC_DIR}/main.cpp)

# Test
include(CTest)
enable_testing()
add_executable(main-test ${SRC_FILES} test/test.cpp)
add_test(test main-test) 
