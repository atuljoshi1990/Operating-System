import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

/*
 * CPU is the main execution unit.
   CPU runs an infinite loop to execute 
   instructions until a HLT instruction 
   or an I/O instruction 
   is interpreted. 
   CPU creates a trace_file if the trace 
   flag is switched on.
   CPU constructs an instruction by giving a call 
   to respective instruction type class.
*/
public class CPU {

 /*
  * Global variables : 
  * R0 - R9 : Registers R0 to R9. 
  * TIMECLOCK : Virtual time clock. 
  * registerType, 
  * effectiveAddressBeforeExecution, 
  * instructionTypeOne,
  * effectiveAddressAfterExecution, 
  * registerBeforeExecution programCounter,
  * programCounter, 
  * traceSwitch : These variables are used 
  * while generating
  * the trace_file. 
  * PC : Program counter to store the 
  * starting address and
  * keep updating it with the execution progress.
  */
 public static int[] R0 = new int[12];
 public static int[] R1 = new int[12];
 public static int[] R2 = new int[12];
 public static int[] R3 = new int[12];
 public static int[] R4 = new int[12];
 public static int[] R5 = new int[12];
 public static int[] R6 = new int[12];
 public static int[] R7 = new int[12];
 public static int[] R8 = new int[12];
 public static int[] R9 = new int[12];
 public static int TIMECLOCK = 0;
 public static int SYSTEMTIMECLOCK = 0;
 public static int waitingTime = 0;
 static int registerType = 0;
 static int[] effectiveAddressBeforeExecution;
 static int[] effectiveAddressAfterExecution;
 public static int[] registerBeforeExecution = new int[12];
 public static int[] registerAfterExecution = new int[12];
 public static int[] programCounter = new int[12];
 public static int[] traceSwitch = new int[12];
 NUMBERCONVERSIONOPERATION operation = new NUMBERCONVERSIONOPERATION();
 static int[] PC = new int[12];
 String progressFileMsg = "";
 static boolean instructionTypeOne = false;
 MEMORY memory = new MEMORY();

