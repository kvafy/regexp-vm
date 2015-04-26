package cz.kvafy.regexp;

import java.util.HashMap;

class VMThread {
    /** The left-most unprocessed character of the input string. */
    int inputPos;
    /** Current instruction. */
    VMInstruction currentInst;
    /** Map: <group number> -> <begin of the group in the input string> */
    HashMap<Integer, Integer> groupBeginMap;
    /** Map: <group number> -> <end of the group in the input string> */
    HashMap<Integer, Integer> groupEndMap;
    
    public VMThread(int positionInInput,
                    VMInstruction instCurrent,
                    HashMap<Integer, Integer> groupBeginMap,
                    HashMap<Integer, Integer> groupEndMap) {
        if(groupBeginMap == null)
            groupBeginMap = new HashMap<Integer, Integer>();
        if(groupEndMap == null)
            groupEndMap = new HashMap<Integer, Integer>();
        this.inputPos = positionInInput;
        this.currentInst = instCurrent;
        this.groupBeginMap = new HashMap<Integer, Integer>(groupBeginMap);
        this.groupEndMap = new HashMap<Integer, Integer>(groupEndMap);
    }
}
