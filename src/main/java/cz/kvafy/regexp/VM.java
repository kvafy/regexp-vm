package cz.kvafy.regexp;

import java.util.HashMap;
import java.util.LinkedList;

class VM {
    /** Bottom thread has highest priority. After successful match, bottom thread holds context of the match. */
    private LinkedList<VMThread> threadStack = new LinkedList<VMThread>();
    /**
     * Thread that captures context of the last successful match after invoking
     * run(). Is null when during the last invocation of {@link #run()} no
     * thread succeeded.
     */
    private VMThread matchContextThread = null;
    /** Input string against which we match. */
    private String input;
    
    public VM(VMCode vmCode, String input, int initialPosition) {
        // create initial thread with the starting position within the input string.
        VMThread initialThread = new VMThread(initialPosition, vmCode.firstInstruction, null, null);
        this.threadStack.add(initialThread);
        this.input = input;
    }
    
    /**
     * Continues execution with the next thread on stack.
     * Finishes either when a thread succeeds or when all remaining threads fail.
     * If successful, the succeeding thread, which holds context of the match,
     * can be obtained via {@link #getMatchContextThread()}. 
     * @return
     *     True when some of remaining threads succeeds, otherwise false.
     */
    public boolean run() {
        matchContextThread = null;
        
        while(true) {
            if(this.threadStack.isEmpty())
                return false;
            VMThread thread = this.threadStack.getFirst();
            
            switch(thread.currentInst.type) {
            case LABEL:
                thread.currentInst = thread.currentInst.next;
                break;
            case JUMP:
                thread.currentInst = thread.currentInst.jmp1;
                break;
            case ANY_CHAR:
                if(thread.inputPos < input.length()) {
                    thread.inputPos++;
                    thread.currentInst = thread.currentInst.next;
                }
                else
                    threadStack.removeFirst();
                break;
            case LITERAL_FROM_SET:
                if(thread.inputPos < input.length()) {
                    char curChar = input.charAt(thread.inputPos);
                    if(thread.currentInst.literalSet.indexOf(curChar) != -1) {
                        thread.inputPos++;
                        thread.currentInst = thread.currentInst.next;
                    }
                    else
                        threadStack.removeFirst();
                }
                else
                    threadStack.removeFirst();
                break;
            case SPLIT:
                VMThread thread2 = new VMThread(thread.inputPos, thread.currentInst.jmp2, thread.groupBeginMap, thread.groupEndMap);
                threadStack.add(1, thread2);
                thread.currentInst = thread.currentInst.jmp1;
                break;
            case SUCCESS:
                matchContextThread = this.threadStack.removeFirst();
                return true;
            case GROUP_BEGIN:
                thread.groupBeginMap.put(thread.currentInst.groupNumber, thread.inputPos);
                thread.currentInst = thread.currentInst.next;
                break;
            case GROUP_END:
                thread.groupEndMap.put(thread.currentInst.groupNumber, thread.inputPos);
                thread.currentInst = thread.currentInst.next;
                break;
            case BACKREFERENCE:
                Integer beginPos = thread.groupBeginMap.get(thread.currentInst.groupNumber);
                Integer endPos = thread.groupEndMap.get(thread.currentInst.groupNumber);
                String capturedString = (beginPos == null || endPos == null)
                                      ? ""  // as if matched empty string
                                      : input.substring(beginPos, endPos);
                if(input.substring(thread.inputPos).startsWith(capturedString)) {
                    thread.inputPos += capturedString.length();
                    thread.currentInst = thread.currentInst.next;
                }
                else
                    this.threadStack.removeFirst();
                break;
            default:
                throw new IllegalStateException("Unknown instruction: " + thread.currentInst);
            }
        }
    }
    
    public boolean hasNoThreadToRun() {
        return this.threadStack.isEmpty();
    }
    
    public VMThread getMatchContextThread() {
        return this.matchContextThread;
    }
}
