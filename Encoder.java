/**
 * PS3, CS10, Dartmouth College Fall 2022
 * @author Aryan Dawer
 */

import java.io.*;
import java.util.*;

public class Encoder {
    private static String pathName;  //Original File
    private static String compressedPathName; // Compressed File
    private static String decompressedPathName; // Decompressed File
    private static Map<Character, Integer> frequencyDict; //Hashmap mapping characters to frequency of occurrence
    private static PriorityQueue<BinaryTree<Data>> queueOfCharacterTrees; // Priority Queue of Binary trees of all characters
    private static BinaryTree<Data> bigTree; //Big Binary tree with all binary trees in priority of occurrence
    private static Map<Character, String> myCodeMap; // Hashmap mapping each character to its specific code from the big binary tree
    private static TreeComparator dataCompare; // comparator object for priority queue

    /**
     * Class for each node of binary tree
     */
    public static class Data {
        private Character character; // stores character
        private int frequency; // stores frequency

        // Getters
        public int getFrequency() {
            return frequency;
        }
        public Character getCharacter() {
            return character;
        }

        // Constructor
        public Data(Character character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        // Returns freq of object in string form when called: Used to print binary tree of frequencies like in the PS instructions
        @Override
        public String toString() { // Just for testing—To print the big binary tree
            return String.valueOf(frequency);
        }
    }

    /**
     * Comparator class for Comparing 2 Character Binary Trees based on frequency
     * @param <T>
     */
    public static class TreeComparator<T> implements Comparator<BinaryTree<T>> {
        public int compare(BinaryTree<T> tree1, BinaryTree<T> tree2) {
            Data data1 = (Data) tree1.getData();
            Data data2 = (Data) tree2.getData();

            if (data1.getFrequency() < data2.getFrequency())
                return -1;
            else if (data1.getFrequency() > data2.getFrequency())
                return 1;
            else {
                return 0;
            }
        }
    }

    /**
     * Makes a Hashmap mapping characters to frequency of occurrence
     * @param pathName original file
     */
    public static void makeDictionary(String pathName) throws IOException {
        frequencyDict = new HashMap<>();
        BufferedReader inp = null;
        int curr = 0;
        try {
            inp = new BufferedReader(new FileReader(pathName)); // Read in the original file
            // Look through file char by char
            curr = inp.read();
            while (curr != -1) {
                if (frequencyDict.containsKey((char) curr)) { // adds to value of frequency if key exists
                    frequencyDict.put((char) curr, frequencyDict.get((char) curr) + 1);
                } else { // adds key with frequency 1 if key doesn't exist
                    frequencyDict.put((char) curr, 1);
                }
                curr = inp.read();
                }
            }
        catch(IOException e) {
                System.out.println("Error Found!");
            }
        finally {
            try{
                inp.close(); //close file
            }
            catch(IOException e) {
                System.out.println("Error Found!");
            }
        }
    }

    /**
     * Generates a Priority Queue of trees with one node for each character
     */
    public static void makePriorityQueue() {
        if(frequencyDict.size()==0) return; // Empty case
        dataCompare = new TreeComparator<>(); //Make comparator object for sorting queue
        queueOfCharacterTrees = new PriorityQueue<>(dataCompare); //initialize queue
        for (Character character : frequencyDict.keySet()) { // iterate via keys and add trees sorted on basis of comparator
            queueOfCharacterTrees.add(new BinaryTree<Data>(new Data(character, frequencyDict.get(character))));
        }
    }

    /**
     * Generates Big Binary tree with all binary trees in priority of occurrence
     */
    public static void makeOneBigTree() {
        if(frequencyDict.size()==0) return; // Empty case
        bigTree = null;
        while (queueOfCharacterTrees.size() != 1 && queueOfCharacterTrees.size() != 0) { // As long as queue has 2 elements
            BinaryTree<Data> tree1 = queueOfCharacterTrees.remove(); //remove lowest freq
            BinaryTree<Data> tree2 = queueOfCharacterTrees.remove(); //remove second lowest freq
            int sum = tree1.getData().getFrequency() + tree2.getData().getFrequency(); //get sum of their freq
            BinaryTree<Data> tree = new BinaryTree<Data>(new Data(null, sum), tree1, tree2);
            queueOfCharacterTrees.add(tree); // add null node with that sum to queue
        }
        bigTree = queueOfCharacterTrees.peek(); // update static variable bigTree to new Binary tree with all chars binary trees
    }

