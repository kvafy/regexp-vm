package cz.kvafy.regexp;

import java.util.ArrayList;

/** Series of instructions connected uni-directly via .next reference. */
class VMCode {
    VMInstruction firstInstruction;
    VMInstruction lastInstruction;
    
    private VMCode(VMInstruction inst) {
        this(inst, inst);
    }

    private VMCode(VMInstruction firstInst, VMInstruction lastInst) {
        // verify that there is a chain firstInst->lastInst and that this chain
        // ends by lastInst
        VMInstruction inst = firstInst;
        while(inst != lastInst) {
            if(inst.next == null)
                throw new IllegalArgumentException("lastInst isn't reachable from firstInst");
            inst = inst.next;
        }
        if(lastInst.next != null)
            throw new IllegalArgumentException("The last instruction " + lastInst.toString() + " has next instruction defined.");
        this.firstInstruction = firstInst;
        this.lastInstruction = lastInst;
    }
    
    /** Convert instructions of this code to list where [i].next == [i+1]. */
    private ArrayList<VMInstruction> getInstructionList() {
        ArrayList<VMInstruction> list = new ArrayList<VMInstruction>();
        VMInstruction inst = this.firstInstruction;
        while(inst != null) {
            list.add(inst);
            inst = inst.next;
        }
        return list;
    }
    
    /**
     * Creates a deep copy of the list, creating new instructions and preserving
     * their references via .next, .jmp1, .jmp2.
     */
    private static ArrayList<VMInstruction> duplicateInstructionListWithStructure(ArrayList<VMInstruction> instListOrig) {
        ArrayList<VMInstruction> result = new ArrayList<VMInstruction>();
        // duplicate instruction types, without references
        for(VMInstruction instOrig : instListOrig) {
            VMInstruction instCopy = new VMInstruction(instOrig.type, instOrig.literalSet, null, null, instOrig.groupNumber);
            result.add(instCopy);
        }
        // compute references (.next, .jmp1, .jmp2)
        for(int i = 0 ; i < instListOrig.size() ; i++) {
            VMInstruction instOrig = instListOrig.get(i);
            switch(instOrig.type) {
            case SPLIT:
                VMInstruction jmp2Target = instOrig.jmp2;
                int jmp2TargetIdx = instListOrig.indexOf(jmp2Target);
                result.get(i).jmp2 = result.get(jmp2TargetIdx);
                // fall through
            case JUMP:
                VMInstruction jmp1Target = instOrig.jmp1;
                int jmp1TargetIdx = instListOrig.indexOf(jmp1Target);
                result.get(i).jmp1 = result.get(jmp1TargetIdx);
                // fall through
            default:
                VMInstruction nextTarget = instOrig.next;
                if(nextTarget != null) {
                    int nextTargetIdx = instListOrig.indexOf(nextTarget);
                    result.get(i).next = result.get(nextTargetIdx);
                }
            }
        }
        return result;
    }
    
    public static VMCode codeForLiteralFromSet(String literals) {
        VMInstruction inst = new VMInstruction(VMInstruction.Type.LITERAL_FROM_SET, literals, null, null, -1);
        return new VMCode(inst);
    }
    
    public static VMCode codeForAnyCharacterMatch() {
        VMInstruction inst = new VMInstruction(VMInstruction.Type.ANY_CHAR, null, null, null, -1);
        return new VMCode(inst);
    }
    
    public static VMCode codeForSuccess() {
        VMInstruction inst = new VMInstruction(VMInstruction.Type.SUCCESS, null, null, null, -1);
        return new VMCode(inst);
    }
    
    public static VMCode codeForConcatenation(Object... codes) {
        // copy all the instructions into an array
        ArrayList<VMInstruction> instToCopy = new ArrayList<VMInstruction>();
        for(Object code : codes) {
            if(code instanceof VMInstruction) {
                if(((VMInstruction)code).next != null)
                    throw new IllegalArgumentException("Concatenating instructions that have defined .next: " + code);
                instToCopy.add((VMInstruction)code);
            }
            else if(code instanceof VMCode) {
                if(((VMCode)code).lastInstruction.next != null)
                    throw new IllegalStateException("Concatenating codes that don't end: " + code);
                instToCopy.addAll(((VMCode)code).getInstructionList());
            }
            else
                throw new IllegalArgumentException("Cannot work with parameter of type " + code.getClass().getCanonicalName());
        }
        // duplicate the instruction structure
        ArrayList<VMInstruction> instCopied = duplicateInstructionListWithStructure(instToCopy);
        // must ensure the chaining via .next
        for(int i = 1 ; i < instCopied.size() ; i++)
            instCopied.get(i-1).next = instCopied.get(i);
        return new VMCode(instCopied.get(0), instCopied.get(instCopied.size()-1));
    }
    
