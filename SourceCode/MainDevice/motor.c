#include <stdio.h>
#include <wiringPi.h>

#define PIN1 11 //gpio 0
#define PIN2 12 //gpio 1

int main(void)
{
	int i;

	//use physical pin
	if(wiringPiSetup() == -1)
		return -1;

	pinMode(PIN1, OUTPUT);
	pinMOde(PIN2, OUTPUT);

	for(i=0; i < 500; ++i){
		digitalWrite(PIN1, HIGH);
		digitalWrite(PIN2, LOW);
		delay(50);
	}



	return 0;
}
