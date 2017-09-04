
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class memory
{   
    static int [] memorySpace = new int[2000];  //establish the memory space
    
    static void extractfile(String path) throws IOException {  //extract the instruction from the file
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String Line;
        int Code; //extracted intruction Code from the Line
        int flag; // this is to decide if the instruction is starting with '.'. If it starts with '.', change it to a minus number.
        int address=0; //this is handling the address.
        while((Line=br.readLine())!=null){// In the case of blank line, do not count it as an instruction.
            int status =0; //indicate if there is number in the line. If there is no integer in the line, go to the next line.
            for(int i=0; i<Line.length();i++){
                if(Line.charAt(i)=='/') break;
                if(Line.charAt(i)>='0'&&Line.charAt(i)<='9') status=1;
            }
            if(status==0) continue;
            Code=0;
            flag=1;
            int i=0; //iteration index of string Line 
            if (Line.length()!=0&&Line.charAt(0)!='\r'){
                while(i<Line.length()&&Line.charAt(i)!='\n'){
                    if(Line.charAt(i)=='/'||(Line.charAt(i)>='A'&&Line.charAt(i)<='z')) break;  // In the case there is comment, jump out of the inner loop.
                    if(Line.charAt(i)=='.') flag=-1;   //label the '.', change it to a negative number
                    if(Line.charAt(i)>='0'&&Line.charAt(i)<='9'){ //this is transferring char to integer    
                        Code = Code*10+ (Line.charAt(i)-'0');
                    }
                    i++;
                }
                Code=Code*flag;
                if(Code>0||(Code==0&&flag==1)) memorySpace[address++]=Code;
                else address=-Code;     
            }
        }
    }
     
    public static void read(int address) //read from the memory space, and send it to CPU
    {
        System.out.println(memorySpace[address]);
    }
    
    public static void write(int address, int data)  //write the data from CPU to the memory address
    {
        memorySpace[address] = data;
    }
    
    public static void main(String args[]) throws IOException
    {
        try
        {
            String file_name=args[0];
            extractfile(file_name);
            /* for(int element:memorySpace){
                System.out.print(element+" ");
            } */
            Scanner in = new Scanner(System.in);      //scan the input from CPU.
                        
            while(in.hasNext()){                     //when the input from CPU comes in
                String line=in.nextLine();           //assign the input to a string
                int address;                          
                int data;
                String[] command = line.split(",");   
                if(command[0].equals("r"))      // when it is a read instruction
                    { address = Integer.parseInt(command[1]);  //fetch the value from the address
                      read(address);}                        
                else{
                      address = Integer.parseInt(command[1]);  //when it is a write instruction
                      data = Integer.parseInt(command[2]);     // get data and address from the iuput, and write the data to the address of memory space.
                      write(address,data);
                    }                
            }
        } catch(Throwable t){t.printStackTrace();}
    }        
}
