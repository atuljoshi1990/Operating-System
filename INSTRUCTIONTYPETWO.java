import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * INSTRUCTIONTYPETWO construct instructions of type two only.
 */
public class INSTRUCTIONTYPETWO {

 NUMBERCONVERSIONOPERATION operation = new NUMBERCONVERSIONOPERATION();
 static boolean READWRITEINDICATOR = false;
 static boolean JOBTERMINATED = false;
 MEMORY memory = new MEMORY();
 static int ioClockCount = 0;

 // This module processes type two instructions.
 public void processInstruction(PCB pcbObj) throws IOException {

  String errorCode = "";
  int readCounter = 0;
  int writeCounter = 0;
  List < String > inputData = new ArrayList < String > ();
  List < String > outputData = new ArrayList < String > ();
  // If more than one bit is set from bit 5-7 throw error.
  if ((1 == CPU.R3[5] && 1 == CPU.R3[6]) 
		  || (1 == CPU.R3[5] && 1 == CPU.R3[7]) 
		  || (1 == CPU.R3[6] && 1 == CPU.R3[7]) 
		  || (1 == CPU.R3[5] && 1 == CPU.R3[6] && 1 == CPU.R3[7])) {
   ERROR_HANDLER error = new ERROR_HANDLER();
   errorCode = "ERROR_001";
   error.ERROR_HANDLER(errorCode, pcbObj);
  } else if (1 == CPU.R3[5]) {
   // if the read bit is set
   if (0 == CPU.R3[4]) {
    CPU.R5 = memory.MEMORYMANAGER("READ",
     operation.DECIMALBIN(pcbObj.getInputAddress()), null);
    readCounter++;
    if (null != pcbObj.getInput()) {
     inputData = pcbObj.getInput();
     inputData.add(operation.BINHEX(CPU.R5));
     pcbObj.setInput(inputData);
    } else {
     inputData.add(operation.BINHEX(CPU.R5));
     pcbObj.setInput(inputData);
    }
   } else if (1 == CPU.R3[4]) {
    CPU.R4 = memory.MEMORYMANAGER("READ",
     operation.DECIMALBIN(pcbObj.getInputAddress()), null);
    readCounter++;
    if (null != pcbObj.getInput()) {
     inputData = pcbObj.getInput();
     inputData.add(operation.BINHEX(CPU.R4));
     pcbObj.setInput(inputData);
    } else {
     inputData.add(operation.BINHEX(CPU.R4));
     pcbObj.setInput(inputData);
    }
   }
   READWRITEINDICATOR = true;
   //Set I/O operation time to the PCB. 
   if (0 != pcbObj.getIoOperationExecutionTime()) {
    pcbObj.setIoOperationExecutionTime(
     pcbObj.getIoOperationExecutionTime() + 10);
   } else {
    pcbObj.setIoOperationExecutionTime(10);
   }
   pcbObj.setInputAddress(pcbObj.getInputAddress() + 1);
   //increment the I/O clock.
   ioClockCount = ioClockCount + 10;
   //Collect the number of read requests made for that job.
   if (0 != pcbObj.getNumberOfReadRequestsMade()) {
    readCounter = readCounter 
    		+ pcbObj.getNumberOfReadRequestsMade();
    pcbObj.setNumberOfReadRequestsMade(readCounter);
   } else {
    pcbObj.setNumberOfReadRequestsMade(readCounter);
   }
  } else if (1 == CPU.R3[6]) {
   // if the write bit is set
   String hexNum = "";
   if (0 == CPU.R3[4]) {
    hexNum = operation.BINHEX(CPU.R5);
   } else if (1 == CPU.R3[4]) {
    hexNum = operation.BINHEX(CPU.R4);
   }
   READWRITEINDICATOR = true;
   memory.MEMORYMANAGER("WRITE",
    operation.DECIMALBIN(pcbObj.getOutputAddress()),
    operation.HEXBIN(hexNum));
   writeCounter++;
   pcbObj.setOutputAddress(pcbObj.getOutputAddress() + 1);
   if (0 != pcbObj.getIoOperationExecutionTime()) {
    pcbObj.setIoOperationExecutionTime(
     pcbObj.getIoOperationExecutionTime() + 10);
   } else {
    pcbObj.setIoOperationExecutionTime(10);
   }
   if (null != pcbObj.getOutput()) {
    outputData = pcbObj.getOutput();
    outputData.add(hexNum);
    pcbObj.setOutput(outputData);
   } else {
    outputData.add(hexNum);
    pcbObj.setOutput(outputData);
   }
   ioClockCount = ioClockCount + 10;
   //Collect number of write requests made for that job.
   if (0 != pcbObj.getNumberOfWriteRequestsMade()) {
    writeCounter = writeCounter 
    		+ pcbObj.getNumberOfWriteRequestsMade();
    pcbObj.setNumberOfWriteRequestsMade(writeCounter);
   } else {
    pcbObj.setNumberOfWriteRequestsMade(writeCounter);
   }
  } else if (1 == CPU.R3[7]) {
   // if HLT bit is set.
   JOBTERMINATED = true;
   //Check if the number of input/output data 
   //lines is equal to the
   //number of read/write requests made.
   //If not throw a warning.
   if (0 != pcbObj.getNumberOfReadRequestsMade()) {
    if (pcbObj.getNumberOfReadRequestsMade() 
    		< pcbObj.getNumberOfDataLines()) {
     ERROR_HANDLER error = new ERROR_HANDLER();
     errorCode = "WARN_003";
     error.ERROR_HANDLER(errorCode, pcbObj);
    }
   }
   if (0 != pcbObj.getNumberOfWriteRequestsMade()) {
    if (pcbObj.getNumberOfWriteRequestsMade() 
    		< pcbObj.getNumberOfOutputLines()) {
     ERROR_HANDLER error = new ERROR_HANDLER();
     errorCode = "WARN_004";
     error.ERROR_HANDLER(errorCode, pcbObj);
    }
   }
   if (0 != pcbObj.getNumberOfWriteRequestsMade()) {
	    if (pcbObj.getNumberOfWriteRequestsMade() 
	    		> pcbObj.getNumberOfOutputLines()) {
	     ERROR_HANDLER error = new ERROR_HANDLER();
	     errorCode = "ERROR_005";
	     error.ERROR_HANDLER(errorCode, pcbObj);
	    }
	   }
  }
 }
}