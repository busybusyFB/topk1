import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

public class TopKSelector {

	public static void main(String[] args) {
//		String inputFilePath = "./input/H1B_FY_2016.csv";
//		String outputStateDir = "./output/top_10_states.txt";
//		String outputOccupDir = "./output/top_10_occupations.txt";
		String inputFilePath = args[0];
		String outputOccupPath = args[1];
		String outputStatePath = args[2];
		File f1 = new File(outputStatePath);
		File f2 = new File(outputOccupPath);
		if (f1.isFile()) {
			f1.delete();
		}
		if (f2.isFile()) {
			f2.delete();
		}
		TopKSelector selector = new TopKSelector();
		selector.selectTopK(inputFilePath, outputStatePath, outputOccupPath, 10);
	}
	
	public boolean selectTopK(String inputFilePath, String outputStatePath, String outputOccupPath, int topK)	{
		try {
			BufferedReader bfreader = new BufferedReader(new FileReader(inputFilePath));
			String line = new String();
			int topKNum = 10;
			String header = bfreader.readLine();
			
			if (header == null) { //empty file
				bfreader.close();
				System.out.println("Input file is empty");
				return false;
			}
			
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
			Map<String, Integer> states = new HashMap<>();
			Map<String, Integer> titles = new HashMap<>();
			while((line = bfreader.readLine()) != null) {
				//prepocessEntry() unifies the format of each entry by eliminating some edge cases.
				//See details in the README file
//				if (line.isEmpty()) continue;
//				System.out.print(line + "\n");
				String goodLine = preprocessEntry(line);
				String[] attri = goodLine.split(";");
				String status = attri[StatusCol];
				if (!status.toUpperCase().equals("CERTIFIED")) {
					continue;
				}
				certifiedNum++;
				
				String state = attri[WorkStateCol];
				String title = attri[SOCnameCol];
				
//				Integer stateNum = states.get(state);
//				stateNum = stateNum == null? 1 : stateNum + 1;
//				states.put(state, stateNum);
//				System.out.print(attri[0] + " " + state + "\n");
				states.put(state, states.getOrDefault(state, 0) + 1);
				
//				Integer titleNum = titles.get(title);
//				titleNum = titleNum == null ? 1 : titleNum + 1;
//				titles.put(title, titleNum);
				titles.put(title,titles.getOrDefault(title, 0) + 1);
//				if (certifiedNum > 5) break;
				
//				if (state.length() != 2 || certifiedNum == 1000) {
//					System.out.print(line + "\n");
//					System.out.println(state  + " " + title + "\n");
//				}
//				System.out.println(status + " " + state  + " " + title + "\n");
			}
			
			System.out.print("Total number of states in hashmap is " + states.size() + "; titles is: " + titles.size() + "\n");
//			for(String key: states.keySet()) {
//				System.out.print(key + "\n");
//			}
			
			//method 1 maxHeap
//			System.out.print(System.currentTimeMillis() + "\n");
			List<Map.Entry<String, Integer>> topKstatesList = getTopKFromMap(states,topKNum);
			List<String> statePercent = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : topKstatesList) {
				double percent = (double) entry.getValue()/ certifiedNum * 100;
				statePercent.add(String.format("%.1f", percent));
			}
			
			List<Map.Entry<String, Integer>> topTKtitlesList = getTopKFromMap(titles,topKNum);
			List<String> titlePercent = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : topTKtitlesList) {
				double percent = (double) entry.getValue()/ certifiedNum * 100;
				titlePercent.add(String.format("%.1f", percent));
			}
//			System.out.println("Total certified number is " + certifiedNum + "\nThe top 10 states are");
			for (int i = topKstatesList.size() -1 ; i > -1; i--) {
//				System.out.println(topKstatesList.get(i).getKey() + " " + topKstatesList.get(i).getValue() + " " + statePercent.get(i));
			}
//			System.out.println("The top 10 titles are");
			for (int i = topTKtitlesList.size() -1 ; i > -1; i--) {
//				System.out.println(topTKtitlesList.get(i).getKey() + " " + topTKtitlesList.get(i).getValue() + " " + titlePercent.get(i));
			}
//			System.out.print(System.currentTimeMillis() + "\n");
			
//			
			saveAsText(topTKtitlesList, titlePercent, outputOccupPath, "title");
			saveAsText(topKstatesList, statePercent, outputStatePath,"state");
			//endofmathed1
			
			//method 2 bucketsort
//			System.out.print(System.currentTimeMillis() + "\n");
//			List<String> topKstatesList = getTopKFromMap(states,topKNum,certifiedNum);
//			List<String> topTKtitlesList = getTopKFromMap(titles,topKNum, certifiedNum);
//			List<String> statePercent = new ArrayList<>();
//			for (String key : topKstatesList) {
//				double percent = (double) states.get(key)/ certifiedNum * 100;
//				statePercent.add(String.format("%.1f", percent));
//			}
//			List<String> titlePercent = new ArrayList<>();
//			for (String key : topTKtitlesList) {
//				double percent = (double) titles.get(key)/ certifiedNum * 100;
//				titlePercent.add(String.format("%.1f", percent));
//			}
//			System.out.println("Total certified number is " + certifiedNum + "\nThe top 10 states are");
//			for (int i = 0; i < topKstatesList.size(); i++) {
//				System.out.println(topKstatesList.get(i) + " " + states.get(topKstatesList.get(i)) + " " + statePercent.get(i));
//			}
//			System.out.println("The top 10 titles are");
//			for (int i = 0; i < topTKtitlesList.size(); i++) {
//				System.out.println(topTKtitlesList.get(i) + " " + titles.get(topTKtitlesList.get(i)) + " " + titlePercent.get(i));
//			}
//			System.out.print(System.currentTimeMillis());
			//endofmethod2
			
			bfreader.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static class myComparator implements Comparator<Map.Entry<String, Integer>> {
		@Override
		public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
			if (e1.getValue() != e2.getValue()) {
				return e1.getValue() - e2.getValue();
			} else {
				return -e1.getKey().compareToIgnoreCase(e2.getKey());
			}
		}
	}
	
	//Use minHeap to select top K
	public List<Map.Entry<String, Integer>> getTopKFromMap(Map<String, Integer> hashMap, int k) {
		PriorityQueue<Map.Entry<String, Integer>> topKPq = new PriorityQueue<>(new myComparator());
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
	//Use bucket sorted
	public List<String> getTopKFromMap(Map<String, Integer> hashMap, int k, int total) {
		List<String> res = new ArrayList<>();
		List<String>[] buckets = new List[total + 1];
        for(String key: hashMap.keySet()) {
            int freq = hashMap.get(key);
            if(buckets[freq] == null) {
                buckets[freq] = new ArrayList<>();
            }
            buckets[freq].add(key);
        }
        for(int pos = buckets.length - 1; pos >= 0; pos--) {
            if(buckets[pos] != null) {
            	//sort buckets first a --> z
            	buckets[pos].sort(new StrComparator());
                for(int i = 0; i < buckets[pos].size() && res.size() < k; i++) {
                    res.add(buckets[pos].get(i));
                }
            }
        }
		return res;
	}
	
	public static class StrComparator implements Comparator<String> {
		@Override
		public int compare(String s1, String s2) {
			return s1.compareToIgnoreCase(s2);
		}
	}
	public boolean saveAsText(List<Map.Entry<String, Integer>> topKList, List<String> percentList, String filePath, String sortField) {
		try {
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
}