    /**
     * Generates Hashmap mapping each character to its specific code from the big binary tree
     * @param tree binary tree with all binary trees in priority of occurrence
     * @return myCodeMap
     */
    public static Map<Character, String> makeCodeMap(BinaryTree<Data> tree) {
        Map<Character, String> charToCode = new HashMap<Character, String>();
        if (tree == null) { // Empty case
            return charToCode;
        }
        if (tree.size() == 1) { // Single char case
            String s = "0";
            charToCode.put(tree.getData().getCharacter(), s);
            return charToCode;
        } else { //other case that implements recursive helper
            String s = "";
            makeCodeMaphelper(charToCode, tree, s);
            return charToCode;
        }
    }

    /**
     * Helper method for making CodeMap recursively
     * @param charToCode Mapping character to its code in the tree
     * @param tree the bigTree made by makeOneBigTree()
     * @param s string that keeps track of code to be added to charToCode
     */
    public static void makeCodeMaphelper(Map<Character, String> charToCode, BinaryTree<Data> tree, String s) {

        if (tree.isLeaf()) { // if dead end reached, map the resultant code to the character found there
            charToCode.put(tree.getData().getCharacter(), s);
        }
        if (tree.hasLeft()) {// if left node exist, add 0 to code
            makeCodeMaphelper(charToCode, tree.getLeft(), s + "0");
        }
        if (tree.hasRight()) {// if right node exist, add 1 to code
            makeCodeMaphelper(charToCode, tree.getRight(), s + "1");
        }
    }

    /**
     * Method to decompress the compressed file
     * @param compressedPathName Compressed file path
     * @param decompressedPathName Decompressed file path
     * @param tree the bigTree made by makeOneBigTree()
     */
    public static void decompress(String compressedPathName, String decompressedPathName, BinaryTree<Data> tree) throws IOException {
        BufferedBitReader bitInput = null;
        BufferedWriter output = null;
        try {
            bitInput = new BufferedBitReader(compressedPathName); //Create new BitReader object
            output = new BufferedWriter(new FileWriter(decompressedPathName)); //Create new Writer object

            while (bitInput.hasNext()) { // As Long as file not empty
                // Read compressed file bit by bit
                boolean bit = bitInput.readBit();

                // Special Case
                if (myCodeMap.size() == 1) { // one unique char/repeated unique char special case
                    output.write(myCodeMap.keySet().iterator().next()); // gets that char from codemap as many times the code appears
                    continue; // makes sure that it only calls this iff statement in special case
                }

                if (bit == true) {
                    //If dead end found, write the character mapped to code at that point
                    if (tree.getRight().isLeaf()) {
                        char character = tree.getRight().getData().getCharacter();
                        output.write(character);
                        tree = queueOfCharacterTrees.peek();
                    }
                    else{ // else go right coz bit true
                        tree = tree.getRight();
                    }
                } //If dead end found, write the character mapped to code at that point
                else if (bit == false) {
                    if (tree.getLeft().isLeaf()) {
                        char character = tree.getLeft().getData().getCharacter();
                        output.write(character);
                        tree = queueOfCharacterTrees.peek();
                    }
                    else { // else go left coz bit false
                        tree = tree.getLeft();
                    }
                }
            }
        }
        catch(IOException e){
            System.out.println("Error Found!");
        }
        finally{
            try{ // close files
                output.close();
                bitInput.close();
            }
            catch(IOException e){
                System.out.println("Error found!");
            }
        }

    }

