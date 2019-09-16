CC = gcc

main : main.c button.o motor.o
	$(CC) -o main main.c ./obj/button.o ./obj/motor.o -lwiringPi

./obj/button.o : button.c
	$(CC) -c button.c -lwiringPi 

./obj/motor.o : motor.c
	$(CC) -c motor.c -lwiringPi

