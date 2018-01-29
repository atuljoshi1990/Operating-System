import java.io.IOException;

/*
 * INSTRUCTIONTYPEONE construct instructions of type one only.
 */
public class INSTRUCTIONTYPEONE {

 NUMBERCONVERSIONOPERATION operation = new NUMBERCONVERSIONOPERATION();
 MEMORY memory = new MEMORY();
 // Effective address.
 public static int[] EA = new int[12];

 // This module processes type one instructions.
 public void processInstruction(String currentOpcode, PCB pcbObj)
 throws IOException {

  int indirectBit = CPU.R3[0];
  int indexBit = CPU.R3[5];
  int[] ADDR = new int[12];
  String LOGICALAND = "000";
  String ADDITION = "001";
  String STORE = "010";
  String LOAD = "011";
  String JUMP = "100";
  String JUMPANDLINK = "101";
  String controlSignal = "";
  String errorCode = "";
  int[] content = new int[12];

  
  ADDR = CALCULATEADDRESS(CPU.PC, CPU.R3);
  // effective address is calculated.
  EA = CALCULATEEFFECTIVEADDRESS(currentOpcode,
		  ADDR, indirectBit, indexBit, pcbObj);
  // if the program counter value is greater then 
  //the program length throw
  // error, or if the program counter 
  //is less then the partition base address.
  if ((operation.BINDECIMAL(EA) 
		  < pcbObj.getPartitionStartingAddress()) 
		  || (operation.BINDECIMAL(EA) 
		> (pcbObj.getPartitionStartingAddress() 
				+ pcbObj.getUserProgramLength()))) {
   errorCode = "ERROR_003";
   ERROR_HANDLER error = new ERROR_HANDLER();
   error.ERROR_HANDLER(errorCode, pcbObj);
  } else {
   // store the effective address in R6, this is set for trace flag.
   CPU.R6 = memory.MEMORYMANAGER("READ", EA, content);
   // All these checks are to identify what kind of operation is to be
   // performed.
   // Once the operation is identified Register bit is checked to
   // identify
   // the type of register to use R4/R5
   // A control bit is set to identify the type of operation
   if (LOGICALAND.equals(currentOpcode)) {
    // contents of register are anded with the content of effective
    // address location.
    if (0 == CPU.R3[4]) {
     controlSignal = "READ";
     CPU.R5 = operation.LOGICALAND(CPU.R5,
      memory.MEMORYMANAGER(controlSignal, EA, content));
    } else {
     controlSignal = "READ";
     CPU.R4 = operation.LOGICALAND(CPU.R4,
      memory.MEMORYMANAGER(controlSignal, EA, content));
    }
   } else if (ADDITION.equals(currentOpcode)) {
    // contents of register are added to the contents of effective
    // address location
    if (0 == CPU.R3[4]) {
     controlSignal = "READ";
     CPU.R5 = operation.TWOSCOMPLIMENTBINARYADDITION(CPU.R5,
      memory.MEMORYMANAGER(controlSignal, EA, content));
    } else {
     controlSignal = "READ";
     CPU.R4 = operation.TWOSCOMPLIMENTBINARYADDITION(CPU.R4,
      memory.MEMORYMANAGER(controlSignal, EA, content));
    }
   } else if (STORE.equals(currentOpcode)) {
    // store the contents of register to effective address location
    // of
    // memory.
    if (0 == CPU.R3[4]) {
     controlSignal = "WRITE";
     memory.MEMORYMANAGER(controlSignal, EA, CPU.R5);
    } else {
     controlSignal = "WRITE";
     memory.MEMORYMANAGER(controlSignal, EA, CPU.R4);
    }
   } else if (LOAD.equals(currentOpcode)) {
    // loads the contents of effective address location to register.
    if (0 == CPU.R3[4]) {
     controlSignal = "READ";
     CPU.R5 = memory.MEMORYMANAGER(controlSignal, EA, content);
    } else {
     controlSignal = "READ";
     CPU.R4 = memory.MEMORYMANAGER(controlSignal, EA, content);
    }
   } else if (JUMP.equals(currentOpcode)) {
    // program counter jumps to the effective address location.
    CPU.PC = EA;
   } else if (JUMPANDLINK.equals(currentOpcode)) {
    // the content of the program counter is saved and program
    // counter
    // jumps to the effective address location.
    if (0 == CPU.R3[4]) {
     CPU.R5 = CPU.PC;
     CPU.PC = EA;
    } else {
     CPU.R4 = CPU.PC;
     CPU.PC = EA;
    }
   } else {
    // if the opcode is invalid throw error.
    errorCode = "ERROR_006";
    ERROR_HANDLER error = new ERROR_HANDLER();
    // error.ERROR_HANDLER(errorCode);
   }
  }
 }

