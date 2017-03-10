package interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

/**
 *
 * @author anuska
 */
public class Interpreter {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        if(args.length < 3){
            System.out.println("Zadany maly pocet argumentov\n");
            return;
        }

        File    sourceFile = new File(args[0]),
                inputFile = new File(args[1]);
        
        if(sourceFile.exists() && !sourceFile.isDirectory()) { 
            System.out.println("Zadany source file neexistuje\n");
            return;
        }
        if(inputFile.exists() && !inputFile.isDirectory()){
            System.out.println("Zadany input file neexistuje\n");
            return;
        }
        
        /*Path path = Paths.get(args[0]);*/
        /*Input to byte array*/
        //Path path = Paths.get("testy/program1/input.bin");
        
        /*Source to String List*/
        //List<String> source = Files.readAllLines(Paths.get("testy/program1/source.txt"), Charset.defaultCharset());
        String source = new Scanner(new File("testy/program4/source.txt")).next();
        List<String> instructions = parseInstruction(source);
        //interprete(instructions, input);
        interpret(instructions);
        
        //System.in.read();

    }
    
    public static List<String> parseInstruction(String source){
        List<String> instructions = new ArrayList<String>();
        for(int i = 0; i < source.length(); i += 4){
            instructions.add(source.substring(i, i+4));
        }
        return instructions;
    }
    
    public static boolean interpret(List<String> instructions) throws FileNotFoundException, IOException{
        FileInputStream inputFile = new FileInputStream("testy/program4/input.bin");
        
        int instructionsCnt = instructions.size();
        int pointer = 0;
        
        int occ1A = Collections.frequency(instructions, "0110");
        int occ1B = Collections.frequency(instructions, "0111");
        
        if(occ1A != occ1B){
            System.out.println("ERR : Chyba parovych znaciek!");
            return false;
        }
        byte[] code = new byte[100000];
        byte x = -1;
        
        //for (String inst: instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            switch(instructions.get(i)){
                case "0000":
                    pointer++;
                    break;
                case "0001":
                    pointer--;
                    break; 
                case "0010":
                    code[pointer]++;
                    break;
                case "0011":
                    code[pointer]--;
                    break;
                case "0100":
                    System.out.println((char)code[pointer]);
                    break;
                case "0101":
                    if((x = (byte) inputFile.read()) == -1){
                        System.out.println("ERR : Chyba citania, nic viac na vstupe");
                        return false;
                    }
                    code[pointer] = x;
                    break;
                case "0110":
                    int cnt = 1;
                    if(code[pointer] == 0){
                        do{
                            i++;
                            if(Objects.equals(instructions.get(i),"0110"))
                                cnt++;
                            else if(Objects.equals(instructions.get(i),"0111"))
                                cnt--;
                        }
                        while( cnt != 0);
                    }
                    break;
                case "0111":
                    int cnt2 = 1;
                    if(code[pointer] != 0){
                        do{
                            i--;
                            if(Objects.equals(instructions.get(i), "0111"))
                                cnt2++;
                            else if(Objects.equals(instructions.get(i), "0110"))
                                cnt2--;
                        }
                        while( cnt2 != 0 );
                    }
                    break;
                case "1000":
                    if((i+1) >= instructionsCnt){
                        System.out.println("ERR : Za 1000 nenasleduje ziadna dalsia instrukcia.");
                        return false;
                    }
                    code[pointer] += Integer.parseInt(instructions.get(i+1), 2);
                    break;
                case "1001":
                    if((i+1) >= instructionsCnt){
                        System.out.println("ERR : Za 1001 nenasleduje ziadna dalsia instrukcia.");
                        return false;
                    }
                    code[pointer] -= Integer.parseInt(instructions.get(i+1), 2);
                    break;
                case "1010":
                    /*NOPE*/
                    break;
                case "1011":
                    code[pointer] = 0x00;
                    break;
                case "1100":
                    pointer = 0;
                    break;
                default:
                    System.out.println("ERR : Neznama instrukcia");
                    return false;
            }
        }
        inputFile.close();
        return true;
    }
}