 // CPU procedure is responsible for the execution of the instructions
 // indefinitely.
 // It calls writeHeaderValuesToTraceFile & writeToTraceFile modules to
 // develop the trace_file
 // CPU has methods like writeToOutputFile and writeErrorToOutputFile
 // etc.
 public String CPU(PCB pcbObj) throws IOException {

  int ioClock = 10;
  int timeClockForPcb = 0;
  int timeClockForExecutionTime = 0;
  String errorCode = "";
  MEMORY memory = new MEMORY();
  int[] binaryOne = {0,0,0,0,0,0,0,0,0,0,0,1};
  if (false == pcbObj.isJobinitiated()) {
   pcbObj.setJobinitiated(true);
   progressFileMsg = "\n" 
   + "JOB " 
   + operation.DECIMALHEX(pcbObj.getJobId()) 
   + "(HEX) INITIATED AT : " 
   + operation.DECIMALHEX(TIMECLOCK) 
   + "(HEX) CLOCK TIME.";
   updateProgressFile(progressFileMsg);
  }
  CPU.PC = operation.DECIMALBIN(pcbObj.getCurrentProgramCounter());
  traceSwitch = operation.DECIMALBIN(pcbObj.getTraceSwitch());
  if (!pcbObj.isJobInTrack()) {
   pcbObj.setTimeJobEnteredTheSystem(CPU.TIMECLOCK);
   pcbObj.setJobInTrack(true);
   if (1 == operation.BINDECIMAL(traceSwitch)) {
    writeHeaderValuesToTraceFile(pcbObj);
   }
  }
  if (null != pcbObj.getR4()) {
   R4 = pcbObj.getR4();
  }
  if (null != pcbObj.getR5()) {
   R5 = pcbObj.getR5();
  }
  if (null != pcbObj.getR6()) {
   R6 = pcbObj.getR6();
  }
  // indefinite loop.
  while (true) {
   // check whether the trace switch is on or not.
   if (0 == operation.BINDECIMAL(traceSwitch)) {
    // get the instruction from main memory.
    R3 = memory.MEMORYADDRESSREGISTER(CPU.PC);
    // increment program counter.
    PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    // identify the type of instruction and execute it.
    if (0 != pcbObj.getTimeClock()) {
     timeClockForPcb = pcbObj.getTimeClock();
    }
    // Check if the job has used its quantum value
    // and return to scheduler if so.
    if (timeClockForPcb == 35) {
     pcbObj.setTimeClock(0);
     return "extraQuantumNeeded";
    }
    executeInstruction(pcbObj);
    TIMECLOCK++;
    SYSTEMTIMECLOCK++;
    // set the PCB object for a particular job.
    pcbObj.setCurrentProgramCounter(operation.BINDECIMAL(PC));
    pcbObj.setR4(R4);
    pcbObj.setR5(R5);
    pcbObj.setR6(R6);
    if (0 != pcbObj.getTimeClockForExecutionTime()) {
     timeClockForExecutionTime = pcbObj.getTimeClockForExecutionTime();
    }
    timeClockForExecutionTime++;
    //Check for suspected infinite loop.
    if (timeClockForExecutionTime > 100000) {
     errorCode = "ERROR_002";
     ERROR_HANDLER error = new ERROR_HANDLER();
     error.ERROR_HANDLER(errorCode, pcbObj);
    }
    pcbObj.setTimeClockForExecutionTime(timeClockForExecutionTime);
    // method to do clock operations.
    timeClockForPcb = CLOCKOPERATIONS(timeClockForPcb);
    pcbObj.setTimeClock(timeClockForPcb);
    //Check if I/O operation is done and return
    //to scheduler if so.
    if (INSTRUCTIONTYPETWO.READWRITEINDICATOR) {
     progressFileMsg = "\n" 
     + "JOB " 
     + operation.DECIMALHEX(pcbObj.getJobId())  
     + "(HEX) REQUESTED I/O OPERATION AT : " 
     + operation.DECIMALHEX(TIMECLOCK) 
     + "(HEX) CLOCK TIME.";
     //update the progress file.
     updateProgressFile(progressFileMsg);
     pcbObj.setIoClock(ioClock);
     pcbObj.setTimeClock(timeClockForPcb);
     INSTRUCTIONTYPETWO.READWRITEINDICATOR = false;
     return "readWriteOperationOn";
    }
    //Check whether the job is terminated or not.
    //if terminated return the job to the scheduler.
    if (INSTRUCTIONTYPETWO.JOBTERMINATED) {
     INSTRUCTIONTYPETWO.JOBTERMINATED = false;
     progressFileMsg = "\n" 
     + "JOB " 
     + operation.DECIMALHEX(pcbObj.getJobId()) 
     + "(HEX) TERMINATED AT : " 
     + operation.DECIMALHEX(TIMECLOCK) 
     + "(HEX) CLOCK TIME.";
     updateProgressFile(progressFileMsg);
     return "jobTerminated";
    }
    // if the trace bit is on.
   } else if (1 == operation.BINDECIMAL(traceSwitch)) {
    // get the instruction from main memory.
    CPU.R3 = memory.MEMORYADDRESSREGISTER(CPU.PC);
    // get the type of register on which 
    //the operation is to be
    // performed.
    registerType = CPU.R3[4];
    String OPCODETYPE2 = "110";
    String OPCODETYPE3AND4 = "111";
    StringBuilder strNum = new StringBuilder();
    for (int i = 1; i < 4; i++) {
     strNum.append(CPU.R3[i]);
    }
    String currentOpcode = strNum.toString();
    // Get the effective address before execution only if the
    // instruction type one is being processed.
    if (!OPCODETYPE2.equals(currentOpcode) 
    		&& !OPCODETYPE3AND4.equals(currentOpcode)) {
     effectiveAddressBeforeExecution = INSTRUCTIONTYPEONE.EA;
     // set instruction type one true.
     instructionTypeOne = true;
    } else {
     // if not type one set it false.
     instructionTypeOne = false;
    }
    // get the value of instruction before execution.
    if (0 == registerType) {
     registerBeforeExecution = CPU.R5;
    } else {
     registerBeforeExecution = CPU.R4;
    }
    // get program before before execution.
    programCounter = CPU.PC;
    // increment the program counter.
    CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    // identify the type of instruction and execute it.
    if (0 != pcbObj.getTimeClock()) {
     timeClockForPcb = pcbObj.getTimeClock();
    }
    if (timeClockForPcb == 35) {
     pcbObj.setTimeClock(0);
     return "extraQuantumNeeded";
    }
    executeInstruction(pcbObj);
    TIMECLOCK++;
    pcbObj.setCurrentProgramCounter(operation.BINDECIMAL(PC));
    pcbObj.setR4(R4);
    pcbObj.setR5(R5);
    pcbObj.setR6(R6);
    if (0 != pcbObj.getTimeClockForExecutionTime()) {
     timeClockForExecutionTime = pcbObj.getTimeClockForExecutionTime();
    }
    timeClockForExecutionTime++;
    if (timeClockForExecutionTime > 100000) {
     errorCode = "ERROR_002";
     ERROR_HANDLER error = new ERROR_HANDLER();
     error.ERROR_HANDLER(errorCode, pcbObj);
    }
    pcbObj.setTimeClockForExecutionTime(timeClockForExecutionTime);
    // method to do clock operations.
    timeClockForPcb = CLOCKOPERATIONS(timeClockForPcb);
    pcbObj.setTimeClock(timeClockForPcb);
    // Get the effective address after execution only if the
    // instruction type one is being processed.
    if (!OPCODETYPE2.equals(currentOpcode) 
    		&& !OPCODETYPE3AND4.equals(currentOpcode)) {
     effectiveAddressAfterExecution = INSTRUCTIONTYPEONE.EA;
     // set instruction type one true.
     instructionTypeOne = true;
    } else {
     // if not type one set it false.
     instructionTypeOne = false;
    }
    // get the value of instruction after execution.
    if (0 == registerType) {
     registerAfterExecution = CPU.R5;
    } else {
     registerAfterExecution = CPU.R4;
    }
    // write the above collected information to the trace_file
    writeToTraceFile(pcbObj);
    if (INSTRUCTIONTYPETWO.READWRITEINDICATOR) {
     progressFileMsg = "\n" 
     + "JOB " 
     + operation.DECIMALHEX(pcbObj.getJobId()) 
     + "(HEX) REQUESTED I/O OPERATION AT : " 
     + Integer.toString(TIMECLOCK) 
     + "(HEX) CLOCK TIME.";
     updateProgressFile(progressFileMsg);
     pcbObj.setIoClock(ioClock);
     pcbObj.setTimeClock(timeClockForPcb);
     INSTRUCTIONTYPETWO.READWRITEINDICATOR = false;
     return "readWriteOperationOn";
    }
    if (INSTRUCTIONTYPETWO.JOBTERMINATED) {
     progressFileMsg = "\n" 
     + "JOB " 
     + operation.DECIMALHEX(pcbObj.getJobId()) 
     + "(HEX) TERMINATED AT : " 
     + operation.DECIMALHEX(TIMECLOCK) 
     + "(HEX) CLOCK TIME.";
     updateProgressFile(progressFileMsg);
     INSTRUCTIONTYPETWO.JOBTERMINATED = false;
     return "jobTerminated";
    }
   }
  }
 }

