/*
 * NUMBERCONVERSION is a helper class and 
 * it consists of call helper methods 
   like HEXBIN, BINHEX, DECIMALBIN.
   NUMBERCONVERSION also has modules which 
   perform logical operations like 1's compliment, AND etc.
*/
public class NUMBERCONVERSIONOPERATION {

 // This module takes a hexadecimal number 
 //and converts it to binary.
 public int[] HEXBIN(String hexString) {

  // convert hex number to decimal.
  int tempBinNum = (Integer.parseInt(hexString, 16));
  // convert decimal to binary.
  String binaryNumString = Integer.toBinaryString(tempBinNum);
  int binaryNumLength = binaryNumString.length();
  int paddingTobeDone = 12 - binaryNumLength;
  int[] binaryNum = new int[12];
  boolean length = false;
  // if the length of the generated binary number 
  //is less than 12 then
  // append that many 0s to the left.
  if (paddingTobeDone == 0) {
   length = true;
  } else {
   for (int i = 0; i < paddingTobeDone; i++) {
    binaryNumString = "0" + binaryNumString;
   }
   length = true;
  }
  if (length) {
   for (int i = 0; i < binaryNumString.length(); i++) {
    binaryNum[i] = binaryNumString.charAt(i) - '0';
   }
  }
  return binaryNum;
 }

 // This module takes a decimal number 
 //and converts it to binary.
 public int[] DECIMALBIN(int decimal) {

  // convert decimal to binary
  String binaryNumString = Integer.toBinaryString(decimal);
  int binaryNumLength = binaryNumString.length();
  int paddingTobeDone = 0;
  int[] binaryNum = new int[12];
  if (binaryNumLength < 12) {
   paddingTobeDone = 12 - binaryNumLength;
  } else if (binaryNumLength > 12) {
   int x = binaryNumString.length() - 12;
   int y = 0;
   for (int i = x; i < binaryNumString.length(); i++) {
    binaryNum[y] = binaryNumString.charAt(i) - '0';
    y++;
   }
   return binaryNum;
  }
  boolean length = false;
  // if the length of the generated binary number 
  //is less than 12 then
  // append that many 0s to the left.
  if (paddingTobeDone == 0) {
   length = true;
  } else {
   for (int i = 0; i < paddingTobeDone; i++) {
    binaryNumString = "0" + binaryNumString;
   }
   length = true;
  }
  if (length) {
   for (int i = 0; i < binaryNumString.length(); i++) {
    binaryNum[i] = binaryNumString.charAt(i) - '0';
   }
  }
  return binaryNum;
 }

 // This module takes a binary number and converts 
 //it to hexadecimal number.
 public String BINHEX(int[] binArray) {

  StringBuilder binaryStr = new StringBuilder();
  // create a binary string from the binary array.
  for (int num: binArray) {
   binaryStr.append(num);
  }
  // convert binary string to decimal.
  int decimal = Integer.parseInt(binaryStr.toString(), 2);
  // convert the decimal to hexadecimal.
  String hexStr = Integer.toString(decimal, 16);
  int hexNumLength = hexStr.length();
  int paddingTobeDone = 3 - hexNumLength;
  boolean length = false;
  // if the length of the hex number is less 
  //then 3 then append that many
  // zeros to the left.
  if (paddingTobeDone == 0) {
   length = true;
  } else {
   for (int i = 0; i < paddingTobeDone; i++) {
    hexStr = "0" + hexStr;
   }
   length = true;
  }
  if (length) {
   return hexStr;
  }
  return "";
 }

 // This module takes a decimal number and 
 //converts it to hexadecimal number.
 public String DECIMALHEX(int decimal) {

  // converts the decimal to hex num.
  String hexStr = Integer.toString(decimal, 16);
  int hexNumLength = hexStr.length();
  int paddingTobeDone = 3 - hexNumLength;
  boolean length = false;
  // if the length of the hex number is less 
  //then 3 then append that many
  // zeros to the left.
  if (paddingTobeDone == 0) {
   length = true;
  } else {
   for (int i = 0; i < paddingTobeDone; i++) {
    hexStr = "0" + hexStr;
   }
   length = true;
  }
  if (length) {
   return hexStr;
  }
  return "";
 }

