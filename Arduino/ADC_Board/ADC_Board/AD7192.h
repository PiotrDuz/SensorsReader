// AD7192.h
// This class NEEDS:
//  -- SPI.begin() initialized in main loop

#ifndef AD7192_H
#define AD7192_H

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

#include <SPI.h>

class AD7192
{
public:
	AD7192(byte CS, byte RDYpin);							//initialize SPISettings, set pinmode for chipselect pins

	void Initialize();										//reset device, set to defaults
	void StartConversion();									//Power GND bridge, set mode to ContinuousRead	
	void EndConversion();									//Power off GND bridge, set mode to normal ContinuousConversion

	byte ReadContinuous(byte arr[]);						//Read conversion when it is available, ONLY TO USE with ContinuousRead
	byte ReadSingle(byte arr[]);							//Read one single conversion, GND has to be manually connected

	void ReadModeRegister(byte arr[]);						
	void WriteModeRegister(byte arr[]);
	void ReadConfigurationRegister(byte arr[]);
	void WriteConfigurationRegister(byte arr[]);

	void SetFSWord(int FSword);								//provide FSWord in int decimal format , MAX =1023
	void SetPolarity(byte flag);							//Set converison polarity: unipolar = 1, bipolar = 0
	void SetGain(byte Gain);								//Set Gain, provide in byte decimal format , only values: gain = 1,8,16,32,64,128

	void ReadOffset(byte arr[]);							//Read Offset coeafficent value 
	void ReadScale(byte arr[]);								//Read Scale coefficent value

	void InternalZeroScaleCalibration();					//Calibrate device, zore-inpu voltage will be conected to pins and offset coeff set
	void InternalFullScaleCalibration();					//MAX voltage will be connected and Scale coeff set

	byte GetCS();											//Get ChipSelect pin number

private:
	void ReadRegister(byte arr[], byte RegisterByte);
	void WriteRegister(byte arr[], byte RegisterByte);
	void Calibration(byte CalibrationByte);

	byte ChipSelect;
	SPISettings SPIsettingAD7192;
	byte RDY;
};


#endif