 //This method performs the logging activity for the simulator.
 public void updateProgressFile(String msg) throws IOException {
  PrintWriter out;
  out = new PrintWriter(new FileWriter("progress_file.txt", true), true);
  out.write(msg);
  out.close();
 }

 //All the clock operations are performed in this method.
 //If a job is in blocked state the I/O timer is decreased here.
 public int CLOCKOPERATIONS(int timeClockForPcb) throws IOException {

  timeClockForPcb++;
  SCHEDULER scheduler = new SCHEDULER();
  if (!SCHEDULER.BLOCKEDLIST.isEmpty()) {
   for (int i = 0; i < SCHEDULER.BLOCKEDLIST.size(); i++) {
    PCB obj = SCHEDULER.BLOCKEDLIST.get(i);
    if (0 == obj.getIoClock() - 1) {
     int timeQuantumRemainder = 35 - obj.getTimeClock();
     if ((timeQuantumRemainder > 0 
    		 && timeQuantumRemainder < 6) 
    		 || timeQuantumRemainder == 0) {
      //Once I/O operation is finished
      //the job is added to the ready queue.
      scheduler.ADDTOREADYQUEUE(6, obj);
     } else if (timeQuantumRemainder > 5 
    		 && timeQuantumRemainder < 11) {
      scheduler.ADDTOREADYQUEUE(5, obj);
     } else if (timeQuantumRemainder > 10 
    		 && timeQuantumRemainder < 16) {
      scheduler.ADDTOREADYQUEUE(4, obj);
     } else if (timeQuantumRemainder > 15 
    		 && timeQuantumRemainder < 21) {
      scheduler.ADDTOREADYQUEUE(3, obj);
     } else if (timeQuantumRemainder > 20 
    		 && timeQuantumRemainder < 26) {
      scheduler.ADDTOREADYQUEUE(2, obj);
     } else if (timeQuantumRemainder > 25 
    		 && timeQuantumRemainder < 31) {
      scheduler.ADDTOREADYQUEUE(1, obj);
     } else if (timeQuantumRemainder > 30 
    		 && timeQuantumRemainder < 36) {
      scheduler.ADDTOREADYQUEUE(0, obj);
     }
     progressFileMsg = "\n" 
     + "JOB " + operation.DECIMALHEX(obj.getJobId()) 
     + "(HEX) REMOVED FROM BLOCKED LIST AT : " 
     + operation.DECIMALHEX(TIMECLOCK) 
     + "(HEX) CLOCK TIME.";
     updateProgressFile(progressFileMsg);
     SCHEDULER.BLOCKEDLIST.remove(i);
    } else {
     obj.setIoClock(obj.getIoClock() - 1);
     CPU.SYSTEMTIMECLOCK++;
     SCHEDULER.BLOCKEDLIST.set(i, obj);
    }
   }
  }
  return timeClockForPcb;
 }

