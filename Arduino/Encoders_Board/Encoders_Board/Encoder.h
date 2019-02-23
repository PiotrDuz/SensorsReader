// Encoder.h
#include <SPI.h>

#ifndef _ENCODER_h
#define _ENCODER_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

class EncoderClass
{
 protected:
	 static int PRECISION_POINTS ;
	 static SPISettings SPISettingsAS5145;
	 //chip select pin
	 int cS;
	 //revolutions counter, it is the saved state
	 long revolutions = 0;
	 //current measurement without revolutions
	 int measurement = 0;

 public:
	 //declare array of avalaible Chip Select pins
     //in correct order
	 static int CS_ARRAY[];
	 //how many available encoders? 
	 static const int ENCODERS_NUM = 2;

	 //call this method to initialize inputs
	static void EncoderClass::init();

	EncoderClass(int chipSel);
	EncoderClass::EncoderClass();

	void EncoderClass::setCs(int cs);
	void EncoderClass::read(bool isFirst);
	long EncoderClass::getReading();
	long EncoderClass::getRevolutionReading();
};

//extern Encoder Encoder;

#endif

