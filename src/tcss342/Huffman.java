/*
 * Nwoke Fortune Chiemeziem
 * Huffman Encoding
 * TCSS 342 - Spring 2015
 * 
 */
package tcss342;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This class compresses a file using the huffam encoding algorithm
 * @author Chiemeziem Fortune Nwoke
 * @version 1.0
 */
public class Huffman {
	
	private static Scanner myScanner;
	private static Map<Character, Integer> myMap;
	
	private static Node myRoot;
	private static StringBuilder sb;
	private static Map<Character, String> encodingMap;
	private static Character[] array;
	
	/**
	 * Goes through the text and puts the every char in the map with its freq.
	 * @param path the text to be parsed.
	 * @return the map containing the character and its frequencies.
	 */
	public static Map<Character, Integer> getCharacterFrequencies(String path) {
		try
		{
			myScanner = new Scanner(new FileInputStream(path));
			myMap = new HashMap<>();
			int newLine = 1;
			while (myScanner.hasNextLine()) {
				String line = myScanner.nextLine();
				//for the new line character
				if (line.isEmpty()) {
					newLine++;
					myMap.put('\n', newLine);
					
				} else {
					getFrequencies(line);//gets the frequencies of char in the line.
				}
			}	
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}	
		finally {
			myScanner.close();
		}
		return myMap;
	}
	
	/**
	 * Gets the frequency of each char in each line and stores it in the map
	 * @param theLine the line to be checked.
	 */
	private static void getFrequencies(String theLine)
	{
		for (int i = 0; i< theLine.length(); i++) {
			
			if(myMap.containsKey(theLine.charAt(i))) {
				int temp = myMap.get(theLine.charAt(i));
				
				myMap.replace(theLine.charAt(i), new Integer(++temp));
			}
			else {
				myMap.put(theLine.charAt(i), 1);
			}
			
		}
	}

	/**
	 * Build the tree with each. The map containing each character with its binary.
	 * @param frequencies the map containing the char and its freq.
	 * @return the maps containing the char and its binary representation.
	 */
	public static Map<Character, String> getEncoding(Map<Character, Integer> frequencies) {
		encodingMap = new HashMap<>();
		sb = new StringBuilder();
		array = new Character[frequencies.size()];
		frequencies.keySet().toArray(array);
		
		
		List<Node> myNodes = new ArrayList<>();
		for (int i = 0; i< frequencies.size(); i++) {
			myNodes.add(new Node(array[i]+"", frequencies.get(array[i]), null, null));
		}
		Collections.sort(myNodes);

		myRoot = buildHuffsTree(myNodes).get(0);
		//System.out.println(myRoot.toString());
		getEncoding(myRoot);
		return encodingMap;
	}
	
	/**
	 * This method assigns each character a binary representation.
	 * @param myRoot the root of the tree.
	 * @param sb the string builder representing
	 */
	private static void getEncoding(Node myRoot) {
		if (myRoot != null) {
			if (myRoot.left == null) {
				encodingMap.put(myRoot.data.charAt(0), sb.toString());
				
			} else {
				
				sb.append('0');
				getEncoding(myRoot.left);
				sb.deleteCharAt(sb.length()-1);
				
				sb.append('1');
				getEncoding(myRoot.right);
				sb.deleteCharAt(sb.length()-1);
				
			}
		}	 
	}
	
	/**
	 * Keeps reducing the size of the array and combining the smallest one, then returns the root.
	 * @param myNodes the list of nodes.
	 * @return the root.
	 */
	private static List<Node> buildHuffsTree(List<Node> myNodes)
	{
		long a = System.currentTimeMillis();
		while(myNodes.size() > 1) {
			Node m =  new Node(myNodes.get(0).data + myNodes.get(1).data,
					myNodes.get(0).myTotal + myNodes.get(1).myTotal, myNodes.get(1) ,myNodes.get(0));
			
			myNodes.set(1, m);	
			myNodes.remove(0);
			
			Collections.sort(myNodes);
		}
		long b = System.currentTimeMillis();
		System.out.println(b - a);
		return myNodes;
	}

	/**
	 * get the compression ratio. 
	 * @param frequencies map of chars and their frequency
	 * @param encoding map of char and their binary
	 * @return the compression ratio
	 */
	public static double getCompressionRatio(Map<Character, Integer> frequencies, Map<Character, String> encoding) {
		
		StringBuilder sb = new StringBuilder();
		int myTotal = 0;
		for (int i = 0; i < frequencies.size(); i++) {
			myTotal += frequencies.get(array[i]);
		}
		
		myTotal *= 8;
		
		for (int i = 0; i< encoding.size(); i++) {
			int rounds = frequencies.get(array[i]);
			
			for (int j = 0; j < rounds; j++) {
				sb.append(encoding.get(array[i]));
			}
			
		}
	
		return ((double)myTotal)/sb.toString().length();
	}
	
	public static class Node implements Comparable {
		private String data;
		private int myTotal;
		private Node left;
		private Node right;
		
		public Node(String data, int myTotal, Node left, Node right) {
			this.data = data;
			this.left = left;
			this.right = right;
			this.myTotal = myTotal;
		}
		
		public String toString() {
			return data;
		}

		@Override
		public int compareTo(Object other)
		{
			if (other instanceof Node) {
		
				return myTotal - ((Node)other).myTotal;
			}
			return -1;
		}
	}
}