 // This module creates a trace file if the trace bit is on and writes the
 // header values in proper format.
 private void writeHeaderValuesToTraceFile(PCB pcbObj) {

  PrintWriter out;
  try {
   String jobId = Integer.toString(pcbObj.getJobId());
   // create a new printWriter object with trace_file.txt
   out = new PrintWriter(new FileWriter("trace_file_job_" 
   + jobId + ".txt", true), true);
   // write the header names to the trace file separated by tab
   // delimiter.
   out.write("PC" + "\t" + "INSTR" 
   + "\t" + "R&EA" + "\t" + "\t" 
   + "R&EA(Before EXE)" 
   + "\t" + "R&EA(After EXE)");
   out.write("\n" + "HEX" + "\t" + "HEX" 
   + "\t" + "  " + "  " + "HEX" 
   + "\t" + "\t" + "HEX" + "  " 
   + "HEX" + "\t" + "\t" 
   + "HEX" + "  " + "HEX");
   out.write("\n" + "------------------"
   		+ "----------------" + "-------------------"
   				+ "------------------");
   out.close();
  } catch (IOException e) {
   e.printStackTrace();
  }
 }

 // This module writes the values collected 
 //in CPU module to the trace file.
 public void writeToTraceFile(PCB pcbObj) {
  // The values which we get in this module 
  //are in binary format. Those
  // values are then converted to
  // hexadecimal format using BINHEX method of the
  // NUMBERCONVERSIONOPERATION class.
  String registerType = "";
  String effectiveAddressAfterExecution = "";
  String EAContentsBeforeExecution = "";
  String EAContentsAfterExecution = "";
  String registerAfterExecution = operation.
  BINHEX(CPU.registerAfterExecution);
  String registerBeforeExecution = operation.
  BINHEX(CPU.registerBeforeExecution);
  String instruction = operation.BINHEX(R3);
  String programCounter = operation.
  BINHEX(CPU.programCounter);
  String jobId = Integer.toString(pcbObj.getJobId());
  MEMORY memory = new MEMORY();
  // if the instruction type is one then only print effective addresses
  // else print "---".
  if (instructionTypeOne) {
   effectiveAddressAfterExecution = operation.
   BINHEX(CPU.effectiveAddressAfterExecution);
   EAContentsBeforeExecution = operation.BINHEX(CPU.R6);
   EAContentsAfterExecution = operation
    .BINHEX(memory.MEMORYMANAGER("READ",
     CPU.effectiveAddressAfterExecution, null));
  } else {
   effectiveAddressAfterExecution = "---";
   EAContentsBeforeExecution = "---";
   EAContentsAfterExecution = "---";
  }
  // set the type of register.
  if (0 == CPU.registerType) {
   registerType = "R5";
  } else {
   registerType = "R4";
  }
  PrintWriter out;
  try {
   out = new PrintWriter(new FileWriter("trace_file_job_" 
   + jobId + ".txt", true), true);
   // write all the collected values separated by tab delimiter.
   out.write("\n" + programCounter 
		   + "\t" + instruction + "\t" 
		   + registerType + "  " 
		   + effectiveAddressAfterExecution 
		   + "\t" + "\t" + registerBeforeExecution 
		   + "  " + EAContentsBeforeExecution 
		   + "\t" + "\t" + registerAfterExecution 
		   + "  " + EAContentsAfterExecution);
   out.close();
  } catch (IOException e) {
   e.printStackTrace();
  }
 }

