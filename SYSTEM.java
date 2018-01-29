import java.io.File;
import java.io.IOException;

/*
* Atul Joshi
  CS 5323
  Programming Assignment Phase 2
  23rd, April 2017
  SYSTEM class has a main method which is the driver of 
  this simulation.
  SYSTEM calls LOADER and SCHEDULER to execute the main program.
 */
public class SYSTEM {

 public static void main(String[] args) throws IOException {
  NUMBERCONVERSIONOPERATION operation= new NUMBERCONVERSIONOPERATION();
  LOADER loader = new LOADER();
  SCHEDULER scheduler = new SCHEDULER();
  CPU cpu = new CPU();
  File file = new File("progress_file.txt");
  String progressFileMsg = "";
  // if file already exists then delete it.
  if (file.exists()) {
   file.delete();
  }
  // Calls LOADER procedure of the LOADER to load user 
  //program to main memory.
  //If the all the partitions have some jobs then calls 
  //CPU.
  while (true) {
   loader.LOAD("errorJob.txt");
   int numberOfPartitionsOccupied = 0;
   int numberOfAvailableBlocks = 0;
   //Checks the number of partitions available
   //and occupied.
   for (int i: MEMORY.memoryBlockBitsList) {
    if (1 == i) {
     numberOfPartitionsOccupied++;
    } else {
     numberOfAvailableBlocks++;
    }
   }
   //Writes the progress to progress file. 
   progressFileMsg = "\n" 
   + "CURRENT DEGREE OF MULTIPROGRAMMING : " 
   + Integer.toString(numberOfPartitionsOccupied) 
   + "(DECIMAL) AT : " 
   + operation.DECIMALHEX(CPU.TIMECLOCK) 
   + "(HEX) CLOCK TIME.";
   cpu.updateProgressFile(progressFileMsg);
   //Writes the progress to progress file. 
   progressFileMsg = "\n" + "NUMBER OF AVAILABLE BLOCKS : " 
   + Integer.toString(numberOfAvailableBlocks) 
   + "(DECIMAL) AT : " 
   + operation.DECIMALHEX(CPU.TIMECLOCK) 
   + "(HEX) CLOCK TIME.";
   cpu.updateProgressFile(progressFileMsg);
   if (!LOADER.isLoaderError) {
    //calls the scheduler.
    scheduler.RUNJOBS();
   } else {
    LOADER.isLoaderError = false;
   }
   //If all the jobs are processed
   //program terminates.
   if (LOADER.loadModuleEnd 
		   && SCHEDULER.BLOCKEDLIST.isEmpty() 
		   && SCHEDULER.READYQUEUE.isEmpty()) {
    System.exit(0);
   }
  }
 }
}