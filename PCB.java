import java.util.List;
import java.util.Map;

/*PCB keeps with it all the necessary
 *information which are needed to run a job.
 *Below is a POJO class which has all the 
 *variables which are needed to run a job.
 *these all values are set in PCB when a job 
 *enters a system.
 *A POJO contains only getter and setter functions
 *whenever a job enters the system all the values
 *for a particular job are set and whenever those 
 *values are needed to run that job they are 
 *accessed by using getter methods of this class.
 **/
public class PCB {

 int jobId;
 int currentProgramCounter;
 int startingAddress;
 int traceSwitch;
 int numberOfDataLines;
 int numberOfOutputLines;
 int inputAddress;
 int outputAddress;
 int partitionBlockNum;
 int[] R4 = new int[12];
 int[] R5 = new int[12];
 int[] R6 = new int[12];
 int ioClock;
 int timeClock;
 int totalProgramLength;
 int timeJobEnteredTheSystem;
 int timeClockForExecutionTime;
 int ioOperationExecutionTime;
 List < String > input;
 List < String > output;
 int userProgramLength;
 int partitionSize;
 int partitionStartingAddress;
 boolean jobInTrack;
 Map < String, String > warningMap;
 Map < String, String > errorMap;
 int numberOfReadRequestsMade;
 boolean jobinitiated;
 int idleTime;
 int unusedWords;
 boolean jobTerminatedAbnormally;
 int numberOfWriteRequestsMade;
 boolean isDataLoadedToMemory;
 int remainderTimeQuantum;