 // This module identifies the type of instruction 
 //being executed currently
 // and calls respective
 // INSTRUCTIONTYPE class.
 public void executeInstruction(PCB pcbObj) throws IOException {

  String OPCODETYPE2 = "110";
  String OPCODETYPE3AND4 = "111";
  StringBuilder strNum = new StringBuilder();
  for (int i = 1; i < 4; i++) {
   strNum.append(CPU.R3[i]);
  }
  String currentOpcode = strNum.toString();
  // By Default if instruction type is not 2 or 3 or 4 type one is
  // executed.
  if (!OPCODETYPE2.equals(currentOpcode) 
		  && !OPCODETYPE3AND4.equals(currentOpcode)) {
   INSTRUCTIONTYPEONE typeOne = new INSTRUCTIONTYPEONE();
   // call processInstruction method to execute the instruction.
   typeOne.processInstruction(currentOpcode, pcbObj);
  } else if (OPCODETYPE2.equals(currentOpcode)) {
   INSTRUCTIONTYPETWO typeTwo = new INSTRUCTIONTYPETWO();
   // call processInstruction method to execute the instruction.
   typeTwo.processInstruction(pcbObj);
  } else if (OPCODETYPE3AND4.equals(currentOpcode)) {
   // if the opcode is 111 check whether it is type 3 or 4 by checking
   // the index bit.
   if (0 == CPU.R3[0]) {
    INSTRUCTIONTYPETHREE typeThree = new INSTRUCTIONTYPETHREE();
    // call processInstruction method to execute the instruction.
    typeThree.processInstruction(pcbObj);
   } else if (1 == CPU.R3[0]) {
    INSTRUCTIONTYPEFOUR typeFour = new INSTRUCTIONTYPEFOUR();
    // call processInstruction method to execute the instruction.
    typeFour.processInstruction(pcbObj);
   }
  }
 }

