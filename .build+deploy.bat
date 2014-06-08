CALL gradle clean --refresh-dependencies asD
cd build
cd apk
CALL adb install -r Multigame-debug-unaligned.apk
cd ..
pause