 public int getRemainderTimeQuantum() {
	return remainderTimeQuantum;
}
public void setRemainderTimeQuantum(int remainderTimeQuantum) {
	this.remainderTimeQuantum = remainderTimeQuantum;
}
public boolean isDataLoadedToMemory() {
  return isDataLoadedToMemory;
 }
 public void setDataLoadedToMemory(boolean isDataLoadedToMemory) {
  this.isDataLoadedToMemory = isDataLoadedToMemory;
 }
 public int getNumberOfWriteRequestsMade() {
  return numberOfWriteRequestsMade;
 }
 public void setNumberOfWriteRequestsMade(int numberOfWriteRequestsMade) {
  this.numberOfWriteRequestsMade = numberOfWriteRequestsMade;
 }
 public void setJobTerminatedAbnormally(boolean jobTerminatedAbnormally) {
  this.jobTerminatedAbnormally = jobTerminatedAbnormally;
 }
 public boolean isJobTerminatedAbnormally() {
  return jobTerminatedAbnormally;
 }
 public int getUnusedWords() {
  return unusedWords;
 }
 public void setUnusedWords(int unusedWords) {
  this.unusedWords = unusedWords;
 }
 public int getIdleTime() {
  return idleTime;
 }
 public void setIdleTime(int idleTime) {
  this.idleTime = idleTime;
 }
 public boolean isJobinitiated() {
  return jobinitiated;
 }
 public void setJobinitiated(boolean jobinitiated) {
  this.jobinitiated = jobinitiated;
 }
 public int getNumberOfReadRequestsMade() {
  return numberOfReadRequestsMade;
 }
 public void setNumberOfReadRequestsMade(int numberOfReadRequestsMade) {
  this.numberOfReadRequestsMade = numberOfReadRequestsMade;
 }
 public Map < String, String > getWarningMap() {
  return warningMap;
 }
 public void setWarningMap(Map < String, String > warningMap) {
  this.warningMap = warningMap;
 }
 public Map < String, String > getErrorMap() {
  return errorMap;
 }
 public void setErrorMap(Map < String, String > errorMap) {
  this.errorMap = errorMap;
 }
 public int getPartitionStartingAddress() {
  return partitionStartingAddress;
 }
 public void setPartitionStartingAddress(int partitionStartingAddress) {
  this.partitionStartingAddress = partitionStartingAddress;
 }
 public int getPartitionSize() {
  return partitionSize;
 }
 public void setPartitionSize(int partitionSize) {
  this.partitionSize = partitionSize;
 }
 public int getUserProgramLength() {
  return userProgramLength;
 }
 public void setUserProgramLength(int userProgramLength) {
  this.userProgramLength = userProgramLength;
 }
 public int getJobId() {
  return jobId;
 }
 public void setJobId(int jobId) {
  this.jobId = jobId;
 }
 public int getCurrentProgramCounter() {
  return currentProgramCounter;
 }
 public void setCurrentProgramCounter(int currentProgramCounter) {
  this.currentProgramCounter = currentProgramCounter;
 }
 public int getTraceSwitch() {
  return traceSwitch;
 }
 public void setTraceSwitch(int traceSwitch) {
  this.traceSwitch = traceSwitch;
 }
 public int getNumberOfDataLines() {
  return numberOfDataLines;
 }
 public void setNumberOfDataLines(int numberOfDataLines) {
  this.numberOfDataLines = numberOfDataLines;
 }
 public int getNumberOfOutputLines() {
  return numberOfOutputLines;
 }
 public void setNumberOfOutputLines(int numberOfOutputLines) {
  this.numberOfOutputLines = numberOfOutputLines;
 }
 public int getInputAddress() {
  return inputAddress;
 }
 public void setInputAddress(int inputAddress) {
  this.inputAddress = inputAddress;
 }
 public int getOutputAddress() {
  return outputAddress;
 }
 public void setOutputAddress(int outputAddress) {
  this.outputAddress = outputAddress;
 }
 public int getPartitionBlockNum() {
  return partitionBlockNum;
 }
 public void setPartitionBlockNum(int partitionBlockNum) {
  this.partitionBlockNum = partitionBlockNum;
 }
 public int[] getR4() {
  return R4;
 }
 public void setR4(int[] iR) {
  R4 = iR;
 }
 public int[] getR5() {
  return R5;
 }
 public void setR5(int[] r5) {
  R5 = r5;
 }
 public int[] getR6() {
  return R6;
 }
 public void setR6(int[] r6) {
  R6 = r6;
 }
 public int getIoClock() {
  return ioClock;
 }
 public void setIoClock(int ioClock) {
  this.ioClock = ioClock;
 }
 public int getTimeClock() {
  return timeClock;
 }
 public void setTimeClock(int timeClock) {
  this.timeClock = timeClock;
 }
 public int getTotalProgramLength() {
  return totalProgramLength;
 }
 public void setTotalProgramLength(int totalProgramLength) {
  this.totalProgramLength = totalProgramLength;
 }
 public int getTimeJobEnteredTheSystem() {
  return timeJobEnteredTheSystem;
 }
 public void setTimeJobEnteredTheSystem(int timeJobEnteredTheSystem) {
  this.timeJobEnteredTheSystem = timeJobEnteredTheSystem;
 }
 public int getTimeClockForExecutionTime() {
  return timeClockForExecutionTime;
 }
 public void setTimeClockForExecutionTime(int timeClockForExecutionTime) {
  this.timeClockForExecutionTime = timeClockForExecutionTime;
 }
 public int getIoOperationExecutionTime() {
  return ioOperationExecutionTime;
 }
 public void setIoOperationExecutionTime(int ioOperationExecutionTime) {
  this.ioOperationExecutionTime = ioOperationExecutionTime;
 }
 public List < String > getInput() {
  return input;
 }
 public void setInput(List < String > input) {
  this.input = input;
 }
 public List < String > getOutput() {
  return output;
 }
 public void setOutput(List < String > output) {
  this.output = output;
 }
 public int getStartingAddress() {
  return startingAddress;
 }
 public void setStartingAddress(int startingAddress) {
  this.startingAddress = startingAddress;
 }
 public boolean isJobInTrack() {
  return jobInTrack;
 }
 public void setJobInTrack(boolean jobInTrack) {
  this.jobInTrack = jobInTrack;
 }
}