    /**
     *
     * @param pathName Original file
     * @param charToCode Mapping character to its code in the tree
     * @param compressedPathName Compressed file path
     */
    public static void compress(String pathName, Map<Character, String> charToCode, String compressedPathName) throws IOException {
        if(frequencyDict.size()==0) return;
        BufferedReader inp = null;
        BufferedBitWriter bitOutput = null;
        int curr = 0;
        try {
            inp = new BufferedReader(new FileReader(pathName)); //Create new Reader object
            bitOutput = new BufferedBitWriter(compressedPathName); //Create new BitWriter object
            // Goes through file char by char
            curr = inp.read();
            while (curr != -1) {
                String code = charToCode.get((char) curr); // gets code for current char
                for (char bit : code.toCharArray()) { // goes through each bit in code one by one and writes them as booleans in compressed file
                    if (bit == '1') {
                        bitOutput.writeBit(true);
                    } else {
                        bitOutput.writeBit(false);
                    }
                }
                curr = inp.read();
            }
        }
        catch (IOException e) {
                System.out.println("Error Found!");
        }
        finally{
            try{ // close files
                inp.close();
                bitOutput.close();
            }
            catch(IOException e){
                System.out.println("Error Found!");
            }
        }
    }

    /**
     * Tester Function changes path variables to test for different boundary cases
     * @param testCase iterates from 0-5 and changes test case everytime
     */
    public static void test(int testCase) {
        String src = "/Users/aryandawer/Documents/IdeaProjects/cs10/cs10/ps3/";
        String inp = "inputs/";
        String out = "outputs/";
        String end = ".txt";
        String compressEnd = "_compressed.txt";
        String decompressEnd = "_decompressed.txt";
        if (testCase == 0) {
            pathName = src + inp + "emptyTester" + end;
            compressedPathName = src + out + "emptyTester" + compressEnd;
            decompressedPathName = src + out + "emptyTester" + decompressEnd;
        }
        if (testCase == 1) {
            pathName = src + inp + "hello" + end;
            compressedPathName = src + out  + "hello" + compressEnd;
            decompressedPathName = src + out  + "hello" + decompressEnd;
        }
        if (testCase == 2) {
            pathName = src + inp + "single" + end;
            compressedPathName = src + out  + "single" + compressEnd;
            decompressedPathName = src + out  + "single" + decompressEnd;
        }
        if (testCase == 3) {
            pathName = src + inp + "repeatedLetter" + end;
            compressedPathName = src + out  + "repeatedLetter" + compressEnd;
            decompressedPathName = src + out  + "repeatedLetter" + decompressEnd;
        }
        if (testCase == 4) {
            pathName = src + inp + "USConstitution" + end;
            compressedPathName = src + out  + "USConstitution" + compressEnd;
            decompressedPathName = src + out  + "USConstitution" + decompressEnd;
        }
        if (testCase == 5) {
            pathName = src + inp + "WarAndPeace" + end;
            compressedPathName = src + out  + "WarAndPeace" + compressEnd;
            decompressedPathName = src + out  + "WarAndPeace" + decompressEnd;
        }
    }

    /**
     * Main method to test and compression and decompression
     */

    public static void main(String[] args) {
        for (int i = 0; i < 6; i++) {
            test(i); // loop calls function with test case i
            try {
                makeDictionary(pathName);
//---- Checking while writing code
//                System.out.println(frequencyDict.size());
//                System.out.println(frequencyDict);
                makePriorityQueue();
                makeOneBigTree();
//---- Checking while writing code
//                if (bigTree != null) {
//                    System.out.println(bigTree.toString());
//                }
                if(bigTree!=null) {
                    myCodeMap = makeCodeMap(bigTree);
                }
//---- Checking while writing code
//                if (myCodeMap != null) {
//                    System.out.println(myCodeMap);
//                }
                if(myCodeMap!=null) {
                    compress(pathName, myCodeMap, compressedPathName);
                }
                if(bigTree!=null) {
                    decompress(compressedPathName, decompressedPathName, bigTree);
                }
            } catch (IOException e) {
                System.out.println("Error Found!");
            }
        }
    }
}