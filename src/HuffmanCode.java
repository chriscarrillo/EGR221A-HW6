import java.io.PrintStream;
import java.util.*;

/**
 * Created by Chris on 3/2/17.
 * HuffmanCode uses Huffman's algorithm to compress text files
 */
public class HuffmanCode {
    private HuffmanNode root; // the overallRoot of the tree
    private PriorityQueue<HuffmanNode> huff; // the PriorityQueue used to build the HuffmanCode

    /**
     * Initializes a new HuffmanCode from an array of frequencies. Calls helper method to reset the value of root
     * @param frequencies is an array of integers that contain all of the frequencies of the letters/chars
     */
    public HuffmanCode(int[] frequencies) {
        huff = new PriorityQueue<>();

        for (int i = 0; i < frequencies.length; i++) {
            if(frequencies[i] != 0) {
                huff.add(new HuffmanNode((char) i, frequencies[i]));
            }
        }
        this.root = constructorHelper(huff);
    }

    /**
     * Adds a new HuffmanNode that is combined of the left and right to huff
     * @param huff is the PriorityQueue of HuffmanNodes
     * @return HuffmanNode that the root is going to be set to
     */
    private HuffmanNode constructorHelper(PriorityQueue<HuffmanNode> huff){
        if(huff.size() == 1){
            return huff.remove();
        }

        HuffmanNode left = huff.remove();
        HuffmanNode right = huff.remove();
        HuffmanNode combined = new HuffmanNode('\0', left.frequency + right.frequency, left, right);
        huff.add(combined);
        return this.constructorHelper(huff);
    }

    /**
     * Initializes a new HuffmanCode from previously constructed .code file
     * @param input is a Scanner of file that it will read from
     */
    public HuffmanCode(Scanner input) {
        while (input.hasNextLine()) {
            int n = Integer.parseInt(input.nextLine());
            String code = input.nextLine();
            root = buildTree(n, code, this.root);
        }
    }

    /**
     * Builds the tree of HuffmanNodes
     * @param n is the ASCII value that is converted to a char
     * @param code is the String from the Scanner that is currently being analyzed
     * @param root is the current root of the HuffmanNode tree
     * @return HuffmanNode to reset the value of the overall root
     */
    private HuffmanNode buildTree(int n, String code, HuffmanNode root) {
        if (code.isEmpty()){
            return new HuffmanNode((char) n, -1);
        }

        if (root == null){
            root = new HuffmanNode('\0', -1);
        }
        if (code.startsWith("0")){
            root.left = buildTree(n, code.substring(1),root.left);
        } else {
            root.right = buildTree(n, code.substring(1), root.right);
        }
        return root;
    }

    /**
     * Stores the current huffman codes to the given output stream. Calls its helper method to do so
     * @param output is the PrintStream used to output information to the file
     */
    public void save(PrintStream output) {
        this.save(root, output, "");
    }

    /**
     * Stores the current huffman codes to the given output stream using recursion.
     * It will call itself to recurse through the left and right if necessary.
     * @param root is the HuffmanNode root
     * @param output is the PrintStream used to output information to the file
     * @param huffVal is either a 0 or 1 value
     */
    private void save(HuffmanNode root, PrintStream output, String huffVal) {
        if (root.left == null && root.right == null) { // no children, so this is a leaf
            output.println((int) root.data);
            output.println(huffVal);
        } else { // must have children, so recurse through the left and right
            save(root.left, output, huffVal + "0");
            save(root.right, output, huffVal + "1");
        }
    }

    /**
     * Reads individual bits from input and writes the corresponding characters to the output.
     * @param input is the BitInputStream that bits are going to be read from
     * @param output is the PrintStream that will be used to output the corresponding characters
     */
    public void translate(BitInputStream input, PrintStream output) {
        HuffmanNode current = this.root;

        while (input.hasNextBit()){ // keep reading as long as the input stream has a next bit
            if (current.left == null && current.right == null) { // there are no children, so leaf
                output.write(current.data);
                current = root;
            }

            int bit = input.nextBit();
            if (bit == 0) { // bit is 0, so left
                current = current.left;
            } else { // bit must be 1, so right
                current = current.right;
            }

            if (!input.hasNextBit()){ // doesn't have next bit, so output the current's data
                output.write(current.data);
            }
        }
    }

    /**
     * HuffmanNode represents a single node of the tree. It implements the Comparable interface
     * to compare one HuffmanNode with the other.
     */
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        public char data; // the character of the node
        public int frequency; // the frequency of the character
        public HuffmanNode left; // left node
        public HuffmanNode right; // right node

        // Constructs a HuffmanNode with given data and frequency
        public HuffmanNode(char data, int frequency) {
            this(data, frequency, null, null);
        }

        // Initializes the values of the HuffmanNode with the given parameters
        public HuffmanNode(char data, int frequency, HuffmanNode left, HuffmanNode right) {
            this.data = data;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        // Compares 2 HuffmanNodes's frequency to determine which one is greater.
        @Override
        public int compareTo(HuffmanNode other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }
}