 // This module write the output to an output file.
 public void writeToOutputFile(PCB pcbObj) throws IOException {

  PrintWriter out;
  int count = 1;
  out = new PrintWriter(new FileWriter(
   "progress_file.txt", true), true);
  out.write("\n");
  out.write("\n" 
  + Integer.toString(count) 
  + ". JOB NUMBER (HEX): " 
  + operation.DECIMALHEX(pcbObj.getJobId()));
  count++;
  // if any warning are there write them to the output file.
  if (null != pcbObj.getWarningMap() 
		  && !pcbObj.getWarningMap().isEmpty()) {
   Set < String > keys = pcbObj.getWarningMap().keySet();
   out.write("\n" + Integer.toString(count) + ". WARNING : ");
   for (Iterator < String > i = keys.iterator(); i.hasNext();) {
    String key = i.next();
    String value = pcbObj.getWarningMap().get(key);
    out.write("\n" + "\t" + "WARN CODE : " + key);
    out.write("\n" + "\t" + "WARN DESCRIPTION : " + value);
   }
   count++;
  }
  out.write("\n" + Integer.toString(count) 
  + ". NORMAL TERMINATION");
  count++;
  out.write("\n" + Integer.toString(count) 
  + ". PART OF THE PARTITION OCCUPIED " 
		  + "(INDEX IN HEX, DATA IN BINARY):");
  out.write("\n");
  DUMP(pcbObj, out);
  count++;
  if (pcbObj.getInput() != null) {
   out.write(Integer.toString(count) + ". INPUT (HEX): ");
   // write input values to the output file.
   for (String strng: pcbObj.getInput()) {
    out.write(strng + " ");
   }
   count++;
  }
  out.write("\n" + Integer.toString(count) + ". OUTPUT (HEX): ");
  // write input values to the output file.
  for (String strng: pcbObj.getOutput()) {
   out.write(strng + " ");
  }
  count++;
  // time clock value in hex.
  out.write("\n" + Integer.toString(count) 
  + ". PARTITION NUMBER (DECIMAL): " 
		  + pcbObj.getPartitionBlockNum() 
		  + ", SIZE OCCUPIED (DECIMAL): " 
		  + pcbObj.getTotalProgramLength());
  count++;
  out.write("\n" + Integer.toString(count) 
  + ". TIME THE JOB ENTERED THE SYSTEM (HEX): " 
		  + operation.DECIMALHEX(
   pcbObj.getTimeJobEnteredTheSystem()));
  count++;
  out.write("\n" + Integer.toString(count) 
  + ". TIME THE JOB LEFT THE SYSTEM (HEX): " 
		  + operation.DECIMALHEX(CPU.TIMECLOCK));
  count++;
  // execution time in decimal.
  out.write("\n" + Integer.toString(count) 
  + ". EXECUTION TIME (DECIMAL): " 
		  + pcbObj.getTimeClockForExecutionTime());
  count++;
  out.write("\n" + Integer.toString(count) 
  + ". TIME SPENT DOING I/O (DECIMAL): " 
		  + pcbObj.getIoOperationExecutionTime());
  count++;
  // I/O time in decimal.
  out.write("\n" + Integer.toString(count) 
  + ". TOTAL RUN TIME (DECIMAL): " 
		  + (pcbObj.getTimeClockForExecutionTime() 
				  + pcbObj.getIoOperationExecutionTime()));
  out.write("\n");
  out.write("\n");
  out.close();
 }

