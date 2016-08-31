JDKDIR=/usr/lib/jvm/java-8-openjdk-armhf
OBJ=sensor.so
SRC=sensor.c hardware.c
$(OBJ): $(SRC) makefile
	gcc -shared $(SRC) -o $(OBJ) -I$(JDKDIR)/include -I$(JDKDIR)/include/linux -fPIC -lwiringPi
