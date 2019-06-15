import time
import RPi.GPIO as gpio

trig = 13
echo = 19
def init():
    gpio.setmode(gpio.BCM)
    gpio.setwarnings(False)
    gpio.setup(trig,gpio.OUT)
    gpio.setup(echo,gpio.IN)

def sig():
    try:
        gpio.output(trig,False)
        time.sleep(0.5)
        gpio.output(trig,True)
        time.sleep(0.00001)
        gpio.output(trig,False)
        while gpio.input(echo) == 0 : pulse_start = time.time()
        while gpio.input(echo) == 1 : pulse_end = time.time()

        test_duration = pulse_end - pulse_start
        test_distance = test_duration * 17000
        test_distance = round(test_distance,2)
        return test_distance
    #    if test_distance <= 5 : print "stop" # stop method add 
       # else : print "test_distance : ",test_distance,"cm"
    except:
       gpio.cleanup()
