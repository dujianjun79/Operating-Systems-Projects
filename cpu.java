import java.io.*;
import java.util.Scanner;

public class cpu 
{
    static int PC = 0; //program counter
    static int SP = 1000; //user stack,initialized at 1000
    static int IR; //instruction register
    static int AC; //accumulator
    static int X; //data register
    static int Y; //data register
    static int timeInterval; //the time interval for an interrupt, actually the counting of execution of instructions.
    static int countInstructions = 0; //count how many instructions have been executed.
    static int systemStack = 2000; //this is the start point of system stack, growing downward
    static int userStack = 1000;  //this is the start point of user stack,growing downward
    static boolean USER_MODE = true; //status if it is in user mode
    static boolean systemInterruption = false; // flag if there is a system interruption
    static Scanner in; //used for communication with memory
    static PrintWriter pw; //wrapping the output stream to memory
    static boolean stop=false; //flag if keep continuing fetch instruction,false means continuing fetch.
        
    public static void main(String args[])
    {        
        String fileName = args[0];   //get the file name from console
        timeInterval = Integer.parseInt(args[1]); //get the timeInterval setting from console        
        try
        {                        
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("java memory "+fileName); //send the file name to the args of memory.class

            OutputStream os = proc.getOutputStream(); //outputstream is the stream sending to memory
            pw = new PrintWriter(os);
            InputStream is = proc.getInputStream();   //inputstream is the stream coming to cpu
            in = new Scanner(is); 
           
            while (!stop)
            {               
                int instruct = fetchMemory(PC); //start to fetch instruction
                operateInstruction(instruct);   //explain the meaning of the instruction
                if(!systemInterruption&&(countInstructions%timeInterval)==0 ) //enter time interruption
                {
                    systemInterruption = true;
                    timeInterruption();
                } 
            }
            proc.waitFor();
            int exitVal = proc.exitValue();
            System.out.println("Process exited: " + exitVal);
        } 
        catch (Throwable t){t.printStackTrace();}

    }

    // fetch data or instruction from memory
    private static int fetchMemory(int address) 
    {
        invadeKernel(address); //check if the address is in the user memory area
        pw.printf("r," + address + "\n"); //use 'r,' to signal the reading from memory
        pw.flush();
        String tmp1 = in.next(); //accept the value from memory
        return Integer.parseInt(tmp1); 
    }
    
    //write to the designated memory area, either is the user area or the kernel area
    private static void writeMemory(int address, int data) {
        pw.printf("w," + address + "," + data + "\n"); //use 'w,' to signal the writing to memroy
        pw.flush();
    }
    
    // check if user is accessing system memory area
    private static void invadeKernel(int address) 
    {
        if(address > 1000&&USER_MODE==true)
        {
            System.out.println("warnig: accessing the kernel area, exits.");
            System.exit(0);
        }        
    }

    // push a value to stack
    private static void pushStack(int value) 
    {
        SP--;
        writeMemory(SP, value);
    }

    // pop value from a stack
    private static int popStack() 
    {
        int tmp = fetchMemory(SP);
        SP++;
        return tmp;
    }
    
    // when the process is interrupted from the timeInterval, this is the function to handle it.
    private static void timeInterruption() 
    {
        int tmp;
        USER_MODE = false; //first set to the kernel mode
        tmp = SP;
        SP = systemStack;
        pushStack(tmp);   //store the user stack position to the system stack
        tmp = PC;
        PC = 1000;        //ready to fetch the instructions from address 1000
        pushStack(tmp);  //store the user PC to the system stack  
    }
	
    private static void instructionAdder() //count the instructions which have been executed. 
    {
           if(!systemInterruption) countInstructions++; //only count the user instructions
    }
	
