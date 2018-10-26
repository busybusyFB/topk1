import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TopKSelector {

	public static void main(String[] args) {
		String inputFilePath = args[0];
		String outputOccupPath = args[1];
		String outputStatePath = args[2];
		TopKSelector selector = new TopKSelector();
		selector.selectTopK(inputFilePath, outputStatePath, outputOccupPath, 10);
	}
	
	public boolean selectTopK(String inputFilePath, String outputStatePath, String outputOccupPath, int numK)	{
		try {
			//read contents from input.csv
			BufferedReader bfreader = new BufferedReader(new FileReader(inputFilePath));
			String header = bfreader.readLine();
			if (header == null) { //empty file
				bfreader.close();
				System.out.println("Input file is empty");
				return false;
			}
			int t1 = (int) System.currentTimeMillis();
			//Analyze the locations of status, work state and occupation name in the entry
			String[] attributes = header.split(";");
			int StatusCol = -1;
			int WorkStateCol = -1;
			int SOCnameCol = -1;
			for (int i = 0; i < attributes.length; i++) {
				String attr = attributes[i].toLowerCase();
				if (attr.contains("status") && StatusCol == -1) {
					StatusCol = i;
				}
				if (attr.contains("soc_name") && SOCnameCol == -1) {
					SOCnameCol = i;
				}
				if (attr.contains("work") && attr.contains("state") && WorkStateCol == -1) {
					WorkStateCol = i;
				}
			}
			
			int certifiedNum = 0;
			//Use hash tables to store certified states and jobs and their counts
			Map<String, Integer> states = new HashMap<>();
			Map<String, Integer> jobs = new HashMap<>();
			
			String line = new String();
			//read cases line by line
			while((line = bfreader.readLine()) != null) {
				//prepocessEntry() unifies the format of each entry by eliminating some edge cases.
				String goodLine = preprocessEntry(line);
				String[] attri = goodLine.split(";");
				String status = attri[StatusCol];
				if (!status.toUpperCase().equals("CERTIFIED")) {
					continue;
				}
				
				//extract work state and occupation name
				String state = attri[WorkStateCol];
				String job = attri[SOCnameCol];
				
				//update counts and number of certified cases
				certifiedNum++;
				states.put(state, states.getOrDefault(state, 0) + 1);
				jobs.put(job,jobs.getOrDefault(job, 0) + 1);
			}
			
			int t2 = (int) System.currentTimeMillis();
			System.out.print("Time to read file and establish hash tables: " + (t2 - t1) + "\n");
			System.out.print("Total number of different certified states is " + states.size() + "; of jobs is: " + jobs.size() + "\n");
			
			//Get top k entries from prioprity queue
			List<Map.Entry<String, Integer>> topKstatesList = getTopKFromMap(states,numK);
			List<String> statePercent = new ArrayList<>();
			//calculate percentage
			for (Map.Entry<String, Integer> entry : topKstatesList) {
				double percent = (double) entry.getValue()/ certifiedNum * 100;
				statePercent.add(String.format("%.1f", percent));
			}
			
			List<Map.Entry<String, Integer>> topTKtitlesList = getTopKFromMap(jobs,numK);
			List<String> titlePercent = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : topTKtitlesList) {
				double percent = (double) entry.getValue()/ certifiedNum * 100;
				titlePercent.add(String.format("%.1f", percent));
			}
			int t3 = (int) System.currentTimeMillis();
			System.out.print("Time of top K in using pq: " + (t3 - t2) + "\n");
			
			//save results as output files
			saveAsText(topTKtitlesList, titlePercent, outputOccupPath, "title");
			saveAsText(topKstatesList, statePercent, outputStatePath,"state");
			int t4 = (int) System.currentTimeMillis();
			System.out.print("Time of saving data: " + (t4 - t3) + "\n");
			
			bfreader.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	//Use minHeap to select top K
	public List<Map.Entry<String, Integer>> getTopKFromMap(Map<String, Integer> hashMap, int k) {
		PriorityQueue<Map.Entry<String, Integer>> topKPq = new PriorityQueue<>(new entryComparator());
		for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
			topKPq.offer(entry);
			if (topKPq.size() > 10) {
				topKPq.poll();
			}
		}
		List<Map.Entry<String, Integer>> topKList = new ArrayList<>(15);
		while (!topKPq.isEmpty()) {
			topKList.add(topKPq.poll());
		}
		return topKList;
	}
	
	//unify the format of case entries
	public String preprocessEntry(String line) {
		char[] charArr = line.toCharArray();
		int quoteNum = 0;
		for (int i = 0; i < charArr.length; i++) {
			if (charArr[i] == ';' && quoteNum % 2 == 1) {
				charArr[i] = 'a';
			}
			if (charArr[i] == '\"') {
				quoteNum++;
			}
		}
		String newLine = new String(charArr);
		return newLine;
	}
	
	public boolean saveAsText(List<Map.Entry<String, Integer>> topKList, List<String> percentList, String filePath, String sortField) {
		try {
			//delete old text file first
			File f1 = new File(filePath);
			if (f1.isFile()) {
				f1.delete();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			if (sortField.contains("title")) {
				writer.append("TOP_OCCUPATIONS;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE");
			} else if (sortField.contains("state")) {
				writer.append("TOP_STATES;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE");
			}
			for (int i = topKList.size() - 1; i > -1; i--) {
				writer.newLine();
				writer.append(topKList.get(i).getKey() + ";" + topKList.get(i).getValue() + ";" + percentList.get(i) + "%");
			}
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static class entryComparator implements Comparator<Map.Entry<String, Integer>> {
		@Override
		public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
			if (e1.getValue() != e2.getValue()) {
				return e1.getValue() - e2.getValue();
			} else {
				return -e1.getKey().compareToIgnoreCase(e2.getKey());
			}
		}
	}
	
	public static class StrComparator implements Comparator<String> {
		@Override
		public int compare(String s1, String s2) {
			return s1.compareToIgnoreCase(s2);
		}
	}
}