 // This module write the error output to an output file.
 public void writeErrorToOutputFile(PCB pcbObj) throws IOException {

  PrintWriter out;
  int count = 1;
  out = new PrintWriter(new FileWriter("progress_file.txt"
		  , true), true);
  out.write("\n" + Integer.toString(count) 
  + ". JOB NUMBER (HEX): " 
  + operation.DECIMALHEX(pcbObj.getJobId()));
  count++;
  // if any warning are there write them to the output file.
  if (null != pcbObj.getWarningMap() 
		  && !pcbObj.getWarningMap().isEmpty()) {
   Set < String > keys = pcbObj.getWarningMap().keySet();
   out.write("\n" + Integer.toString(count) + ". WARNING : ");
   for (Iterator < String > i = keys.iterator(); i.hasNext();) {
    String key = i.next();
    String value = pcbObj.getWarningMap().get(key);
    out.write("\n" + "\t" + "WARN CODE : " + key);
    out.write("\n" + "\t" + "WARN DESCRIPTION : " + value);
   }
   count++;
  }
  out.write("\n" + Integer.toString(count) 
  + ". ABNORMAL TERMINATION");
  count++;
  if (pcbObj.isDataLoadedToMemory()) {
   out.write("\n" + Integer.toString(count) 
   + ". PART OF THE PARTITION " 
		   + "OCCUPIED (INDEX IN HEX, DATA IN BINARY):");
   out.write("\n");
   DUMP(pcbObj, out);
   count++;
  }
  if (pcbObj.getInput() != null) {
   out.write("\n" + Integer.toString(count) 
   + ". INPUT (HEX): ");
   // write input values to the output file.
   for (String strng: pcbObj.getInput()) {
    out.write(strng + " ");
   }
   count++;
  }
  if (null != pcbObj.getErrorMap() 
		  && !pcbObj.getErrorMap().isEmpty()) {
   Set < String > keys = pcbObj.getErrorMap().keySet();
   out.write("\n" + Integer.toString(count) + ". ERROR : ");
   for (Iterator < String > i = keys.iterator(); i.hasNext();) {
    String key = i.next();
    String value = pcbObj.getErrorMap().get(key);
    out.write("\n" + "\t" + "ERROR CODE : " + key);
    out.write("\n" + "\t" + "ERROR DESCRIPTION : " + value);
   }
   count++;
  }
  //If the data has been loaded into memory
  //show the partition size and its number.
  //If load module has any errors
  //skip printing clock units.
  if (!LOADER.isLoaderError
		  || pcbObj.isDataLoadedToMemory()) {
   out.write("\n" + Integer.toString(count) 
   + ". PARTITION NUMBER (DECIMAL): " 
		   + pcbObj.getPartitionBlockNum() 
		   + ", SIZE OCCUPIED (DECIMAL): " 
		   + pcbObj.getTotalProgramLength());
   count++;
   out.write("\n" + Integer.toString(count) 
   + ". TIME THE JOB ENTERED THE SYSTEM (HEX): " 
		   + operation.DECIMALHEX(
    pcbObj.getTimeJobEnteredTheSystem()));
   count++;
   out.write("\n" + Integer.toString(count) 
   + ". TIME THE JOB LEFT THE SYSTEM (HEX): " 
		   + operation.DECIMALHEX(CPU.TIMECLOCK));
   count++;
   // execution time in decimal.
   out.write("\n" + Integer.toString(count) 
   + ". EXECUTION TIME (DECIMAL): " 
		   + pcbObj.getTimeClockForExecutionTime());
   count++;
   out.write("\n" + Integer.toString(count) 
   + ". TIME SPENT DOING I/O (DECIMAL): " 
		   + pcbObj.getIoOperationExecutionTime());
   count++;
   // I/O time in decimal.
   out.write("\n" + Integer.toString(count) 
   + ". TOTAL RUN TIME (DECIMAL): " 
		   + (pcbObj.getTimeClockForExecutionTime() 
				   + pcbObj.getIoOperationExecutionTime()));
  }
  out.write("\n");
  out.close();
 }

 // This method dumps the partition of a job in main memory.
 public void DUMP(PCB pcbObj, PrintWriter out) {

  String controlSignal = "DUMP";
  int count = 8;
  int mod = 0;
  boolean isLastLine = false;
  StringBuilder sb = new StringBuilder();
  mod = pcbObj.getTotalProgramLength() % 8;
  int x = 0;
  for (int i = pcbObj.getPartitionStartingAddress(); 
		  i < pcbObj.getPartitionStartingAddress() 
		  + pcbObj.getTotalProgramLength(); i++) {
   // To display the first column of the dump screen.
   if (8 == count) {
    if (isLastLine) {
     count = mod;
    }
    // append 0 to the converted 
    //hex number as it will be of 2
    // length.
    sb.append("0" + operation.DECIMALHEX(i));
   }
   sb.append("\t");
   // gets all the instructions from the partition.
   int[] binArray = memory.MEMORYMANAGER(controlSignal,
    operation.DECIMALBIN(i), null);
   StringBuilder strNum = new StringBuilder();
   for (int num: binArray) {
    strNum.append(num);
   }
   if (pcbObj.getTotalProgramLength() - (x + 1) == mod) {
    isLastLine = true;
   }
   count--;
   sb.append(strNum.toString());
   if (0 == count) {
    count = 8;
    out.write("\t" + sb.toString());
    out.write("\n");
    sb = new StringBuilder();
   }
   x++;
  }
 }