    // process the instructions and data fetched from memory
    private static void operateInstruction(int instruct) 
    {
        instructionAdder(); 
        IR = instruct; //store the instruction value to the instruction register
        int localVar;    //local variable to store localVar
        
        switch(IR)
        {
            //Load the value into the AC
            case 1: 
                PC++; //Because the value is in the following line of the instruction, you need to increment PC first.
                AC = fetchMemory(PC);         
                PC++;
                break;
            // Load the value at the address into the AC    
            case 2: 
                PC++;
                AC = fetchMemory(fetchMemory(PC));
                PC++;
                break;
            // Load the value from the address found in the address into the AC
            case 3: 
                PC++;
                localVar = fetchMemory(PC);
                localVar = fetchMemory(localVar);
                AC = fetchMemory(localVar);
                PC++;
                break;                
            // Load the value at (address+X) into the AC    
            case 4: 
                PC++;
                localVar = fetchMemory(PC);
                AC = fetchMemory(localVar + X);
                PC++;
                break;
            //Load the value at (address+Y) into the AC    
            case 5: 
                PC++;
                localVar = fetchMemory(PC);
                AC = fetchMemory(localVar + Y);
                PC++;
                break;
            //Load from (SP+X) into the AC    
            case 6: 
                AC = fetchMemory(SP + X);
                PC++;
                break;
            //Store the value in the AC into the address    
            case 7: 
                PC++;
                localVar = fetchMemory(PC);
                writeMemory(localVar, AC);
                PC++;
                break;
            //Get a random int from 1 to 100 into the AC    
            case 8: 
                AC = (int )(Math.random()*100 + 1); 
                PC++;
                break;
            //If port=1, writes AC as an int to the screen
            //If port=2, writes AC as a char to the screen    
            case 9: 
                PC++;
                localVar = fetchMemory(PC);
                if(localVar == 1) System.out.print(AC);                    
                else System.out.print((char)AC);              
	        PC++;
                break;
            // Add the value in X to the AC    
            case 10: 
                AC = AC + X;
                PC++;
                break;
            //Add the value in Y to the AC    
            case 11: 
                AC = AC + Y;
                PC++;
                break;
            //Subtract the value in X from the AC    
            case 12: 
                AC = AC - X;
                PC++;
                break;
            case 13: //Subtract the value in Y from the AC
                AC = AC - Y;
                PC++;
                break;
            //Copy the value in the AC to X    
            case 14: 
                X = AC;
                PC++;
                break;
            //Copy the value in X to the AC    
            case 15: 
                AC = X;
                PC++;
                break;
            //Copy the value in the AC to Y    
            case 16: 
                Y = AC;
                PC++;
                break;               
            //Copy the value in Y to the AC    
            case 17: 
                AC = Y;
                PC++;
                break;
            //Copy the value in AC to the SP    
            case 18: 
                SP = AC;
                PC++;
                break;
            //Copy the value in SP to the AC    
            case 19:  
                AC = SP;
                PC++;
                break;
            // Jump to the address    
            case 20: 
                PC++;
                localVar = fetchMemory(PC);
                PC = localVar;
                break;
            // Jump to the address only if the value in the AC is zero    
            case 21: 
                PC++;
                localVar = fetchMemory(PC);
                if (AC == 0) 
                {
                    PC = localVar;
                    break;
                }
                PC++;
                break;                
            // Jump to the address only if the value in the AC is not zero    
            case 22: 
                PC++;
                localVar = fetchMemory(PC);
                if (AC != 0) 
                {
                    PC = localVar;
                    break;
                }                
                PC++;
                break;
            //Push return address onto stack, jump to the address    
            case 23: 
                PC++;
                localVar = fetchMemory(PC);
                pushStack(PC+1);
                userStack = SP;
                PC = localVar;
                break;                
            //Pop return address from the stack, jump to the address    
            case 24: 
                localVar = popStack();
                PC = localVar;
                break;
            //Increment the value in X    
            case 25: 
                X++;
                PC++;
                break;
            //Decrement the value in X
            case 26: 
                X--;
                PC++;
                break;
            // Push AC onto stack
            case 27: 
                pushStack(AC);
                PC++;
                break;
            //Pop from stack into AC    
            case 28: 
                AC = popStack();
                PC++;
                break;
            // Int call. Set system mode, switch stack, push SP and PC, set new SP and PC    
            case 29:                 
                systemInterruption = true;
                USER_MODE = false;
                localVar = SP;
                SP = 2000;
                pushStack(localVar);                
                localVar = PC + 1;
                PC = 1500;
                pushStack(localVar);                
                break;
            //Restore registers, set user mode    
            case 30:                 
                PC = popStack();
                SP = popStack();
                USER_MODE = true;
                countInstructions++;
                systemInterruption = false;
                break;
            // quit from the process    
            case 50: 
                stop=true;
                System.exit(0);
                break;
            //in case of different instructions system does not recognize
            default:
                System.out.println("Instruction is illegal.");
                System.exit(0);
                break;        
        }
    }
}

