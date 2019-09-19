#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <wiringPi.h>
#include "gpio.h"

int btnPinNum[4] = {BTN1, BTN2, BTN3, BTN4};
int lightPinNum[4] = {LIGHT1, LIGHT2, LIGHT3, LIGHT4};

void init(void){
	int i = 0;
	for(i = 0; i < 4; ++i){
		pinMode(btnPinNum[i], INPUT);
		pinMode(lightPinNum[i], OUTPUT);
		digitalWrite(lightPinNum[i], HIGH);
	}
	delay(1000);
	for(int i = 0; i < 4; ++i){
		digitalWrite(lightPinNum[i], LOW);
	}
}

int isPressed(void){
	if(digitalRead(BTN1)) return 0;
	else if(digitalRead(BTN2)) return 1;
	else if(digitalRead(BTN3)) return 2;
	else if(digitalRead(BTN4)) return 3;
	else return -1;
}

int playGame(int count){ //input : number of games that dog should win | output : if dog won, return true, else return false
	srand(time(NULL));
	int match_count = 10;
	int answer_count = 0;
	int next_btn = -1;
	while(match_count--){
		next_btn = rand()%4;
		//light on the button that dog should push
		digitalWrite(lightPinNum[next_btn], HIGH);

		while(1){
			int pushed_btn = isPressed();
			printf("pushed_btn num = %d\n", pushed_btn);
			//if any button pressed
			if(pushed_btn >= 0){
				if(next_btn == pushed_btn){
					answer_count++;
				}
				delay(1000);
				break;
			}
		}
	
		//light off after any button pushed
		digitalWrite(lightPinNum[next_btn], LOW);
		delay(500);
	}
	return (count <= answer_count);
}

int btn_game(void)
{
	//use physical pin
	if(wiringPiSetupPhys() == -1)
		return -1;
	
	init();

	if(playGame(7)) printf("DOG WIN\n");
	else printf("DOG LOSE\n");

	return 0;
}