 // This module calculates the effective address by using the address the
 // indirect bit and the index bit.
 private int[] CALCULATEEFFECTIVEADDRESS(String currentOpcode, int[] ADDR, 
		 int indirectBit, int indexBit, PCB pcbObj) {

  int[] EA = new int[12];
  int[] content = new int[12];
  String controlSignal = "";

  if (0 == indirectBit && 0 == indexBit) {
   // if direct addressing mode.
   // effective address is equal to address.
   EA = ADDR;
  } else if (0 == indirectBit && 1 == indexBit) {
   // if indexing addressing mode.
   // effective address is equal to sum of 
   //address and contents of
   // register R4.
   EA = operation.BINARYADDITION(ADDR, CPU.R4);
  } else if (1 == indirectBit && 0 == indexBit) {
   // if indirect addressing mode.
   // effective address = contents of 
   //the address location.
   controlSignal = "READ";
   if("100".equals(currentOpcode)){
	 EA = memory.MEMORYMANAGER(controlSignal, ADDR, content);  
   }else{
	 EA = memory.MEMORYMANAGER(controlSignal, ADDR, content);
	 EA = operation.BINARYADDITION(EA, 
			 operation.DECIMALBIN(pcbObj.getPartitionStartingAddress()));
   }
   
  } else if (1 == indirectBit && 1 == indexBit) {
   // if if indirect indexing addressing mode.
   // effective address is equal to sum of 
   //contents of address location
   // and the contents of register R4.
   controlSignal = "READ";
   EA = operation.BINARYADDITION(
    memory.MEMORYMANAGER(controlSignal, ADDR, content),
    CPU.R4);
  }
  return EA;
 }

 // This module calculates the address by using 
 //the program counter and last
 // six bits of R3
 public int[] CALCULATEADDRESS(int[] PC, int[] word) {

  int[] ADDR = new int[12];
  int[] INSTR = new int[6];
  String INSTRSTRING = "";
  int[] INSTRSE = new int[12];
  int x = 0;
  // get the last six bits of the instruction.
  for (int i = 6; i < 12; i++) {
   INSTR[x] = word[i];
   x++;
  }
  StringBuilder binaryStr = new StringBuilder();
  for (int num: INSTR) {
   binaryStr.append(num);
  }
  INSTRSTRING = binaryStr.toString();
  // if the left most bit is 0 append 6 0s to the left.
  if (0 == INSTR[0]) {
   for (int i = 0; i < 6; i++) {
    INSTRSTRING = "0" + INSTRSTRING;
   }
   // if the left most bit is 1 append 6 0s.to the left.
  } else {
   for (int i = 0; i < 6; i++) {
    INSTRSTRING = "1" + INSTRSTRING;
   }
  }
  for (int i = 0; i < INSTRSE.length; i++) {
   INSTRSE[i] = Integer.parseInt(INSTRSTRING.charAt(i) + "");
  }
  // Add the program counter and the sign extended address.
  ADDR = operation.BINARYADDITION(PC, INSTRSE);
  return ADDR;
 }
}