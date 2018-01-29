import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * ERROR_HANDLER is responsible for handling 
 * the errors/warnings of the simulation.
   Whenever an error is suspected a call 
   is given to the ERROR_HANDLER.
   ERROR_HANDLER identifies the type of error/warning 
   and writes the error message on the progress file.
*/
public class ERROR_HANDLER {

 MEMORY memory = new MEMORY();
 NUMBERCONVERSIONOPERATION operation = new NUMBERCONVERSIONOPERATION();
 // Map to store warning_code along with its 
 //warning_message to write to the
 // output file.
 Map < String, String > warningMap = new HashMap < String, String > ();
 // Map to store error_code along with 
 //its error_message to write to the
 // output file.
 Map < String, String > errorMap = new HashMap < String, String > ();
 static int totalAbnormalTerminations = 0;
 static int totalTimeUsedInAbnormalJobs = 0;
 static int totalTimeUsedInInfiniteLoopJobs = 0;
 static List < Integer > jobIdsWithInfiniteLoop = new ArrayList < Integer > ();
 static int jobId = 0;

 public void ERROR_HANDLER(String errorCode, PCB pcbObj) throws IOException {

  String errorMessage = "";
  // The following conditions are to identify 
  //what kind of error/warning
  // has occurred.
  // In every error condition the error code 
  //and error message is put to
  // an errorMap.
  // In every warning condition the warning 
  //code and warning message is
  // put to a warningMap.
  // Every time an error/warning is encountered, 
  //the error/warning code
  // and error/warning message
  // is passed to writeOutput method where the 
  //code and messages are written
  //to the output file.
  if ("ERROR_001".equals(errorCode)) {
   StringBuilder strBldr = new StringBuilder();
   for (int i = 0; i < CPU.R3.length; i++) {
    strBldr.append(CPU.R3[i]);
   }
   errorMessage = "Invalid instruction bits. (" 
   + strBldr.toString() + ")";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_002".equals(errorCode)) {
   errorMessage = "Suspected infinite loop.";
   totalTimeUsedInInfiniteLoopJobs =
    totalTimeUsedInInfiniteLoopJobs 
    + pcbObj.getTimeClockForExecutionTime() + 1;
   jobIdsWithInfiniteLoop.add(pcbObj.getJobId());
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_003".equals(errorCode)) {
   errorMessage = "Address out of range.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_004".equals(errorCode)) {
   errorMessage = "Illegal input.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_005".equals(errorCode)) {
   errorMessage = "Memory overflow.(Expected more "
   		+ "memory for output records)";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_006".equals(errorCode)) {
   StringBuilder strBldr = new StringBuilder();
   for (int i = 1; i < CPU.R3.length; i++) {
    strBldr.append(CPU.R3[i]);
   }
   errorMessage = "Invalid op-code. (" 
   + strBldr.toString() + ")";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_007".equals(errorCode)) {
   errorMessage = "Program size too large.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_008".equals(errorCode)) {
   errorMessage = "Invalid program length.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_010".equals(errorCode)) {
   errorMessage = "Null job.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_011".equals(errorCode)) {
   errorMessage = "Missing the ** END record.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_012".equals(errorCode)) {
   errorMessage = "Missing user program.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_013".equals(errorCode)) {
   errorMessage = "Missing ** DATA record.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_014".equals(errorCode)) {
   errorMessage = "Input data missing.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_015".equals(errorCode)) {
   errorMessage = "Invalid hex record.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  } else if ("ERROR_016".equals(errorCode)) {
   errorMessage = "** JOB record missing.";
   errorMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  }
  if ("WARN_001".equals(errorCode)) {
   errorMessage = "Trace Flag missing.";
   warningMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  }
  if ("WARN_002".equals(errorCode)) {
   errorMessage = "Bad trace flag.";
   warningMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  }
  if ("WARN_003".equals(errorCode)) {
   errorMessage = "Extra input data unused.";
   warningMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  }
  if ("WARN_004".equals(errorCode)) {
   errorMessage = "Allocated unnecessary"
   		+ " memory for output data.";
   warningMap.put(errorCode, errorMessage);
   writeOutput(errorCode, errorMessage, pcbObj);
  }
 }

 // This method writes the error/warning code 
 //and error/warning message to
 // the output.
 private void writeOutput(String errorCode, 
		 String errorMessage, PCB pcbObj)
 throws IOException {

   NUMBERCONVERSIONOPERATION convert = new NUMBERCONVERSIONOPERATION();
   CPU cpu = new CPU();
   String progressFileMsg = "";
  // if any warning is encountered program 
	 //executes normally with the
  // warning code and message on the output 
	 //screen and to the output file.
  if (errorCode.contains("WARN")) {
   if (null != pcbObj.getWarningMap()) {
    warningMap.putAll(pcbObj.getWarningMap());
    pcbObj.setWarningMap(warningMap);
   } else {
    pcbObj.setWarningMap(warningMap);
   }

  } else {
	  progressFileMsg = "\n" 
       + "ERROR OCCURRED FOR JOB (" 
       + convert.DECIMALHEX(pcbObj.getJobId()) 
       + ") " 
       + "(HEX) AT " 
       + (convert.BINDECIMAL(CPU.PC)-1) 
       + " PROGRAM COUNTER VALUE (DECIMAL).";
       cpu.updateProgressFile(progressFileMsg);
   // if error is encountered error code and message are written
   // to the progress file and the job is
   // terminated.
   pcbObj.setJobTerminatedAbnormally(true);
   //Set total abnormal terminations.
   totalAbnormalTerminations++;
   errorMap.put(errorCode, errorMessage);
   pcbObj.setErrorMap(errorMap);
   //Set execution time used in abnormal terminations.
   if (000001 != pcbObj.getTimeClockForExecutionTime()) {
    totalTimeUsedInAbnormalJobs =
     totalTimeUsedInAbnormalJobs 
     + pcbObj.getTimeClockForExecutionTime();
   }
   INSTRUCTIONTYPETWO.JOBTERMINATED = true;
  }
 }
}