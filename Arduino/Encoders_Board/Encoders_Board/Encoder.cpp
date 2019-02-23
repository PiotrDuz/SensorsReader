// 
// 
// 

#include "Encoder.h"

SPISettings EncoderClass::SPISettingsAS5145 = SPISettings(20000, MSBFIRST, SPI_MODE1);
int EncoderClass::PRECISION_POINTS = 4096;
int EncoderClass::CS_ARRAY[] = { 9, 8 };

EncoderClass::EncoderClass(int chipSel) {
	this->cS = chipSel;
}

EncoderClass::EncoderClass() {

}

void EncoderClass::init() {
	for (int i = 0; i < ENCODERS_NUM; i++) {
		pinMode(CS_ARRAY[i], OUTPUT);
		digitalWrite(CS_ARRAY[i], HIGH);
	}
}

void EncoderClass::setCs(int cs) {
	this->cS = cs;
}

void EncoderClass::read(bool isFirst) {

	SPI.beginTransaction(SPISettingsAS5145);
	digitalWrite(cS, LOW);

	unsigned int high = SPI.transfer(0x00);
	unsigned int low = SPI.transfer(0x00);

	digitalWrite(cS, HIGH);
	SPI.endTransaction();

	//measured values
	unsigned int highShifted = high << 4 ;
	unsigned int lowShifted = low >> 4;
	int measNow = highShifted | lowShifted;

	if (!isFirst) {
		if (this->measurement - measNow > 1000) {
			this->revolutions = this->revolutions + 1;
		}
		else if (measNow - this->measurement > 1000) {
			this->revolutions = this->revolutions - 1;
		}
	}

	this->measurement =  measNow;
}

long EncoderClass::getReading() {
	long reading = this->revolutions * PRECISION_POINTS  + this->measurement  + 0L;
	return reading;
}

long EncoderClass::getRevolutionReading() {
	return this->revolutions * PRECISION_POINTS;
}


