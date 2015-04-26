package cz.kvafy.regexp;

public class Matcher {
    /** Input string to be matched. */
    private String input;
    /** Regular expression compiled into VM code. */
    private VMCode vmCode;
    
    /**
     * After a successful match, this VM thread holds context of the match
     * (ie. position in the input where the VM stopped, capturing groups'
     * positions, ...).
     *  */
    private VMThread matchContextVMThread = null;
    /** At which index in the input string should the next invocation of #find() start. */
    private int findBeginPosition = 0;
    
    
    Matcher(String input, VMCode vmCode) {
        this.input = input;
        this.vmCode = vmCode;
    }
    
    public boolean matches() {
        // initialize matching context
        VM vm = new VM(this.vmCode, input, 0);
        while(!vm.hasNoThreadToRun()) {
            boolean prefixMatchSuccesfull = vm.run();
            if(prefixMatchSuccesfull) {
                VMThread vmThread = vm.getMatchContextThread();
                boolean wholeInputMatched = vmThread.inputPos == input.length();
                if(wholeInputMatched) {
                    this.matchContextVMThread = vmThread;
                    return true;
                }
            }
        }
        // failed to match the whole input
        this.matchContextVMThread = null;
        return false;
    }
    
    public boolean find() {
        while(this.findBeginPosition < this.input.length()) {
            VM vm = new VM(this.vmCode, input, findBeginPosition);
            boolean vmStoppedWithSuccess = vm.run();
            if(vmStoppedWithSuccess) {
                this.matchContextVMThread = vm.getMatchContextThread();
                // next time, attempt to find next non-overlapping match
                this.findBeginPosition = this.matchContextVMThread.groupEndMap.get(0);
                return true;
            }
            else
                this.findBeginPosition++;
        }
        // failed to find any next match somewhere within the input
        this.matchContextVMThread = null;
        return false;
    }
    
    /**
     * Return the portion of the input string that was matched by last
     * {@link #matches()} or {@link #find()}.
     */
    public String group() {
        return group(0);
    }
    
    /**
     * Return the portion of the input string that was matched by last
     * {@link #matches()} or {@link #find()} and saved into capturing group
     * <arg>number</arg>.
     * @param number
     *     Number of the capturing group whose content we want to obtain.
     *     Group 0 stands for the whole string that was matched.
     * @throws IllegalStateException
     *     When last {@link #matches()} or {@link #find()} was unsuccessful
     *     or when the was no invocation to {@link #matches()} or {@link #find()}
     *     at all.
     */
    public String group(int number) {
        if(this.matchContextVMThread == null)
            throw new IllegalStateException("No justification for calling group([i]) method, when it is not preceeded by a successful match.");
        
        Integer groupBegin = this.matchContextVMThread.groupBeginMap.get(number);
        Integer groupEnd = this.matchContextVMThread.groupEndMap.get(number);
        
        return (groupBegin != null && groupEnd != null)
               ? this.input.substring(groupBegin, groupEnd)
               : "";
    }
}
