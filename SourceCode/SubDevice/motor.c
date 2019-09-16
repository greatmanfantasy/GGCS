#include <stdio.h>
#include <wiringPi.h>
#include <softPwm.h>
#include "gpio.h"

int motor(void)
{
	//use physical pin
	if(wiringPiSetupPhys() == -1)
		return -1;

	pinMode(MTR1, OUTPUT);
	pinMode(MTR2, OUTPUT);
	pinMode(CTRMTR, OUTPUT);
	softPwmCreate(CTRMTR, 100, 100);
	softPwmWrite(CTRMTR, 1000);

	digitalWrite(MTR1, HIGH);
	digitalWrite(MTR2, LOW);
	delay(70);
	digitalWrite(MTR1, LOW);
	digitalWrite(MTR2, HIGH);
	delay(100);

	pinMode(MTR1, INPUT);
	pinMode(MTR2, INPUT);

	return 0;
}