 //This method generate the stats for all the jobs of
 //the batch file.
 public void generateProgressFile() throws IOException {

  PrintWriter out;
  out = new PrintWriter(new FileWriter(
   "progress_file.txt", true), true);
  out.write("\n");
  out.write("\n" + "CURRENT VALUE OF THE "
  		+ "CLOCK (HEX) : " + operation.DECIMALHEX(TIMECLOCK));
  out.write("\n" + "MEAN USER JOB RUNTIME "
  		+ "(DECIMAL) : " + String.format("%.1f",
   (float)(SCHEDULER.totalexecutionTime + 
		   SCHEDULER.totalIoExectuionTime) 
   / (LOADER.jobIdsList.size() 
		   - ERROR_HANDLER.totalAbnormalTerminations)));
  out.write("\n" + "MEAN USER JOB EXECUTION TIME "
  		+ "(DECIMAL) : " + String.format("%.1f",
   (float) SCHEDULER.totalexecutionTime 
   / (LOADER.jobIdsList.size() 
		   - ERROR_HANDLER.totalAbnormalTerminations)));
  out.write("\n" + "MEAN USER JOB I/O TIME "
  		+ "(DECIMAL) : " + String.format("%.1f",
   (float) SCHEDULER.totalIoExectuionTime 
   / (LOADER.jobIdsList.size() 
		   - ERROR_HANDLER.totalAbnormalTerminations)));
  if (LOADER.jobIdsList.size() == 1) {
   out.write("\n" + "MEAN USER JOB TIME IN "
   		+ "THE SYSTEM (DECIMAL) : " + String.format("%.1f",
    (float)(CPU.SYSTEMTIMECLOCK 
    		+ SCHEDULER.totalIoExectuionTime) 
    / (LOADER.jobIdsList.size() 
    		- ERROR_HANDLER.totalAbnormalTerminations)));
  } else {
   out.write("\n" + "MEAN USER JOB TIME IN THE "
   		+ "SYSTEM (DECIMAL) : " + String.format("%.1f",
    (float)(CPU.SYSTEMTIMECLOCK + SCHEDULER.totalIoExectuionTime 
    		+ SCHEDULER.idleTime) / (LOADER.jobIdsList.size() 
    				- ERROR_HANDLER.totalAbnormalTerminations)));
  }
  out.write("\n" + "TOTAL CPU IDEAL TIME (DECIMAL"
  		+ ") : " + SCHEDULER.totalIdleTime);
  out.write("\n" + "TOTAL TIME LOST DUE TO PARTIAL "
  		+ "PROCESSING " + "OF THE ABNORMALLY "
  				+ "TERMINATED JOBS (DECIMAL) : " 
  		+ ERROR_HANDLER.totalTimeUsedInAbnormalJobs);
  out.write("\n" + "TOTAL JOBS TERMINATED "
  		+ "NORMALLY " + "(DECIMAL) : " 
		  + (LOADER.jobIdsList.size() 
				  - ERROR_HANDLER.totalAbnormalTerminations));
  out.write("\n" + "TOTAL JOBS TERMINATED "
  		+ "ABNORMALLY " + "(DECIMAL) : " 
		  + ERROR_HANDLER.totalAbnormalTerminations);
  out.write("\n" + "TOTAL TIME LOST DUE TO THE PARTIAL " 
		  + "PROCESSING OF JOBS CONTAINING " 
		  + "SUSPECTED INFINITE LOOP (DECIMAL) : " 
		  + ERROR_HANDLER.totalTimeUsedInInfiniteLoopJobs);
  out.write("\n" + "IDS OF JOBS WITH SUSPECTED " 
		  + "INFINITE LOOP (HEX) : ");
  if (ERROR_HANDLER.jobIdsWithInfiniteLoop.isEmpty() 
		  || null == ERROR_HANDLER.jobIdsWithInfiniteLoop) {
   out.write("000");
  } else {
   for (int i: ERROR_HANDLER.jobIdsWithInfiniteLoop) {
    out.write(operation.DECIMALHEX(i) + "\t");
   }
  }
  out.write("\n" + "MEAN INTERNAL FRAGMENTATION "
  		+ "(DECIMAL) : " + String.format("%.1f",
   (float) SCHEDULER.totalUnusedWords 
   / (LOADER.jobIdsList.size() 
		   - ERROR_HANDLER.totalAbnormalTerminations)));
  out.close();

 }
}