    public static VMCode codeForAlteration(VMCode code1, VMCode code2) {
        //     SPLIT L1, L2
        // L1:
        //     <code1>
        //     JMP L3
        // L2:
        //     <code2>
        // L3: 
        VMInstruction instL1 = new VMInstruction(VMInstruction.Type.LABEL, null, null, null, -1);
        VMInstruction instL2 = new VMInstruction(VMInstruction.Type.LABEL, null, null, null, -1);
        VMInstruction instL3 = new VMInstruction(VMInstruction.Type.LABEL, null, null, null, -1);
        VMInstruction instJmpL3 = new VMInstruction(VMInstruction.Type.JUMP, null, instL3, null, -1);
        VMInstruction instSplit = new VMInstruction(VMInstruction.Type.SPLIT, null, instL1, instL2, -1);
        return VMCode.codeForConcatenation(instSplit, instL1, code1, instJmpL3, instL2, code2, instL3);
    }
    
    public static VMCode codeForRepetitionStar(VMCode code, boolean greedy) {
        // L0:
        //     [greedy] SPLIT L1, L2  /  [not greedy] SPLIT L2, L1
        // L1:
        //     <code>
        //     JUMP L0
        // L2:
        VMInstruction instL0 = new VMInstruction(VMInstruction.Type.LABEL, null, null, null, -1);
        VMInstruction instL1 = new VMInstruction(VMInstruction.Type.LABEL, null, null, null, -1);
        VMInstruction instL2 = new VMInstruction(VMInstruction.Type.LABEL, null, null, null, -1);
        VMInstruction instJmpL0 = new VMInstruction(VMInstruction.Type.JUMP, null, instL0, null, -1);
        VMInstruction instSplit = greedy
                                ? new VMInstruction(VMInstruction.Type.SPLIT, null, instL1, instL2, -1)
                                : new VMInstruction(VMInstruction.Type.SPLIT, null, instL2, instL1, -1);
        return VMCode.codeForConcatenation(instL0, instSplit, instL1, code, instJmpL0, instL2);
    }
    
    public static VMCode codeForRepetitionPlus(VMCode code, boolean greedy) {
        // Kleene algebra: a+ = aa*
        VMCode repetitionStarCode = VMCode.codeForRepetitionStar(code, greedy);
        return VMCode.codeForConcatenation(code, repetitionStarCode);
    }
    
    public static VMCode codeForRepetitionQuestionmark(VMCode code, boolean greedy) {
        //     [greedy] SPLIT L0, L1  /  [nongreedy] SPLIT L1, L0
        // L0:
        //     <code>
        // L1:
        VMInstruction instL0 = new VMInstruction(VMInstruction.Type.LABEL, null, null, null, -1);
        VMInstruction instL1 = new VMInstruction(VMInstruction.Type.LABEL, null, null, null, -1);
        VMInstruction instSplit = greedy
                                ? new VMInstruction(VMInstruction.Type.SPLIT, null, instL0, instL1, -1)
                                : new VMInstruction(VMInstruction.Type.SPLIT, null, instL1, instL0, -1);
        return VMCode.codeForConcatenation(instSplit, instL0, code, instL1);
    }
    
    public static VMCode codeForSurroundingByCapturingGroup(VMCode code, int groupNumber) {
        // MARK group(<groupNumber>, begin)
        // <code>
        // MARK group(<groupNumber>, end)
        VMInstruction instMarkBegin = new VMInstruction(VMInstruction.Type.GROUP_BEGIN, null, null, null, groupNumber);
        VMInstruction instMarkEnd = new VMInstruction(VMInstruction.Type.GROUP_END, null, null, null, groupNumber);
        return VMCode.codeForConcatenation(instMarkBegin, code, instMarkEnd);
    }
    
    public static VMCode codeForBackreference(int groupNumber) {
        VMInstruction instBackreference = new VMInstruction(VMInstruction.Type.BACKREFERENCE, null, null, null, groupNumber);
        return new VMCode(instBackreference);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(VMCode.class.getSimpleName()).append(" ");
        VMInstruction inst = this.firstInstruction;
        while(inst != null) {
            if(inst != this.firstInstruction)
                sb.append(", ");
            sb.append(inst.toString());
            inst = inst.next;
        }
        sb.append(">");
        return sb.toString();
    }
}

class VMInstruction {
    Type type;
    VMInstruction next;
    String literalSet;
    VMInstruction jmp1;
    VMInstruction jmp2;
    int groupNumber;
    
    public static enum Type {
        SUCCESS,
        LITERAL_FROM_SET, ANY_CHAR,
        GROUP_BEGIN, GROUP_END, BACKREFERENCE,
        LABEL, SPLIT, JUMP
    };
    
    public VMInstruction(Type type, String literalSet, VMInstruction jmp1, VMInstruction jmp2, int groupNumber) {
        this.type = type;
        this.next = null;
        this.literalSet = literalSet;
        this.jmp1 = jmp1;
        this.jmp2 = jmp2;
        this.groupNumber = groupNumber;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(VMInstruction.class.getSimpleName()).append(" ");
        sb.append(this.type);
        switch(this.type) {
        case LITERAL_FROM_SET:
            sb.append(" \"").append(this.literalSet).append("\"");
            break;
        case GROUP_BEGIN:
        case GROUP_END:
            sb.append("(").append(this.groupNumber).append(")");
            break;
        }
        sb.append(">");
        return sb.toString();
    }
}