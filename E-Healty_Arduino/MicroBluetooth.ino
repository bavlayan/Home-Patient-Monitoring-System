#include <eHealth.h>
#include <eHealthDisplay.h>
#include <PinChangeInt.h>
#include <PinChangeIntConfig.h>

#define PATIENT_POSITION_SENSOR '1'
#define GLUCOMETER_SENSOR '2'
#define BODY_TEMPRATURE_SENSOR '3'
#define BLOOD_PRESSURE_SENSOR '4'
#define PULSE_AND_OXYGEN_IN_BLOOD_SENSOR '5'
#define AIRFLOW_SENSOR '6'
#define GALVANIC_SKIN_RESPONSE_SENSOR '7'
#define ELECTROCARDIOGRAM_SENSOR '8'
#define ELECTROMYOGRAPHY_SENSOR '9'

int pulseAndOxygenInBloodSensorCount = 0;
int CURRENT_SENSOR;

void setup() {
  Serial.begin(9600);
  eHealth.initPositionSensor();
  PCintPort::attachInterrupt(6, ReadPulsioximeter, RISING);
}

void loop() {
  if(Serial.available() > 0){    
    CURRENT_SENSOR = Serial.read(); 
    CURRENT_SENSOR = char(CURRENT_SENSOR);
  }
    switch(CURRENT_SENSOR){      
      case PATIENT_POSITION_SENSOR:
        PatientPositionSensor();
        break;        
      case GLUCOMETER_SENSOR:
        GlucometerSensor();
        break;        
      case BODY_TEMPRATURE_SENSOR:
        BodyTempratureSensor();
        break;        
      case BLOOD_PRESSURE_SENSOR:
        BloodPressureSensor();
        break;        
      case PULSE_AND_OXYGEN_IN_BLOOD_SENSOR:
        PulseAndOxygenInBloodSensor();
        break;        
      case AIRFLOW_SENSOR:
        AirFlowSensor();
        break;        
      case GALVANIC_SKIN_RESPONSE_SENSOR:
        GalvanicSkinResponseSensor();
        break;        
      case ELECTROCARDIOGRAM_SENSOR:
        ElectroCardiogrammSensor();
        break;        
      case ELECTROMYOGRAPHY_SENSOR:
        ElectromoGraphySensor();
        break;        
      default:
        break;
    }    
  delay(1000);
}

void PatientPositionSensor(){
  uint8_t position = eHealth.getBodyPosition();
  eHealth.printPosition(position);
  Serial.print("\n");
  delay(1000); // wait for a second.
}

void GlucometerSensor(){
  Serial.println("The sensor is not working..");
}

void BodyTempratureSensor(){
  float temperature = eHealth.getTemperature();
  Serial.print("C:");
  Serial.print(temperature, 2);
  Serial.println("");
  delay(1000); // wait for a second
}

void BloodPressureSensor(){
  Serial.println("The sensor is not working..");
}

void PulseAndOxygenInBloodSensor(){
  float BPM = eHealth.getBPM();
  float OxygenSaturation = eHealth.getOxygenSaturation();
  Serial.println("Bpm:" + String(BPM) + ",SPO2:" + String(OxygenSaturation));
  delay(500);
}

void AirFlowSensor(){
  int air = eHealth.getAirFlow();
  Serial.println("Air:" + String(air));
}

void GalvanicSkinResponseSensor(){
  float conductance = eHealth.getSkinConductance();
  float resistance = eHealth.getSkinResistance();
  float conductanceVol = eHealth.getSkinConductanceVoltage();
  Serial.println("C:" + String(conductance) + ",R:" + String(resistance) + ",CV:" + String(conductanceVol));
  delay(1000);
}

void ElectroCardiogrammSensor(){
  float ECG = eHealth.getECG();
  Serial.println("ECG:" + String(ECG, 2));
  delay(1); // wait for a millisecond
}

void ElectromoGraphySensor(){
  int EMG = eHealth.getEMG();
  Serial.println("EMG:" + String(EMG));
  delay(100);
}

void ReadPulsioximeter(){
  pulseAndOxygenInBloodSensorCount++;
  if (pulseAndOxygenInBloodSensorCount == 50) { //Get only of one 50 measures to reduce the latency
    eHealth.readPulsioximeter();
    pulseAndOxygenInBloodSensorCount = 0;
  }
}



