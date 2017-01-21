

@echo on

cd /d %1
echo %2

if "%3"=="g++" (g++ %2.cpp -o %2.exe -ftest-coverage -fprofile-arcs)
if "%3"=="gcov" (gcov %2.cpp)
