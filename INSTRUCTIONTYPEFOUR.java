/*
 * INSTRUCTIONTYPEFOUR construct instructions of type four only.
 */
public class INSTRUCTIONTYPEFOUR {

 // This module processes type four instructions.
 public void processInstruction(PCB pcbObj) {

  int equalBit = CPU.R3[5];
  int lessThanBit = CPU.R3[6];
  int greaterThanBit = CPU.R3[7];
  NUMBERCONVERSIONOPERATION operation = new NUMBERCONVERSIONOPERATION();
  int[] binaryOne = {0,0,0,0,0,0,0,0,0,0,0,1};

  if (0 == equalBit && 0 == lessThanBit && 0 == greaterThanBit) {
   // NO SKIP, next instruction is executed normally.
  } else if (0 == equalBit && 0 == lessThanBit && 1 == greaterThanBit) {
   // if greater than bit is set and the content of register is greater
   // than 0 then skip the next instruction.
   if (0 == CPU.R3[4]) {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R5) > 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   } else {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R4) > 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   }
  } else if (0 == equalBit && 1 == lessThanBit && 0 == greaterThanBit) {
   // if less than bit is set and content of register is less than o
   // then skip the next instruction.
   if (0 == CPU.R3[4]) {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R5) < 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   } else {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R4) < 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   }
  } else if (0 == equalBit && 1 == lessThanBit && 1 == greaterThanBit) {
   // if less than and greater than bit are set and the content of
   // register is not equal to 0 then skip the next instruction.
   if (0 == CPU.R3[4]) {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R5) != 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   } else {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R4) != 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   }
  } else if (1 == equalBit && 0 == lessThanBit && 0 == greaterThanBit) {
   // if equal bit is set and the content of register is equal to 0
   // then skip the next instruction.
   if (0 == CPU.R3[4]) {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R5) == 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   } else {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R4) == 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   }
  } else if (1 == equalBit && 0 == lessThanBit && 1 == greaterThanBit) {
   // if greater than and equal bits are set and content of register is
   // greater than or equal to 0 then skip the next instruction.
   if (0 == CPU.R3[4]) {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R5) >= 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   } else {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R4) >= 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   }
  } else if (1 == equalBit && 1 == lessThanBit && 0 == greaterThanBit) {
   // if less than and equal to bits are set and content of register is
   // less than or equal to 0 then skip the next instruction.
   if (0 == CPU.R3[4]) {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R5) <= 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   } else {
    if (operation.BINTODECIMALTWOSCOMPLEMENT(CPU.R4) <= 0) {
     CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
    }
   }
  } else if (1 == equalBit && 1 == lessThanBit && 1 == greaterThanBit) {
   // if all three bits are set then skip the next instruction.
   if (0 == CPU.R3[4]) {
    CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
   } else {
    CPU.PC = operation.BINARYADDITION(CPU.PC, binaryOne);
   }
  }
 }
}