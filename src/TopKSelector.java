import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


/* This class sorted (key, value) pairs stored in a map and return a list of the top k among them with higher priority.
 * Input: map + other necessary parameter
 * Output: sorted list of top k elements selected in the map
 * Each method of the class implements one selecting and sorting algorithm
 * Function 1: minHeap (priority queue)
 * Function 2: bucket sort
 */
public class TopKSelector {	
	//Function 1: Use minHeap to select top K
	public static List<String> getTopKKeysFromMap(Map<String, Integer> hashMap, int k) {
		PriorityQueue<Map.Entry<String, Integer>> topKPq = new PriorityQueue<>((a, b) -> {
			if (a.getValue() != b.getValue()) {
				return a.getValue() - b.getValue();
			} else {
				return -a.getKey().compareToIgnoreCase(b.getKey());
			}
		});	
		
		for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
			topKPq.offer(entry);
			if (topKPq.size() > k) {
				topKPq.poll();
			}
		}
		List<String> topKKeyList = new ArrayList<String>();
		while (!topKPq.isEmpty()) {
			topKKeyList.add(topKPq.poll().getKey());
		}
		Collections.reverse(topKKeyList);
		return topKKeyList;
	}
	
	//Function 2: Use bucket sorted
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
            	buckets[pos].sort((a, b) -> (a.compareToIgnoreCase(b)));
                for(int i = 0; i < buckets[pos].size() && res.size() < k; i++) {
                    res.add(buckets[pos].get(i));
                }
            }
        }
		return res;
	}
}
