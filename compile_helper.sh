#!/bash/sh

cd $1

if [ $3 = "g++" ]
then
g++ $2.cpp -o $2.exe -ftest-coverage -fprofile-arcs
fi

if [ $3 = "gcov" ]
then
gcov $2.cpp
fi
