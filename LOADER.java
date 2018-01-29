import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * LOADER loads each user program to the main memory.
   LOADER uses a BUFFER to load each word to the main memory.
   BUFFER loads single word at a time to the main memory.
*/
public class LOADER {

 /*
  * Global variables : traceSwitch : store trace flag from the input file.
  * totalProgramLength : keeps track of the total program length for a job.
  * jobId : job id for a particular job. lineNumber : keeps track of the
  * current job. loadModuleEnd : true if all the jobs have been loaded to
  * memory. jobIdsList : keeps track of all the job ids. convert : used for
  * calling all the helper methods. memory : variable to access memory
  * object. progressFileMsg : variable used to write log message to progress
  * file. isDataLoadedToMemory : sets when the job data has been loaded to
  * memory. cpu : variable used to call write method of CPU to write the
  * progress of the jobs.
  */
 public static int totalProgramLength;
 public static int jobId = 0;
 public static int lineNumber = 0;
 public static boolean loadModuleEnd = false;
 static boolean isLoaderError = false;
 static List < Integer > jobIdsList = new ArrayList < Integer > ();
 NUMBERCONVERSIONOPERATION convert = new NUMBERCONVERSIONOPERATION();
 MEMORY memory = new MEMORY();
 String progressFileMsg = "";
 static boolean isDataLoadedToMemory = false;
 CPU cpu = new CPU();

 /*
  * LOADER Procedure loads each record 
  * to memory using BUFFER. This module
  * gets the input from file and convert 
  * the user program from hex to binary.
  */
 public void LOAD(String path) throws IOException {

  try {
   // Fetches every line from the file and put it in a list.
   List < String > lines = Files
    .readAllLines(Paths.get(path), StandardCharsets.UTF_8);
   // Takes the hex values from the list and covert those to binary
   // numbers.
   fetchInputFromListAndConvertToBinary(lines);
  } catch (Exception e) {}
 }

 /*
  * BUFFER module is responsible for loading 
  * each word to the main memory. 1
  * word at a time in this simulator. 
  * Buffer gives a call to MEMORY's
  * MEMORYBUFFERREGISTER to load each word to main memory.
  */
 public void BUFFER(String record, int startingAddress) {
  MEMORY memory = new MEMORY();
  String controlSignal = "WRITE";
  // call MEMORY's MEMORYBUFFERREGISTER to insert each instruction
  // to memory.
  memory.MEMORYMANAGER(controlSignal,
   convert.DECIMALBIN(startingAddress),
   convert.HEXBIN(record));
 }

