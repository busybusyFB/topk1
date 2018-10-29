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
		
		//create a priority queue in reversed order
		PriorityQueue<Map.Entry<String, Integer>> topKPq = new PriorityQueue<>((a, b) -> 
			a.getValue() != b.getValue() ? a.getValue() - b.getValue() : b.getKey().compareToIgnoreCase(a.getKey())
		);	
		
		//Put map entries in the map, keep the size of map no larger than k
		for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
			topKPq.offer(entry);
			if (topKPq.size() > k) {
				topKPq.poll();
			}
		}
		
		//Put entry keys of PQ into a list in order O(m*logk)
		List<String> topKKeyList = new ArrayList<String>();
		while (!topKPq.isEmpty()) {
			topKKeyList.add(topKPq.poll().getKey());
		}
		
		//Reverse the list so that the elements are in descending order.
		Collections.reverse(topKKeyList);
		
		return topKKeyList;
	}
	
	//Function 2: Use bucket sorted
	public List<String> getTopKFromMap(Map<String, Integer> hashMap, int k, int total) {
		
		List<String> res = new ArrayList<>();
		
		//create a array holding lists of buckets
		List<String>[] buckets = new List[total + 1];
		
		//Put keys into buckets
        for(String key: hashMap.keySet()) {
            int freq = hashMap.get(key);
            if(buckets[freq] == null) {
                buckets[freq] = new ArrayList<>();
            }
            buckets[freq].add(key);
        }
        
        //Extract top k keys from bucket list array
        for(int pos = buckets.length - 1; pos >= 0; pos--) {
            if(buckets[pos] != null) {
            	//In case of more than one key in the same bucket, sort keys in the bucket in the order a - z
            	buckets[pos].sort((a, b) -> (a.compareToIgnoreCase(b)));
                for(int i = 0; i < buckets[pos].size() && res.size() < k; i++) {
                    res.add(buckets[pos].get(i));
                }
            }
        }
		return res;
	}
}
