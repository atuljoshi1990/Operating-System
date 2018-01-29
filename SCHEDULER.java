import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * SCHEDULER is responsible for dispatching jobs and maintaining
 * the ready queue and the block list.
 * Once a job arrives it is added to the ready queue.
 * The ready queue is organized in a round robin fashion
 * with a time quantum of 35 units.*/
public class SCHEDULER {

 static List < PCB > READYQUEUE = new ArrayList < PCB > ();
 static List < PCB > BLOCKEDLIST = new ArrayList < PCB > ();
 static int idleTime = 0;
 static int totalIdleTime = 0;
 static int totalRunTime = 0;
 static int totalexecutionTime = 0;
 static int totalIoExectuionTime = 0;
 static int totalUnusedWords = 0;
 boolean jobTerminated = false;
 NUMBERCONVERSIONOPERATION convert = new NUMBERCONVERSIONOPERATION();
 MEMORY memory = new MEMORY();
 CPU cpu = new CPU();
 String progressFileMsg = "";

 //This methods takes the jobs from partition
 //block and run them.
 //This method is also responsible for dispatching
 //jobs and maintaining the ready queue and block
 //list.
 public void RUNJOBS() throws IOException {

   String executionType = "";
   //Indefinite loop for scheduling jobs
   while (true) {
    //First check whether the blocked list is empty
    //or not.
    if (!BLOCKEDLIST.isEmpty()) {
     if (READYQUEUE.isEmpty()) {
      CPU cpu = new CPU();
      PCB object = BLOCKEDLIST.get(0);
      //Check if the CPU is IDEAL or not
      //if ideal increment the clock.
      if (0 != object.getIoClock()) {
       if (0 == object.getIdleTime()) {
        object.setIdleTime(1);
       } else {
        object.setIdleTime(object.getIdleTime() + 1);
       }
       totalIdleTime++;
       CPU.TIMECLOCK++;
       CPU.SYSTEMTIMECLOCK++;
       cpu.CLOCKOPERATIONS(1);
      }
     }
     //if block list is not empty check if 
     //there are jobs ready to be added to 
     //ready queue based on their time quantum value.
     //If any such jobs are present add them to 
     //ready queue.
     Iterator < PCB > it = BLOCKEDLIST.iterator();
     while (it.hasNext()) {
      PCB obj = it.next();
      if (0 == obj.getIoClock()) {
       int timeQuantumRemainder = 35 - obj.getTimeClock();
       if (timeQuantumRemainder > 0 
    		   && timeQuantumRemainder < 6) {
        ADDTOREADYQUEUE(6, obj);
       } else if (timeQuantumRemainder > 5 
    		   && timeQuantumRemainder < 11) {
        ADDTOREADYQUEUE(5, obj);
       } else if (timeQuantumRemainder > 10 
    		   && timeQuantumRemainder < 16) {
        ADDTOREADYQUEUE(4, obj);
       } else if (timeQuantumRemainder > 15 
    		   && timeQuantumRemainder < 21) {
        ADDTOREADYQUEUE(3, obj);
       } else if (timeQuantumRemainder > 20 
    		   && timeQuantumRemainder < 26) {
        ADDTOREADYQUEUE(2, obj);
       } else if (timeQuantumRemainder > 25 
    		   && timeQuantumRemainder < 31) {
        ADDTOREADYQUEUE(1, obj);
       } else if (timeQuantumRemainder > 30 
    		   && timeQuantumRemainder < 36) {
        ADDTOREADYQUEUE(0, obj);
       }
       //Write to progress file.
       progressFileMsg = "\n" + "JOB " 
       + convert.DECIMALHEX(obj.getJobId()) 
       + "(HEX) REMOVED FROM BLOCKED LIST AT : " 
       + convert.DECIMALHEX(CPU.TIMECLOCK) 
       + "(HEX) CLOCK TIME.";
       cpu.updateProgressFile(progressFileMsg);
       it.remove();
      }
     }
    }
    //Check if there are jobs available in the ready queue.
    //If there are any available send them to CPU for 
    //execution.
    if (!READYQUEUE.isEmpty()) {
     PCB pcbObj = null;
     //Get the first job in the ready queue and 
     //send it to CPU for execution.
     pcbObj = READYQUEUE.get(0);
     //Remove the job from ready queue.
     READYQUEUE.remove(0);
     //Get the type of execution done on the 
     //instruction.
     executionType = cpu.CPU(pcbObj);
     //If the job requests for extra quantum.
     //Add the job to the ready queue.
     if ("extraQuantumNeeded".equals(executionType)) {
      READYQUEUE.add(pcbObj);
      //write to progress file.
      progressFileMsg = "\n" + "JOB " 
      + convert.DECIMALHEX(pcbObj.getJobId()) 
      + "(HEX) REQUESTED MORE CPU " 
      + "TIME AND ADDED TO READY QUEUE AT : " 
      + convert.DECIMALHEX(CPU.TIMECLOCK) 
      + "(HEX) CLOCK TIME.";
      cpu.updateProgressFile(progressFileMsg);
      //If job requested for read write operation
      //add it to block list.
     } else if ("readWriteOperationOn".equals(executionType)) {
      BLOCKEDLIST.add(pcbObj);
      //write to progress file.
      progressFileMsg = "\n" + "JOB " 
      + convert.DECIMALHEX(pcbObj.getJobId()) 
      + "(HEX) ADDED TO BLOCKED LIST AT : " 
      + convert.DECIMALHEX(CPU.TIMECLOCK) 
      + "(HEX) CLOCK TIME.";
      cpu.updateProgressFile(progressFileMsg);
      //If job terminated, write the result to the 
      //progress file.
     } else if ("jobTerminated".equals(executionType)) {
      PrintWriter out;
      out = new PrintWriter(new FileWriter("progress_file.txt", true), true);
      MEMORY memory = new MEMORY();
      if (pcbObj.isJobTerminatedAbnormally()) {
       cpu.writeErrorToOutputFile(pcbObj);
      } else {
       cpu.writeToOutputFile(pcbObj);
      }
      //Check for loader errors if not 
      //present set the progress file 
      //statistics parameters.
      if (!LOADER.isLoaderError) {
       totalUnusedWords = totalUnusedWords 
    		   + pcbObj.getUnusedWords();
       idleTime = idleTime 
    		   + pcbObj.getIdleTime();
       totalexecutionTime = totalexecutionTime 
    		   + pcbObj.getTimeClockForExecutionTime();
       totalIoExectuionTime = totalIoExectuionTime 
    		   + pcbObj.getIoOperationExecutionTime();
      }
      jobTerminated = true;
      out.write("MEMORY DEALLOCATED FOR JOB " 
      + convert.DECIMALHEX(pcbObj.getJobId()) 
      + "(HEX)");
      //Deallocate memory block.
      memory.DELLOCATEMEMORYBLOCK(pcbObj);
      pcbObj = null;
      out.close();
     }
    }
    //If the blocked list is empty 
    //and ready queue is empty and there are 
    //no more jobs to process generate the stats in the 
    //progress file.
    if (BLOCKEDLIST.isEmpty() 
    		&& READYQUEUE.isEmpty() && jobTerminated) {
     if (LOADER.loadModuleEnd) {
      cpu.generateProgressFile();
     }
     break;
    } else if (jobTerminated) {
     jobTerminated = false;
     break;
    }
   }
  }
  //This method takes a job and add it to the ith location
  //decided by the time remaining quantum of the ready queue.
 public void ADDTOREADYQUEUE(int i, PCB pcbObj) throws IOException {

  PrintWriter out;
  out = new PrintWriter(new FileWriter("progress_file.txt", true), true);
  if (READYQUEUE.size() >= i) {
   READYQUEUE.add(i, pcbObj);
  } else {
   READYQUEUE.add(pcbObj);
  }
  progressFileMsg = "\n" + "JOB " 
  + convert.DECIMALHEX(pcbObj.getJobId()) 
  + "(HEX) ADDED TO READY QUEUE AT : " 
  + convert.DECIMALHEX(CPU.TIMECLOCK) 
  + "(HEX) CLOCK TIME.";
  cpu.updateProgressFile(progressFileMsg);
 }
}