 /*
  * This module gets a list of words. This is an extra utility used to
  * convert the hexadecimal input to binary inputs. This utility separates
  * the trace flag, starting address and program length from user program.
  */
 public void fetchInputFromListAndConvertToBinary(List < String > lines)
 throws IOException {

  int size = 3;
  int numberOfDataLines = 0;
  int numberOfOutputLines = 0;
  Iterator < String > it = lines.iterator();
  int startingAddress = 0;
  int tempStartingAddress = 0;
  int recordCount = 0;
  int userProgramLength = 0;
  String tempJobString = "";
  int lineCount = 0;
  int traceswitch = 0;
  boolean isInputData = false;
  int outputDataStartingAddress = 0;
  int inputDataStartingAddress = 0;
  int partitionBlockNum = 0;
  int counterIfPartitionsNotAvailable = 0;
  lineCount = lineNumber;
  lineNumber = 0;
  String userProgramRecordOriginal = "";
  String userProgramRecord = "";
  int partitionsize = 0;
  String address = "";
  String errorCode = "";
  int partitionStartingAddress = 0;
  int inputCount = 1;
  int unUsedWords = 0;
  boolean traceFlagMissing = false;
  boolean invalidTraceFlag = false;
  boolean loadNewProgram = false;
  boolean endModuleStart = false;
  boolean lastLineOfJob = false;
  boolean userProgramMissing = false;
  boolean nextDATA = false;
  boolean DATAMissing = false;
  boolean dataRecordsMissing = false;
  boolean dataPresent = false;
  boolean invalidRecord = false;
  boolean programLengthGreater = false;
  boolean traceFlagSet = false;
  boolean JOBpresent = false;
  CPU errorWriter = new CPU();
  while (it.hasNext()) {
   // Check if the ** END is the next line.
   if (endModuleStart) {
    lastLineOfJob = true;
   }
   // If the job has come again in the loader
   // after being removed from it because of
   // full partitions. Start it from where it left.
   if (lineNumber < lineCount) {
    lineNumber = lineNumber + 1;
    userProgramRecord = it.next().trim();
    continue;
   }
   lineNumber = lineNumber + 1;
   counterIfPartitionsNotAvailable++;
   // get the next record.
   userProgramRecordOriginal = it.next();
   userProgramRecord = userProgramRecordOriginal;
   // check If program length is greater then 128
   if (programLengthGreater 
		   && !userProgramRecord.contains("END")) {
    continue;
   } else if (programLengthGreater 
		   && userProgramRecord.contains("END")) {
    lastLineOfJob = true;
    recordCount = userProgramLength;
    lineNumber--;
   }
   // if a job is loaded to memory check for the next.
   if (loadNewProgram) {
    loadNewProgram = false;
    continue;
   }
   // check if the last job is loaded to memory.
   if (lineNumber == lines.size() - 1) {
    loadModuleEnd = true;
   }
   //check if ** JOB is present or not.
   if (userProgramRecord.contains("JOB")) {
    jobId = jobId + 1;
    JOBpresent = true;
    tempJobString = "JOB";
    String[] tempString = userProgramRecord.split(" ");
    try {
     numberOfDataLines = convert.HEXDECIMAL(tempString[2]);
     numberOfOutputLines = convert.HEXDECIMAL(tempString[3]);
     //Get total number of input and output lines.
     totalProgramLength = numberOfDataLines + numberOfOutputLines;
     File file = new File("trace_file_job_" + jobId + ".txt");
     // if file already exists then delete it.
     if (file.exists()) {
      file.delete();
     }
    } catch (Exception exc) {}
   } else if (tempJobString.contains("JOB")) {
    //Check if there is null job or not, if any
    //throw error.
    if (userProgramRecord.contains("END")) {
     loadNewProgram = true;
     PCB obj = new PCB();
     obj.setJobId(jobId);
     ERROR_HANDLER error = new ERROR_HANDLER();
     errorCode = "ERROR_010";
     obj.setDataLoadedToMemory(false);
     isLoaderError = true;
     jobIdsList.add(jobId);
     counterIfPartitionsNotAvailable = 0;
     error.ERROR_HANDLER(errorCode, obj);
     INSTRUCTIONTYPETWO.JOBTERMINATED = false;
     errorWriter.writeErrorToOutputFile(obj);
    }
    tempJobString = "";
    try {
     PCB tempPcbObj = new PCB();
     userProgramLength = convert.HEXDECIMAL(userProgramRecord);
     //check if the program length is greater then 128.
     if (userProgramLength > 128) {
      programLengthGreater = true;
     } else {
      totalProgramLength = totalProgramLength + userProgramLength;
      //allocate memory block to the job.
      address = memory
       .ALLOCATEMEMORYBLOCK(totalProgramLength,
        tempPcbObj);
      if ("999".equals(address)) {
       startingAddress = Integer.parseInt(address);
      } else {
       //write to progress file.
       progressFileMsg = "\n" 
       + "JOB " 
       + convert.DECIMALHEX(jobId) 
       + "'s" 
       + " (HEX) MEMORY ALLOCATION STARTED AT : " 
       + convert.DECIMALHEX(CPU.TIMECLOCK) 
       + "(HEX) CLOCK TIME.";
       cpu.updateProgressFile(progressFileMsg);
       partitionsize = tempPcbObj.getPartitionSize();
       //get the left space of the partition after a 
       //job is loaded in it.
       unUsedWords = partitionsize - totalProgramLength;
       progressFileMsg = "\n" 
       + "JOB " 
       + convert.DECIMALHEX(jobId) 
       + "'s" 
       + " (HEX) INTERNAL FRAGMENTATION : " 
       + Integer.toString(unUsedWords) 
       + "(DECIMAL).\n";
       cpu.updateProgressFile(progressFileMsg);
       //get the starting address for a job.
       startingAddress = Integer.parseInt(address.split(":")[0]);
       //set the starting address for writing the output
       //to memory.
       outputDataStartingAddress = startingAddress 
    		   + userProgramLength 
    		   + numberOfDataLines;
       //set the starting address for writing the input
       //to memory.
       inputDataStartingAddress = startingAddress 
    		   + userProgramLength;
       //set the partition number in the variable.
       partitionBlockNum = Integer
        .parseInt(address.split(":")[1]);
       tempPcbObj = null;
      }
      if (startingAddress == 999) {
       jobId = jobId - 1;
       lineNumber = lineNumber 
    		   - counterIfPartitionsNotAvailable;
       break;
      }
      tempStartingAddress = startingAddress;
      partitionStartingAddress = startingAddress;
     }
    } catch (Exception exc) {
     // handle invalid loader.
    }
   } else if (startingAddress != 999) {
    if (!JOBpresent) {
     recordCount = userProgramLength;
    } //If the loader has no errors then load data to
    //memory using BUFFER.
    //Else if there are errors in the loader, set flags for
    //that.
    if (recordCount != userProgramLength) {
     if (!programLengthGreater) {
      if (!userProgramRecord.contains("DATA")) {
       for (int start = 0; start 
    		   < userProgramRecord.length(); start += size) {
        String record = userProgramRecord
         .substring(start,
          Math.min(userProgramRecord.length(),
           start + size));
        //check if a record is valid hex num or not.
        boolean isHex = record.matches("[0-9a-fA-F]+");
        if (!isHex) {
         invalidRecord = true;
        }
        try {
         BUFFER(record, startingAddress);
        } catch (Exception e) {

        }
        startingAddress = startingAddress + 1;
        recordCount = recordCount + 1;
       }
      } else {
       userProgramMissing = true;
       isInputData = true;
       recordCount = userProgramLength;
      }
     }
     //If the user program is loaded to memory
     //Then add input data to memory and 
     //set the trace flag and the starting address
     //for the user program.
    } else if (recordCount == userProgramLength) {
     if (JOBpresent) {
      if (!userProgramRecord.contains("DATA") 
    		  && !userProgramRecord.contains("END")) {
       if (!traceFlagSet) {
        //if the user program is missing
        //set the error flag.
        if (!userProgramMissing) {
         String[] tempString = userProgramRecord.split(" ");
         tempStartingAddress = tempStartingAddress 
        		 + convert.HEXDECIMAL(tempString[0]);
         try {
          //If trace switch is missing/invalid 
          //set the warning flag for it.
          if (1 == tempString.length) {
           traceFlagMissing = true;
           if (!userProgramRecord.contains(" ")) {
            userProgramRecord = userProgramRecord 
            		+ " " 
            		+ "0";
           }
          } else if (!"0".equals(tempString[1]) 
        		  && !"1".equals(tempString[1])) {
           invalidTraceFlag = true;
          }
          traceswitch = Integer.parseInt(tempString[1]);
         } catch (Exception exc) {

         }
         //once the trace switch is set,
         //set the flag to check ** DATA record
         nextDATA = true;
         traceFlagSet = true;
        }
       }
      }
      //check if the ** DATA is present.
     } else if (userProgramRecord.contains("DATA")) {
      nextDATA = true;
     }
     //if input data is present load it to memory.
     if (isInputData) {
      if (!userProgramRecord.contains("END")) {
       if (!endModuleStart) {
        if (inputCount == numberOfDataLines) {
         endModuleStart = true;
         inputCount = 0;
        }
        String inputData = "";
        inputData = userProgramRecord;
        // Check whether the input record is 
        //valid hex num or not.
        boolean isHex = inputData.matches("[0-9a-fA-F]+");
        if (!isHex) {
         invalidRecord = true;
        }
        try {
         BUFFER(inputData, startingAddress);
        } catch (Exception exe) {

        }
        startingAddress = startingAddress + 1;
        inputCount++;
        dataPresent = true;
       }
      } else if (numberOfDataLines == 0) {
       lastLineOfJob = true;
       //if input records are missing set the 
       //error flag.
      } else if (numberOfDataLines > 0 
    		  && !dataPresent) {
       lastLineOfJob = true;
       dataRecordsMissing = true;
      }
     }
     //if ** DATA is missing set the error flag.
     if (nextDATA 
    		 && userProgramRecord.contains("DATA")) {
      isInputData = true;
      nextDATA = false;
     } else if (nextDATA &&
      !userProgramRecord.contains("DATA") 
      && userProgramRecord.contains("END")) {
      DATAMissing = true;
      lastLineOfJob = true;
     }
     //Check if ** END record has come or not.
     if (lastLineOfJob) {
      PCB pcbObj = new PCB();
      endModuleStart = false;
      lastLineOfJob = false;
      isInputData = false;
      dataPresent = false;
      traceFlagSet = false;
      lineCount = lineNumber;
      counterIfPartitionsNotAvailable = 0;
      //Throw error if ** JOB is not present.
      if (!JOBpresent) {
       ERROR_HANDLER error = new ERROR_HANDLER();
       errorCode = "ERROR_016";
       recordCount = 0;
       userProgramMissing = false;
       invalidRecord = false;
       isLoaderError = true;
       loadNewProgram = true;
       pcbObj.setDataLoadedToMemory(false);
       pcbObj.setJobId(jobId + 1);
       jobIdsList.add(jobId + 1);
       jobId = jobId + 1;
       error.ERROR_HANDLER(errorCode, pcbObj);
       errorWriter.writeErrorToOutputFile(pcbObj);
       INSTRUCTIONTYPETWO.JOBTERMINATED = false;
       //Throw error if program size is too large
      } else if (programLengthGreater) {
       pcbObj.setJobId(jobId);
       jobIdsList.add(jobId);
       programLengthGreater = false;
       JOBpresent = false;
       ERROR_HANDLER error = new ERROR_HANDLER();
       errorCode = "ERROR_007";
       recordCount = 0;
       isLoaderError = true;
       loadNewProgram = true;
       pcbObj.setDataLoadedToMemory(false);
       error.ERROR_HANDLER(errorCode, pcbObj);
       errorWriter.writeErrorToOutputFile(pcbObj);
       INSTRUCTIONTYPETWO.JOBTERMINATED = false;
      } else {
       //Set all the required parameters for the job
       //to PCB.
       JOBpresent = false;
       pcbObj.setJobId(jobId);
       jobIdsList.add(jobId);
       pcbObj.setCurrentProgramCounter(tempStartingAddress);
       pcbObj.setStartingAddress(tempStartingAddress);
       pcbObj.setTraceSwitch(traceswitch);
       pcbObj.setNumberOfDataLines(numberOfDataLines);
       pcbObj.setNumberOfOutputLines(numberOfOutputLines);
       pcbObj.setInputAddress(inputDataStartingAddress);
       pcbObj.setOutputAddress(outputDataStartingAddress);
       pcbObj.setPartitionBlockNum(partitionBlockNum);
       pcbObj.setTotalProgramLength(totalProgramLength);
       pcbObj.setUserProgramLength(userProgramLength);
       pcbObj.setPartitionSize(partitionsize);
       pcbObj.setUnusedWords(unUsedWords);
       pcbObj.setPartitionStartingAddress(partitionStartingAddress);
       //If the record is not hex throw error.
       if (invalidRecord) {
        invalidRecord = false;
        ERROR_HANDLER error = new ERROR_HANDLER();
        errorCode = "ERROR_015";
        recordCount = 0;
        isLoaderError = true;
        loadNewProgram = true;
        pcbObj.setDataLoadedToMemory(false);
        memory.DELLOCATEMEMORYBLOCK(pcbObj);
        error.ERROR_HANDLER(errorCode, pcbObj);
        errorWriter.writeErrorToOutputFile(pcbObj);
        progressFileMsg = "\nMEMORY DEALLOCATED FOR JOB " 
        + convert.DECIMALHEX(pcbObj.getJobId()) + "(HEX)";
        cpu.updateProgressFile(progressFileMsg);
        INSTRUCTIONTYPETWO.JOBTERMINATED = false;
        //if the input records are missing
        //throw error.
       } else if (dataRecordsMissing) {
        dataRecordsMissing = false;
        ERROR_HANDLER error = new ERROR_HANDLER();
        errorCode = "ERROR_014";
        recordCount = 0;
        isLoaderError = true;
        loadNewProgram = true;
        pcbObj.setDataLoadedToMemory(true);
        memory.DELLOCATEMEMORYBLOCK(pcbObj);
        error.ERROR_HANDLER(errorCode, pcbObj);
        errorWriter.writeErrorToOutputFile(pcbObj);
        progressFileMsg = "\nMEMORY DEALLOCATED FOR JOB " 
        + convert.DECIMALHEX(pcbObj.getJobId()) 
        + "(HEX)";
        cpu.updateProgressFile(progressFileMsg);
        INSTRUCTIONTYPETWO.JOBTERMINATED = false;
        //If ** DATA record is not present 
        //throw error.
       } else if (DATAMissing) {
        DATAMissing = false;
        ERROR_HANDLER error = new ERROR_HANDLER();
        errorCode = "ERROR_013";
        recordCount = 0;
        isLoaderError = true;
        loadNewProgram = true;
        pcbObj.setDataLoadedToMemory(true);
        memory.DELLOCATEMEMORYBLOCK(pcbObj);
        error.ERROR_HANDLER(errorCode, pcbObj);
        errorWriter.writeErrorToOutputFile(pcbObj);
        progressFileMsg = "\nMEMORY DEALLOCATED FOR JOB " 
        + convert.DECIMALHEX(pcbObj.getJobId()) + "(HEX)";
        cpu.updateProgressFile(progressFileMsg);
        INSTRUCTIONTYPETWO.JOBTERMINATED = false;
        //If user program is missing
        //throw error.
       } else if (userProgramMissing) {
        userProgramMissing = false;
        ERROR_HANDLER error = new ERROR_HANDLER();
        errorCode = "ERROR_012";
        recordCount = 0;
        isLoaderError = true;
        loadNewProgram = true;
        pcbObj.setDataLoadedToMemory(false);
        memory.DELLOCATEMEMORYBLOCK(pcbObj);
        error.ERROR_HANDLER(errorCode, pcbObj);
        errorWriter.writeErrorToOutputFile(pcbObj);
        progressFileMsg = "\nMEMORY DEALLOCATED FOR JOB " 
        + convert.DECIMALHEX(pcbObj.getJobId()) + "(HEX)";
        cpu.updateProgressFile(progressFileMsg);
        INSTRUCTIONTYPETWO.JOBTERMINATED = false;
       } else if (userProgramRecord.contains("END")) {
        recordCount = 0;
        loadNewProgram = true;
        pcbObj.setDataLoadedToMemory(true);
        isLoaderError = false;
        progressFileMsg = "\n" 
        + "INTERNAL FRAGMENTATION FOR JOB (" 
        +convert.DECIMALHEX(pcbObj.getJobId()) 
        		+ ") (HEX): " 
        		+ unUsedWords 
        		+ " (DECIMAL)";
        cpu.updateProgressFile(progressFileMsg);
        //If trace flag is missing/invalid, give a
        //message.
        if (traceFlagMissing) {
         ERROR_HANDLER error = new ERROR_HANDLER();
         errorCode = "WARN_001";
         error.ERROR_HANDLER(errorCode, pcbObj);
         traceFlagMissing = false;
        } else if (invalidTraceFlag) {
         ERROR_HANDLER error = new ERROR_HANDLER();
         errorCode = "WARN_002";
         error.ERROR_HANDLER(errorCode, pcbObj);
         invalidTraceFlag = false;
        }
        traceswitch = 0;
        progressFileMsg = "\n" 
        + "JOB " 
        + convert.DECIMALHEX(pcbObj.getJobId()) 
        + "(HEX) ADDED TO READY QUEUE FROM LOADER AT : " 
        + convert.DECIMALHEX(CPU.TIMECLOCK) 
        + "(HEX) CLOCK TIME.";
        cpu.updateProgressFile(progressFileMsg);
        SCHEDULER.READYQUEUE.add(pcbObj);
        CPU.SYSTEMTIMECLOCK++;
       } else {
        //If ** END is missing throw error.
        ERROR_HANDLER error = new ERROR_HANDLER();
        errorCode = "ERROR_011";
        recordCount = 0;
        isLoaderError = true;
        loadNewProgram = false;
        pcbObj.setDataLoadedToMemory(true);
        memory.DELLOCATEMEMORYBLOCK(pcbObj);
        error.ERROR_HANDLER(errorCode, pcbObj);
        errorWriter.writeErrorToOutputFile(pcbObj);
        progressFileMsg = "\nMEMORY DEALLOCATED FOR JOB " 
        + convert.DECIMALHEX(pcbObj.getJobId()) + "(HEX)";
        cpu.updateProgressFile(progressFileMsg);
        INSTRUCTIONTYPETWO.JOBTERMINATED = false;
       }
      }
     }
    }
   }
  }
 }
}