 // This module takes a binary number and 
 //converts it to decimal number.
 public int BINDECIMAL(int[] binArray) {

  StringBuilder strNum = new StringBuilder();
  int numDecimal = 0;
  // generate binary number string from binary array.
  for (int num: binArray) {
   strNum.append(num);
  }
  // convert the binary num to decimal.
  numDecimal = Integer.parseInt(strNum.toString(), 2);
  return numDecimal;
 }

 // This module performs the addition of two 
 //binary numbers using two's compliment.
 public int[] BINARYADDITION(int[] binNumOne, int[] binNumTwo) {
  int[] sum = new int[12];
  int cry = 0;
  for (int i = 11; i >= 0; i--) {
   // Here we are adding each bit of two numbers and looking the carry.
   // Below are all possible conditions of the carry and the two bits
   // those are to be added.
   // The total sum is calculated accordingly.
   if (cry == 0 && binNumOne[i] == 0 && binNumTwo[i] == 0) {
    sum[i] = 0;
    cry = 0;
   } else if (cry == 0 && binNumOne[i] == 0 && binNumTwo[i] == 1) {
    sum[i] = 1;
    cry = 0;
   } else if (cry == 0 && binNumOne[i] == 1 && binNumTwo[i] == 0) {
    sum[i] = 1;
    cry = 0;
   } else if (cry == 0 && binNumOne[i] == 1 && binNumTwo[i] == 1) {
    sum[i] = 0;
    cry = 1;
   } else if (cry == 1 && binNumOne[i] == 0 && binNumTwo[i] == 0) {
    sum[i] = 1;
    cry = 0;
   } else if (cry == 1 && binNumOne[i] == 0 && binNumTwo[i] == 1) {
    sum[i] = 0;
    cry = 1;
   } else if (cry == 1 && binNumOne[i] == 0 && binNumTwo[i] == 0) {
    sum[i] = 1;
    cry = 0;
   } else if (cry == 1 && binNumOne[i] == 1 && binNumTwo[i] == 1) {
    sum[i] = 1;
    cry = 1;
   }
  }
  return sum;
 }

 // This module perform logical and operation on two binary numbers.
 public int[] LOGICALAND(int[] binOne, int[] binTwo) {

  int[] andedArray = new int[12];
  for (int i = 0; i < andedArray.length; i++) {
   andedArray[i] = binOne[i] * binTwo[i];
  }
  return andedArray;
 }

 // This module calculates one's complement of a given binary number.
 public int[] CALCULATEONESCOMPLEMENT(int[] binArry) {

  int[] onesComplemetNum = new int[12];
  for (int i = 0; i < binArry.length; i++) {
   if (0 == binArry[i]) {
    onesComplemetNum[i] = 1;
   } else if (1 == binArry[i]) {
    onesComplemetNum[i] = 0;
   }
  }
  return onesComplemetNum;
 }

 // This module converts negative binary numbers to decimal numbers.
 public int BINTODECIMALTWOSCOMPLEMENT(int[] binArry) {

  StringBuilder strNum = new StringBuilder();
  for (int num: binArry) {
   strNum.append(num);
  }
  String binaryString = strNum.toString();
  // Check if the number is negative.
  // We know it's negative if it starts with a 1
  if (binaryString.charAt(0) == '1') {
   // Call invert digits method
   String invertedString = invertDigits(binaryString);
   int decimal = Integer.parseInt(invertedString, 2);
   // Add 1 to the current decimal and multiply it by -1
   decimal = (decimal + 1) * -1;
   // return the final result
   return decimal;
  } else {
   return Integer.parseInt(binaryString, 2);
  }
 }

 // This method is used to invert the bits of the register.
 public String invertDigits(String binaryInt) {
  String result = binaryInt;
  result = result.replace("0", " "); // temp replace 0s
  result = result.replace("1", "0"); // replace 1s with 0s
  result = result.replace(" ", "1"); // put the 1s back in
  return result;
 }

 // This module takes a hexadecimal number and converts it to decimal.
 public int HEXDECIMAL(String hexString) {

  // convert hex number to decimal.
  int decimalNum = (Integer.parseInt(hexString, 16));
  return decimalNum;
 }

 public int[] TWOSCOMPLIMENTBINARYADDITION(int[] binNumOne, int[] binNumTwo) {
  int[] sum = new int[12];
  int numOne = BINTODECIMALTWOSCOMPLEMENT(binNumOne);
  int numTwo = BINTODECIMALTWOSCOMPLEMENT(binNumTwo);
  int sumDecimal = numOne + numTwo;
  sum = DECIMALBIN(sumDecimal);
  return sum;
 }
}