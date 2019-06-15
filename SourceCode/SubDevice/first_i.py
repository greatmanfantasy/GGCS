#from picamera.array import PiRGBArray
#import RPi.GPIO as GPIO
#from picamera import PiCamera
import math
import cv2
import numpy as np
import sys
from datetime import datetime
cap = cv2.VideoCapture("/home/jaeil/vision/project/one.mp4")
low_c = np.array([38,13,10])
high_c = np.array([80,100,250])
low_g = np.array([10,100,17])
high_g = np.array([20,255,100])
# 38 10 20 / 70 100 200
# for frame in camera.capture_continuous(rawCapture, format="bgr",use_video_port=True) : 
# test Video => for frame -> while(cap.isOpened()
now = datetime.now()
dog_bow_count = np.zeros((720,1280))
print('bow_check_camera start day || %s-%s-%s' %(now.year, now.month, now.day))
while(cap.isOpened()):
    day_check = datetime.now()
    if(day_check.day == now.day):
        ret,frame = cap.read()
        test = cv2.cvtColor(frame,cv2.COLOR_BGR2HSV)
        RGB = cv2.cvtColor(frame,cv2.COLOR_BGR2GRAY)
	blurred = cv2.GaussianBlur(test,(5,5,),0)
        # array check = np.zeros((780,1280),1)
        # need check color average
        '''
        print(test.shape[0])
        print("--------------------------")
        print(test.shape[1])
        print("--------------------------")
        print(test.shape[2])
        print('=================================')
        '''
        check = test[150:600,150:800]
        #cv2.imshow('check',check)
        #cv2.imshow('',RGB)
	#cv2.imshow('',blurred)
        cv2.imshow('aa',check)
        imageWidth = check.shape[1]
        imageHeight = check.shape[0]
        for y in range(150,imageHeight,2):
            for x in range(120,imageWidth,2):
                q1,q2,q3 = check[y,x]
                if q1>=low_c[0] and q1 <= high_c[0]:
                    if q2>=low_c[1] and q2<=high_c[1]:
                        if q3>= low_c[2] and q3<=high_c[2]:
                            if dog_bow_count[y][x] == 0 :
                                dog_bow_count[y][x] = 111
                                cv2.rectangle(check,(x-10,y-10),(x+50,y+50),(0,255,0))
                                print('y',y,' -------- x ',x)
                                cv2.imshow('chec',check)
                                # connect server , return value
                            else:
                                continue
		'''
	        if q1>=low_b[0] and q1 <= high_b[0]:
		    if q2>=low_b[1] and q2 <=high_b[1]:
		        if q3>= low_b[2] and q3<=high_b[2]:
			    if dog_bow_count[y][x] == 0 :
			        dog_bow_count[y][x] == 111
			        cv2.rectangle(check,(x-10,y-10),(x+50,y+50),(0,255,0))
			        cv2.imshow('chec',check)
		'''
        if ret:
            cv2.imshow('video',frame)
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
        else:
            break
    else:
        dog_bow_count = np.zeros((720,1280))
        # need more reset data

capture.release()
cv2.destroyAllWindows()
