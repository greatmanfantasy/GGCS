#"from picamera.array import PiRGBArray
#import RPi.GPIO as GPIO
#from picamera import PiCamera
import math # 타임 함수를 사용하기 위해 선언한 라이브러리
import cv2 # 오픈씨브이
import numpy as np # 모든 프레임을 쪼개어 각 픽셀당을 넘파이의 행렬로 표현
import sys
from datetime import datetime
import socket

SERVER_IP = "10.14.4.42"
PORT_NUM = 7000

#cap = cv2.VideoCapture("/home/ubu/Desktop/input.avi") # 기존에 영상을 가져오는 방식, 표준 입력은 0번으로 설정하면 캠이됨, 라즈베리파이 영상을 받아오는 방법도 이와 유사
cap = cv2.VideoCapture(0);
low_c = np.array([38,13,10]) # 색사의 범위를 다루는 공간 hsv의 값들은 이곳에 해당
high_c = np.array([80,100,250])
low_g = np.array([10,100,17])
high_g = np.array([20,255,100])
# 38 10 20 / 70 100 200
# for frame in camera.capture_continuous(rawCapture, format="bgr",use_video_port=True) : 
# test Video => for frame -> while(cap.isOpened()
now = datetime.now() # 날짜가 넘어갈때마다 초기화 시킬것 이기 떄문에 처음 날짜 선언
dog_bow_count = np.zeros((720,1280)) # 배변을 했는지 안했는지를 체크하기 위한 변수
print('bow_check_camera start day || %s-%s-%s' %(now.year, now.month, now.day)) # 년 월 일
frame_count = 1
while(cap.isOpened()): # 실시간 비디오 받아오는곳
    day_check = datetime.now() # 현재의 날짜 확인
    if(day_check.day == now.day): # 아직 오늘날짜면 그대로 수행하는것,
        ret,frame = cap.read()
        test = cv2.cvtColor(frame,cv2.COLOR_BGR2HSV) # hsv로 파악할 것 이기 때문에 test로 선언
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
#        check = test[150:600,150:800] # test영상에서 촬영할 범위값 설정 한 후 이곳만 감시할것
        check = test[0:-1,0:-1]
        cv2.imshow('check',check)
#        cv2.imshow('',RGB)
#        cv2.imshow('',blurred)
#        cv2.imshow('aa',check)
        imageWidth = check.shape[1] # 가로와 세로 , 픽셀들을 분석하기 위한 과정
        imageHeight = check.shape[0]
        flag = 1
        print("Analyzing " + str(frame_count) + "\'s frame")
        frame_count += 1
        for y in range(150,imageHeight,2): # range(a,b,c) 에서 c 과정은 픽셀의 건너뜀 값임 2칸씩 건너뛰면서 분석하겠다, 정확도가 감소하지만 영상의 분석속도는 빨라짐
            for x in range(120,imageWidth,2): # 기존 설정한 hsv의 범위값과 유사한지 확인
#                print('y',y,' -------- x ',x)
                q1,q2,q3 = check[y,x]
                if q1>=low_g[0] and q1 <= high_g[0]:
                    if q2>=low_g[1] and q2<=high_g[1]:
                        if q3>= low_g[2] and q3<=high_g[2]:
                            if dog_bow_count[y][x] == 0 :
                                dog_bow_count[y][x] = 111
                                cv2.rectangle(check,(x-10,y-10),(x+50,y+50),(0,255,0))
                                cv2.imshow('chec',check)
                                # 배변을 인식한 경우 서버에 연결 시도 및 "1" 전송
                                if flag :
                                    print()
                                    print("try to connect device")
                                    print()
                                    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                                    sock.connect((SERVER_IP, PORT_NUM))
                                    sock.send("1".encode())
                                    flag=0
                                # connect server , return value
                                print("x_cor : " + str(x) + " y_cor : " + str(y))
                                print("HSV value : (" + str(q1) + ", " + str(q2) + ", " + str(q3) +")")
                                print()
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
        dog_bow_count = np.zeros((720,1280)) # 날짜가 넘어가면 날짜 변경한후 기존 배변패드 체크값 초기화
        # need more reset data

cap.release()
cv2.destroyAllWindows()
