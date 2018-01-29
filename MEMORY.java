/*
 * MEMORY consists of main memory.
   Words are loaded to MEMORY and read 
   from MEMORY using MEMORYMANAGER.
*/
public class MEMORY {

 /*
  * Global variables : 
  * mainMemory : a 2D array of 512 12 bit elements.
  * memoryBlockList : block list
  * memoryBlockBitsList : bits used to allocate partition.
  * 
  */
 public static int[][] mainMemory = new int[512][12];
 NUMBERCONVERSIONOPERATION operation = new NUMBERCONVERSIONOPERATION();
 static int[] memoryBlockList = {32,32,64,64,64,128,128};
 static int[] memoryBlockBitsList = {0,0,0,0,0,0,0};

 // MEMORYBUFFERREGISTER puts each word to main memory
 public void MEMORYBUFFERREGISTER(int[] word, int i) {
  mainMemory[i] = word;
 }

 // MEMORYADDRESSREGISTER reads words from main memory.
 public int[] MEMORYADDRESSREGISTER(int[] PC) {

  int[] word = new int[12];
  int memoryAddress = 0;
  memoryAddress = operation.BINDECIMAL(PC);
  // fetch the word from main meory from a specified location.
  word = mainMemory[memoryAddress];
  return word;
 }

 /*
  * MEMORYMANAGER is responsible to read and 
  * write a word from and to main
  * memory. Based on the control signal 
  * MEMORYMANAGER performs READ, WRITE
  * operations.
  */
 public int[] MEMORYMANAGER(String controlSignal, int[] ADDR, int[] content) {

  int[] word = new int[12];
  int memoryAddress = 0;
  memoryAddress = operation.BINDECIMAL(ADDR);
  // Check whether the operation to be 
  //performed is READ or WRITE or DUMP.
  if ("READ".equals(controlSignal) 
		  || "DUMP".equals(controlSignal)) {
   word = mainMemory[memoryAddress];
   return word;
  } else if ("WRITE".equals(controlSignal)) {
   MEMORYBUFFERREGISTER(content, memoryAddress);
  }
  return null;
 }

 public String ALLOCATEMEMORYBLOCK(int programLength, PCB pcbObj) {

  int startingBaseAddress = 0;
  for (int i = 0; i < memoryBlockList.length; i++) {
   if (i == 0) {
    pcbObj.setPartitionSize(32);
   } else if (i == 1) {
    startingBaseAddress = 32;
    pcbObj.setPartitionSize(32);
   } else if (i == 2) {
    startingBaseAddress = 64;
    pcbObj.setPartitionSize(64);
   } else if (i == 3) {
    startingBaseAddress = 128;
    pcbObj.setPartitionSize(64);
   } else if (i == 4) {
    startingBaseAddress = 192;
    pcbObj.setPartitionSize(64);
   } else if (i == 5) {
    startingBaseAddress = 256;
    pcbObj.setPartitionSize(128);
   } else if (i == 6) {
    startingBaseAddress = 384;
    pcbObj.setPartitionSize(128);
   }
   if (programLength <= memoryBlockList[i] 
		   && memoryBlockBitsList[i] == 0) {
    memoryBlockBitsList[i] = 1;
    return Integer.toString(startingBaseAddress) 
    		+ ":" + Integer.toString(i);
   } else {
    startingBaseAddress = 999;
   }
  }
  return Integer.toString(startingBaseAddress);
 }

 //This method is used to deallocate memory if a 
 //job is terminated.
 public void DELLOCATEMEMORYBLOCK(PCB pcbObj) {

  int partitionBlock = 0;
  partitionBlock = pcbObj.getPartitionBlockNum();
  memoryBlockBitsList[partitionBlock] = 0;
 }
}