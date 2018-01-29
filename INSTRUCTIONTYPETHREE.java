import java.io.IOException;

/*
 * INSTRUCTIONTYPETHREE construct instructions of type three only.
 */
public class INSTRUCTIONTYPETHREE {

 // This module processes type three instructions.
 public void processInstruction(PCB pcbObj) throws IOException {

  int clearBit = CPU.R3[5];
  int incrementBit = CPU.R3[6];
  int complementBit = CPU.R3[7];
  int byteSwapBit = CPU.R3[8];
  int rotateLeftBit = CPU.R3[9];
  int rotateRightBit = CPU.R3[10];
  int shiftMAgnitudeBit = CPU.R3[11];
  int[] binaryOne = {0,0,0,0,0,0,0,0,0,0,0,1};
  NUMBERCONVERSIONOPERATION operation = new NUMBERCONVERSIONOPERATION();
  String errorCode = "";
  int count = 0;
  // If the wrong combination of actions is specified increase the count.
  for (int i = 5; i < 12; i++) {
   if (1 == CPU.R3[i]) {
    count = count + 1;
   }
  }
  if ((CPU.R3[9] == 1 && CPU.R3[11] == 1) 
		  || (CPU.R3[10] == 1 && CPU.R3[11] == 1)) {
   count = 0;
  }
  // through error if the count is greater than 1.
  if (count > 1) {
   errorCode = "ERROR_001";
   ERROR_HANDLER error = new ERROR_HANDLER();
   error.ERROR_HANDLER(errorCode, pcbObj);
  }
  // if clear bit is set.
  if (1 == clearBit) {
   // clear the values of the registers.
   if (0 == CPU.R3[4]) {
    CPU.R5 = new int[12];
   } else {
    CPU.R4 = new int[12];
   }
  }
  // if the increment bit is set.
  if (1 == incrementBit) {
   // add increment the register by 1.
   if (0 == CPU.R3[4]) {
    CPU.R5 = operation.BINARYADDITION(CPU.R5, binaryOne);
   } else {
    CPU.R4 = operation.BINARYADDITION(CPU.R4, binaryOne);
   }
  }
  // if the compliment bit is set.
  if (1 == complementBit) {
   // calculate the one's compliment of 
   //the register and update it.
   if (0 == CPU.R3[4]) {
    CPU.R5 = operation.CALCULATEONESCOMPLEMENT(CPU.R5);
   } else {
    CPU.R4 = operation.CALCULATEONESCOMPLEMENT(CPU.R4);
   }
  }
  // if the byte swap bit is set
  if (1 == byteSwapBit) {
   // swap the bits of the register.
   if (0 == CPU.R3[4]) {
    CPU.R5 = SWAPBITS(CPU.R5);
   } else {
    CPU.R4 = SWAPBITS(CPU.R4);
   }
  }
  // if the rotate left bit is set.
  if (1 == rotateLeftBit) {
   // if shift magnitude is set then 
   //rotate two bits else one bit.
   if (0 == shiftMAgnitudeBit) {
    if (0 == CPU.R3[4]) {
     CPU.R5 = ROTATELEFT(CPU.R5, 1);
    } else {
     CPU.R4 = ROTATELEFT(CPU.R4, 1);
    }
   } else {
    if (0 == CPU.R3[4]) {
     CPU.R5 = ROTATELEFT(CPU.R5, 2);
    } else {
     CPU.R4 = ROTATELEFT(CPU.R4, 2);
    }
   }
  }
  // if the rotate right bit is set.
  if (1 == rotateRightBit) {
   // if shift magnitude is set then 
   //rotate two bits else one bit.
   if (0 == shiftMAgnitudeBit) {
    if (0 == CPU.R3[4]) {
     CPU.R5 = ROTATERIGHT(CPU.R5, 1);
    } else {
     CPU.R4 = ROTATERIGHT(CPU.R4, 1);
    }
   } else {
    if (0 == CPU.R3[4]) {
     CPU.R5 = ROTATERIGHT(CPU.R5, 2);
    } else {
     CPU.R4 = ROTATERIGHT(CPU.R4, 2);
    }
   }
  }
 }

 // This module rotates the bits of the register 
 //to the left based on the
 // shift magnitude.
 public int[] ROTATELEFT(int[] r5, int q) {
  int[] shiftedArray = new int[12];
  int[] temp = new int[12];
  if (q == 1) {
   // if the shift magnitude is 0 rotate 1 bit.
   for (int i = 1; i < r5.length; i++) {
    // copy all bits of r5 from location 1 to the end and paste it
    // to a new array.
    shiftedArray[i - 1] = r5[i];
   }
   // copy the first bit of the r5 array to the last bit of the new
   // array.
   shiftedArray[11] = r5[0];
  } else {
   // if shift magnitude is 1 rotate 2 bits.
   for (int i = 1; i < r5.length; i++) {
    // store the last 11 contents of the r5 to a temp array.
    temp[i - 1] = r5[i];
   }
   // store the first content of register to the last index of the temp
   // array.
   // repeat the above process.
   temp[11] = r5[0];
   for (int i = 1; i < temp.length; i++) {
    shiftedArray[i - 1] = temp[i];
   }
   shiftedArray[11] = temp[0];
  }
  return shiftedArray;
 }

 // This module rotates the bits of the 
 //register to the right based on the
 // shift magnitude.
 public int[] ROTATERIGHT(int[] r5, int q) {
  int[] shiftedArray = new int[12];
  int[] temp = new int[12];
  if (q == 1) {
   // if the shift magnitude is 0 rotate 1 bit.
   for (int i = 0; i < r5.length - 1; i++) {
    // copy all bits from location 0 
	   //to the second last position of
    // r5 and paste it to a new array.
    shiftedArray[i + 1] = r5[i];
   }
   // paste the last value of r5 to the 
   //first value of new array.
   shiftedArray[0] = r5[11];
  } else {
   // if shift magnitude is 1 rotate 2 bits.
   for (int i = 0; i < r5.length - 1; i++) {
    // copy all bits from location 0 to the second last position of
    // r5 and paste it to a new array.
    temp[i + 1] = r5[i];
   }
   // paste the last value of r5 to the first value of new array.
   temp[0] = r5[11];
   // repeat the above process.
   for (int i = 0; i < temp.length - 1; i++) {
    shiftedArray[i + 1] = temp[i];
   }
   shiftedArray[0] = temp[11];
  }
  return shiftedArray;
 }

 // This module swaps the bits of 
 //the register (six bits either way).
 private int[] SWAPBITS(int[] register) {

  int[] swapedArry = new int[12];
  int x = 11;
  for (int i = 0; i < register.length; i++) {
   // swap the first and last and 
	  //then second and second last and so
   // on.
   swapedArry[i] = register[x];
   swapedArry[x] = register[i];
   x--;
  }
  return swapedArry;
 }
}