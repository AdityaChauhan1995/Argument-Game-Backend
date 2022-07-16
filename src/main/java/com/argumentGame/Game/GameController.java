package com.argumentGame.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
@CrossOrigin
public class GameController {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	ArrayList<String> copyElements(ArrayList<String> list){
		ArrayList<String> newList = new ArrayList<String>(list);
		return newList;
	}
	
	boolean duplicateElement(ArrayList<String> list, String element, int position){
		for(int i=0;i<list.size();i++) {
			if(i%2 == position && list.get(i).equalsIgnoreCase(element)) {
				return true;
			}
		}
			return false;
	}
	
	ArrayList<ArrayList<String>> getPlayedGameTreeList(String initialNode,HashMap<String,String> map){
		ArrayList<ArrayList<String>> aList =  new ArrayList<ArrayList<String> >();
		ArrayList<String> firstRow = new ArrayList<String>();
		firstRow.add(initialNode);
		aList.add(firstRow);
		int index=0;
		while(index<aList.size()) {
			ArrayList<String> tempList = aList.get(index);
			int count = aList.get(index).size();
			String element = aList.get(index).get(count-1);
			while(map.get(element) != null ) {
				String attacker = map.get(element);
				if(attacker.length() == 1) {
					aList.get(index).add(attacker);
				}else {
					String[] attackers = attacker.split(",");
					for(int i=1;i<attackers.length;i++) {
						ArrayList<String> temp = copyElements(aList.get(index));
						temp.add(attackers[i]);
						aList.add(temp);
					}
					aList.get(index).add(attackers[0]);
				}
				count = aList.get(index).size();
				element = aList.get(index).get(count-1);
			}
			index++;
		}
		return aList;
	}
	
	int compareTreeList(ArrayList<ArrayList<String>> gameTreeList, ArrayList<ArrayList<String>> playedGameTreeList, String lastNodeAdded, String gameStart) {
		boolean matched = true;
		ArrayList<String> allMatched = new ArrayList<String>();
		for(ArrayList<String> treeList: playedGameTreeList ) {
			matched = true;
			for(ArrayList<String> gameList: gameTreeList ) {
				matched = true;
				if(treeList.size() > gameList.size()) {
					matched = false;
				}else {
					for(int i=0;i<treeList.size();i++) {
						if(!treeList.get(i).split("\\(")[0].equalsIgnoreCase(gameList.get(i))) {
							matched = false;
							break;
						}
					}
				}
				if(matched) {
					break;
				}
			}
			allMatched.add(String.valueOf(matched));
		}
		int result = -1, count = 0;
		for(String r:allMatched) {
			if(r.equalsIgnoreCase("false")) {
				result = count;
				break;
			}
			count++;
		}
//		int evenRows = 0;
//		for(ArrayList<String> treeList: playedGameTreeList ) {
//			if(treeList.size()%2==0) {
//				evenRows++;
//			}
//		}
//		if(evenRows == playedGameTreeList.size()) {
//			result = 0;
//		}
		int validLength = 0;
		if(gameStart.equalsIgnoreCase("Proponent")) {
			validLength = 0;
		}else {
			validLength = 1;
		}
		matched = false; 
		ArrayList<String> tempList = new ArrayList<String>();
		for(ArrayList<String> treeList: playedGameTreeList ) {
			matched = false;
			for(String value:treeList) {
				if(lastNodeAdded.equalsIgnoreCase(value)) {
					matched = true;
					tempList = treeList;
					break;
				}
			}
			if(matched) {
				break;
			}
		}
		if(matched && tempList.size()%2==validLength) {
			result = 0;
		}
		return result;
	}
	
	boolean compareLength(ArrayList<ArrayList<String>> gameTreeList, ArrayList<ArrayList<String>> playedGameTreeList) {
		int gameTreeListLength = 0;
		for(ArrayList<String> treeList:gameTreeList) {
			gameTreeListLength = gameTreeListLength + treeList.size();
		}
		int playedGameTreeListLength = 0;
		for(ArrayList<String> gameList:playedGameTreeList) {
			playedGameTreeListLength = playedGameTreeListLength + gameList.size();
		}
		if(gameTreeListLength == playedGameTreeListLength) {
			return true;
		}else {
			return false;
		}
		
	}
	
	String getWinningStatergy(ArrayList<ArrayList<String>> playedGameTreeList, ArrayList<ArrayList<String>> gameTreeList,String gameStart) {
		
		ArrayList<String> allMatched = new ArrayList<String>();
		for(ArrayList<String> treeList: playedGameTreeList ) {
			boolean matched = true;
			for(ArrayList<String> gameList: gameTreeList ) {
				matched = true;
				if(treeList.size() == gameList.size()) {
					for(int i=0;i<treeList.size();i++) {
						if(!treeList.get(i).split("\\(")[0].equalsIgnoreCase(gameList.get(i))) {
							matched = false;
							break;
						}
					}
				}else {
					matched = false;
				}
				if(matched) {
					break;
				}
			}
			allMatched.add(String.valueOf(matched));
		}
		StringBuffer temp  = new StringBuffer();
		int count =0;
		int validPropLength = 1, validOppLength = 0;
		if(gameStart.equalsIgnoreCase("Proponent")) {
			validPropLength = 1;
			validOppLength = 0;
		}else {
			validPropLength = 0;
			validOppLength = 1;
		}
		for(ArrayList<String> gameList:playedGameTreeList) {
			if(gameList.size()%2 == validPropLength && allMatched.get(count).equalsIgnoreCase("true")) {
				if(!temp.isEmpty()) {
					temp.append("      ;      ");
				}else {
					temp.append("Winning Statergy for proponent is :");
				}
				for(int i=0;i<gameList.size();i++) {
					temp.append(gameList.get(i));
					if(i != gameList.size()-1) {
						temp.append("-->");
					}
				}
			}
			count++;
		}
		StringBuffer temp2  = new StringBuffer();
		count =0;
		for(ArrayList<String> gameList:playedGameTreeList) {
			if(gameList.size()%2 == validOppLength && allMatched.get(count).equalsIgnoreCase("true")) {
				if(!temp2.isEmpty()) {
					temp2.append("      ;      ");
				}else {
					temp2.append("     and Winning Statergy for opponent is :");
				}
				for(int i=0;i<gameList.size();i++) {
					temp2.append(gameList.get(i));
					if(i != gameList.size()-1) {
						temp2.append("-->");
					}
				}
			}
			count++;
		}
		temp.append(temp2.toString());
		return temp.toString();
	}
	
	String getMessage(ArrayList<ArrayList<String>>playedGameTreeList ,ArrayList<ArrayList<String>> gameTreeList,int opponentWinCount,int proponentWinCount, String gameStart) {
		int newOpponentWinCount = 0;
		int newProponentWinCount = 0;
		ArrayList<String> allMatched = new ArrayList<String>();
		for(ArrayList<String> treeList: playedGameTreeList ) {
			boolean matched = true;
			for(ArrayList<String> gameList: gameTreeList ) {
				matched = true;
				if(treeList.size() == gameList.size()) {
					for(int i=0;i<treeList.size();i++) {
						if(!treeList.get(i).split("\\(")[0].equalsIgnoreCase(gameList.get(i))) {
							matched = false;
							break;
						}
					}
				}else {
					matched = false;
				}
				if(matched) {
					break;
				}
			}
			allMatched.add(String.valueOf(matched));
		}
		int validPropLength = 1, validOppLength = 0;
		if(gameStart.equalsIgnoreCase("Proponent")) {
			validPropLength = 1;
			validOppLength = 0;
		}else {
			validPropLength = 0;
			validOppLength = 1;
		}
		
		int index = 0;
		for(String r:allMatched) {
			if(r.equalsIgnoreCase("true") && playedGameTreeList.get(index).size()%2 == validPropLength) {
				newProponentWinCount++;
			}else if(r.equalsIgnoreCase("true") && playedGameTreeList.get(index).size()%2 == validOppLength) {
				newOpponentWinCount++;
			}
			index++;
		}
		String message = null;
		if(newProponentWinCount>proponentWinCount) {
			message = "Proponent Wins !";
		}else if(newOpponentWinCount>opponentWinCount) {
			message = "Opponent Wins !";
		}
		if(message != null) {
			return message+";"+newProponentWinCount+";"+newOpponentWinCount;
		}else {
			return message;
		}
	}
	
	String getNodeCount(String nextNode,ArrayList<Nodes> nodes ) {
		int count=0;
		for(Nodes node: nodes) {
			if(node.getId().split("\\(")[0].equalsIgnoreCase(nextNode)) {
				count++;
			}
		}
		if(count != 0) {
			count++;
			return nextNode + "(" + String.valueOf(count) + ")";
		}else {
			return nextNode;
		}
	}
	
	boolean isSublistAlreadyPresent(ArrayList<String> subList,String tempNextNode,ArrayList<ArrayList<String>> playedGameTreeList) {
		
		ArrayList<String> tempSubList = new ArrayList<String>(subList);
		tempSubList.add(tempNextNode);
		boolean matched = true;
		for(ArrayList<String> treeList: playedGameTreeList ) {
			matched = true;
			if(tempSubList.size() <= treeList.size()) {
				for(int i=0;i<tempSubList.size();i++) {
					if(!tempSubList.get(i).split("\\(")[0].equalsIgnoreCase(treeList.get(i).split("\\(")[0])) {
						matched = false;
						break;
					}
				}
			}else {
				matched = false;
			}
			if(matched) {
				break;
			}
		}
		return matched;
	}
	
	String getNextMove(ArrayList<ArrayList<String>> playedGameTreeList,ArrayList<ArrayList<String>> gameTreeList,ArrayList<Nodes> nodes, String gameStart) {
		ArrayList<String> stringMatched = new ArrayList<String>();
		ArrayList<String> lengthMatched = new ArrayList<String>();
		ArrayList<Integer> index = new ArrayList<Integer>();
		ArrayList<String> tempGameList = new ArrayList<>();
		String result = "";
		boolean resultObtained = false;
		int count = 0;
		int validLength = 1, validLength2 = 0;
		if(gameStart.equalsIgnoreCase("Proponent")) {
			validLength = 1;
			validLength2 = 0;
		}else {
			validLength = 0;
			validLength2 = 1;
		}
		for(ArrayList<String> treeList: playedGameTreeList ) {
			boolean matched = true;
			count=0;
			for(ArrayList<String> gameList: gameTreeList ) {
				matched = true;
				if(treeList.size() <= gameList.size()) {
					for(int i=0;i<treeList.size();i++) {
						if(!treeList.get(i).split("\\(")[0].equalsIgnoreCase(gameList.get(i))) {
							matched = false;
							break;
						}
					}
				}else {
					matched = false;
				}
				if(matched) {
					tempGameList = gameList;
					break;
				}
				count++;
			}
			stringMatched.add(String.valueOf(matched));
			if(matched && treeList.size() == tempGameList.size()) {
				lengthMatched.add(String.valueOf(true));
				index.add(count);
			}else {
				lengthMatched.add(String.valueOf(false));
			}
			if(matched && !(treeList.size() == tempGameList.size())) {
				if(treeList.size()%2 == validLength) {
					int playedGameLength= treeList.size();
					String nextNode = tempGameList.get(playedGameLength);
					nextNode = getNodeCount(nextNode,nodes);
					result = nextNode +";" + treeList.get(playedGameLength-1);
					resultObtained = true;
					break;
				}
			}
		}
		
		if(resultObtained) {
			return result;
		}else {
			ArrayList<ArrayList<String>> tempGameTreeList = new ArrayList<ArrayList<String>>();
			ArrayList<String> subList = new ArrayList<String>();
			String tempNextNode = "";
			resultObtained = false;
//			for(int i:index) {
//				tempGameTreeList.remove(i);
//			}
			for(int i=0;i<gameTreeList.size();i++) {
				if(!index.contains(i)) {
					tempGameTreeList.add(gameTreeList.get(i));
				}
			}
			for(ArrayList<String> treeList: playedGameTreeList) {
				if(treeList.size()%2 == validLength2) {
					tempNextNode = treeList.get(treeList.size()-1);
					subList = new ArrayList<String>(treeList.subList(0, treeList.size()-1));
				}else {
					tempNextNode = treeList.get(treeList.size()-2);
					subList = new ArrayList<String>(treeList.subList(0, treeList.size()-2));
				}
				while(subList.size() >0) {
					boolean matched = true;
					for(ArrayList<String> gameList: tempGameTreeList ) {
						matched = true;
						if(subList.size() < gameList.size()) {
							for(int i=0;i<subList.size();i++) {
								if(!subList.get(i).split("\\(")[0].equalsIgnoreCase(gameList.get(i))) {
									matched = false;
									break;
								}
							}
					}else {
						matched = false;
					}
					if(matched) {
						if(!tempNextNode.split("\\(")[0].equalsIgnoreCase(gameList.get(subList.size())) ) {
								tempGameList = gameList;
								break;
						}else {
							matched = false;
							}
						}
					}
					if(matched) {
						int playedGameLength= subList.size();
						String nextNode = tempGameList.get(playedGameLength);
						nextNode = getNodeCount(nextNode,nodes);
						if(!isSublistAlreadyPresent(subList,nextNode,playedGameTreeList)) {
							result = nextNode +";" + subList.get(playedGameLength-1);
							resultObtained = true;
							break;
						}
					}
					if(subList.size() == 1 && gameStart.equalsIgnoreCase("Proponent")) {
						break;
					}else if(subList.size() == 2 && gameStart.equalsIgnoreCase("Opponent")) {
						break;
					}else if(subList.size()%2 == validLength2) {
						tempNextNode = subList.get(subList.size()-1);
						subList = new ArrayList<String>(subList.subList(0, subList.size()-1));
					}else {
						tempNextNode = subList.get(subList.size()-2);
						subList = new ArrayList<String>(subList.subList(0, subList.size()-2));
					}
			  }
			if(resultObtained) {
				break;
			}
			}
			return result;
		}
		
	}
	
	ArrayList<ArrayList<String>> sortListOfList(ArrayList<ArrayList<String>> gameTreeList, String order){
//		ArrayList<Integer> indexes = new ArrayList<Integer>();
//		ArrayList<Integer> length = new ArrayList<Integer>();
		Map<Integer,Integer> map = new HashMap<Integer, Integer>();
		int count =0;
		for(ArrayList<String> list: gameTreeList) {
//			indexes.add(count);
//			length.add(list.size());
			map.put(count, list.size());
			count++;
		}
		LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<>();
		ArrayList<Integer> list = new ArrayList<>();
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            list.add(entry.getValue());
        }
		if(order.equalsIgnoreCase("asc")){
			Collections.sort(list); 
		}else {
			Collections.sort(list, Collections.reverseOrder());
		}
        for (int num : list) {
            for (Entry<Integer, Integer> entry : map.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }
		ArrayList<ArrayList<String>> tempList = new ArrayList<ArrayList<String>>();
		for (Integer key : sortedMap.keySet()) {
			tempList.add(gameTreeList.get(key));
		}
		return tempList;
		
	}
	
	ArrayList<ArrayList<String>> updatePlayedGameTreeList(String initialNode,ArrayList<Edges> edges ,ArrayList<ArrayList<String>> playedGameTreeList ){
		HashMap<String,String> map = new HashMap<String, String>();
		for(Edges edge:edges) {
			if(map.get(edge.getSource()) == null) {
				map.put(edge.getSource(), edge.getTarget());
			}else {
				String temp = map.get(edge.getSource());
				map.put(edge.getSource(), temp + "," +edge.getTarget());
			}
		}
		
		playedGameTreeList = getPlayedGameTreeList(initialNode,map);
		playedGameTreeList = sortListOfList(playedGameTreeList,"desc");
		
		return playedGameTreeList;
	}
	
	@RequestMapping(path= "/", method = RequestMethod.GET)
	public String getList() {
//		String sql = "SELECT count(*) FROM `argument-games-db`.`argument-game-table`";
//		int result = jdbcTemplate.queryForObject(sql, Integer.class);
		String nodeJson = "[{\"width\":150,\"height\":42,\"id\":\"1\",\"data\":{\"label\":\"Node 1\"},\"position\":{\"x\":86.00000000000006,\"y\":129.99999999999994},\"positionAbsolute\":{\"x\":86.00000000000006,\"y\":129.99999999999994},\"selected\":true,\"dragging\":false},{\"width\":150,\"height\":42,\"id\":\"2\",\"data\":{\"label\":\"Node 2\"},\"position\":{\"x\":251.99999999999991,\"y\":53.00000000000003},\"positionAbsolute\":{\"x\":251.99999999999991,\"y\":53.00000000000003},\"selected\":false,\"dragging\":false},{\"width\":150,\"height\":42,\"id\":\"3\",\"position\":{\"x\":162.67434199207776,\"y\":249.99999999999997},\"data\":{\"label\":\"Node 3\"},\"positionAbsolute\":{\"x\":162.67434199207776,\"y\":249.99999999999997},\"selected\":false,\"dragging\":false},{\"width\":150,\"height\":42,\"id\":\"4\",\"position\":{\"x\":445.90017163032684,\"y\":-30.99999999999995},\"data\":{\"label\":\"Node 4\"},\"positionAbsolute\":{\"x\":445.90017163032684,\"y\":-30.99999999999995},\"selected\":false,\"dragging\":false},{\"width\":150,\"height\":42,\"id\":\"5\",\"position\":{\"x\":-90.10046317682334,\"y\":207.99999999999997},\"data\":{\"label\":\"Node 5\"},\"positionAbsolute\":{\"x\":-90.10046317682334,\"y\":207.99999999999997},\"selected\":false,\"dragging\":false}]";
		String edgesJson = "[{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"id\":\"e2-1\",\"source\":\"2\",\"target\":\"1\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"4\",\"sourceHandle\":null,\"target\":\"2\",\"targetHandle\":null,\"id\":\"reactflow__edge-4-2\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"1\",\"sourceHandle\":null,\"target\":\"5\",\"targetHandle\":null,\"id\":\"reactflow__edge-1-5\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"1\",\"sourceHandle\":null,\"target\":\"3\",\"targetHandle\":null,\"id\":\"reactflow__edge-1-3\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"3\",\"sourceHandle\":null,\"target\":\"1\",\"targetHandle\":null,\"id\":\"reactflow__edge-3-1\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"3\",\"sourceHandle\":null,\"target\":\"2\",\"targetHandle\":null,\"id\":\"reactflow__edge-3-2\"}]";
		String sql = "INSERT INTO `argument-games-db`.`argument-game-table` (NodesJson, EdgesJson, Nodes, Edges) VALUES (" + 
					 "'" + nodeJson + "', " + "'" + edgesJson + "',null,null)";
		
		int result = jdbcTemplate.update(sql);
		String sql1 = "TRUNCATE `argument-games-db`.`argument-game-table` ";
		jdbcTemplate.execute(sql1);
		return String.valueOf(result);
	}
	
	@RequestMapping(path= "/getInitialMap", method = RequestMethod.GET)
	public ResponseEntity<RequestBodySaveMap> getInitialMap() {
//		String nodesJson = "[{\"width\":150,\"height\":42,\"id\":\"1\",\"data\":{\"label\":\"Node 1\"},\"position\":{\"x\":86.00000000000006,\"y\":129.99999999999994},\"positionAbsolute\":{\"x\":86.00000000000006,\"y\":129.99999999999994},\"selected\":true,\"dragging\":false},{\"width\":150,\"height\":42,\"id\":\"2\",\"data\":{\"label\":\"Node 2\"},\"position\":{\"x\":251.99999999999991,\"y\":53.00000000000003},\"positionAbsolute\":{\"x\":251.99999999999991,\"y\":53.00000000000003},\"selected\":false,\"dragging\":false},{\"width\":150,\"height\":42,\"id\":\"3\",\"position\":{\"x\":162.67434199207776,\"y\":249.99999999999997},\"data\":{\"label\":\"Node 3\"},\"positionAbsolute\":{\"x\":162.67434199207776,\"y\":249.99999999999997},\"selected\":false,\"dragging\":false},{\"width\":150,\"height\":42,\"id\":\"4\",\"position\":{\"x\":445.90017163032684,\"y\":-30.99999999999995},\"data\":{\"label\":\"Node 4\"},\"positionAbsolute\":{\"x\":445.90017163032684,\"y\":-30.99999999999995},\"selected\":false,\"dragging\":false},{\"width\":150,\"height\":42,\"id\":\"5\",\"position\":{\"x\":-90.10046317682334,\"y\":207.99999999999997},\"data\":{\"label\":\"Node 5\"},\"positionAbsolute\":{\"x\":-90.10046317682334,\"y\":207.99999999999997},\"selected\":false,\"dragging\":false}]";
//		String edgesJson = "[{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"id\":\"e2-1\",\"source\":\"2\",\"target\":\"1\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"4\",\"sourceHandle\":null,\"target\":\"2\",\"targetHandle\":null,\"id\":\"reactflow__edge-4-2\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"1\",\"sourceHandle\":null,\"target\":\"5\",\"targetHandle\":null,\"id\":\"reactflow__edge-1-5\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"1\",\"sourceHandle\":null,\"target\":\"3\",\"targetHandle\":null,\"id\":\"reactflow__edge-1-3\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"3\",\"sourceHandle\":null,\"target\":\"1\",\"targetHandle\":null,\"id\":\"reactflow__edge-3-1\"},{\"animated\":false,\"style\":{\"stroke\":\"black\"},\"source\":\"3\",\"sourceHandle\":null,\"target\":\"2\",\"targetHandle\":null,\"id\":\"reactflow__edge-3-2\"}]";
//		String sql = "INSERT INTO `argument-games-db`.`argument-game-table` (NodesJson, EdgesJson, Nodes, Edges) VALUES (" + 
//					 "'" + nodesJson + "', " + "'" + edgesJson + "',null,null)";
//		String edgesJson = "[{\"id\": \"e2-1\", \"style\": {\"stroke\": \"black\"}, \"source\": \"2\", \"target\": \"1\", \"animated\": false}, {\"id\": \"reactflow__edge-4-2\", \"style\": {\"stroke\": \"black\"}, \"source\": \"4\", \"target\": \"2\", \"animated\": false, \"sourceHandle\": null, \"targetHandle\": null}, {\"id\": \"reactflow__edge-1-5\", \"style\": {\"stroke\": \"black\"}, \"source\": \"1\", \"target\": \"5\", \"animated\": false, \"sourceHandle\": null, \"targetHandle\": null}, {\"id\": \"reactflow__edge-1-3\", \"style\": {\"stroke\": \"black\"}, \"source\": \"1\", \"target\": \"3\", \"animated\": false, \"sourceHandle\": null, \"targetHandle\": null}, {\"id\": \"reactflow__edge-3-1\", \"style\": {\"stroke\": \"black\"}, \"source\": \"3\", \"target\": \"1\", \"animated\": false, \"sourceHandle\": null, \"targetHandle\": null}, {\"id\": \"reactflow__edge-3-2\", \"style\": {\"stroke\": \"black\"}, \"source\": \"3\", \"target\": \"2\", \"animated\": false, \"sourceHandle\": null, \"targetHandle\": null}]";
//		String nodesJson = "[{\"id\": \"1\", \"data\": {\"label\": \"Node 1\"}, \"width\": 150, \"height\": 42, \"dragging\": false, \"position\": {\"x\": 86.00000000000006, \"y\": 129.99999999999994}, \"selected\": true, \"positionAbsolute\": {\"x\": 86.00000000000006, \"y\": 129.99999999999994}}, {\"id\": \"2\", \"data\": {\"label\": \"Node 2\"}, \"width\": 150, \"height\": 42, \"dragging\": false, \"position\": {\"x\": 251.99999999999991, \"y\": 53.00000000000003}, \"selected\": false, \"positionAbsolute\": {\"x\": 251.99999999999991, \"y\": 53.00000000000003}}, {\"id\": \"3\", \"data\": {\"label\": \"Node 3\"}, \"width\": 150, \"height\": 42, \"dragging\": false, \"position\": {\"x\": 162.67434199207776, \"y\": 249.99999999999997}, \"selected\": false, \"positionAbsolute\": {\"x\": 162.67434199207776, \"y\": 249.99999999999997}}, {\"id\": \"4\", \"data\": {\"label\": \"Node 4\"}, \"width\": 150, \"height\": 42, \"dragging\": false, \"position\": {\"x\": 445.9001716303269, \"y\": -30.99999999999995}, \"selected\": false, \"positionAbsolute\": {\"x\": 445.9001716303269, \"y\": -30.99999999999995}}, {\"id\": \"5\", \"data\": {\"label\": \"Node 5\"}, \"width\": 150, \"height\": 42, \"dragging\": false, \"position\": {\"x\": -90.10046317682334, \"y\": 207.99999999999997}, \"selected\": false, \"positionAbsolute\": {\"x\": -90.10046317682334, \"y\": 207.99999999999997}}]";

		String sql = "SELECT NodesJson,EdgesJson FROM `argument-games-db`.`argument-game-table` ";
		List<RequestBodySaveMap> value = jdbcTemplate.query(sql,new ArgumentGameMapper());
//		List<RequestBodySaveMap> value = new ArrayList<RequestBodySaveMap>();
//		RequestBodySaveMap temp = new RequestBodySaveMap();
//		temp.setNodes(nodesJson);
//		temp.setEdges(edgesJson);
//		value.add(temp);
		return ResponseEntity.status(HttpStatus.OK).body(value.get(0));
	}
	
	@RequestMapping(path= "/saveMap", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<String> saveMap(@RequestBody RequestBodySaveMap value) {
		String sql1 = "TRUNCATE `argument-games-db`.`argument-game-table` ";
		jdbcTemplate.execute(sql1);
		
		String nodeJson = value.getNodes();
		String edgesJson = value.getEdges();
		String sql = "INSERT INTO `argument-games-db`.`argument-game-table` (NodesJson, EdgesJson, Nodes, Edges) VALUES (" + 
					 "'" + nodeJson + "', " + "'" + edgesJson + "',null,null)";
		int result = jdbcTemplate.update(sql);
		if(result != 0) {
			return ResponseEntity.status(HttpStatus.OK).body("Saved");
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
		}
	}
	
	@RequestMapping(path= "/getGameMap", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<HashMap<String,String>> getGameMap(@RequestBody RequestBodyPassed value) {
		ArrayList<Nodes> nodes = value.getNodes();
		ArrayList<Edges> edges = value.getEdges();
		
		HashMap<String,String> map = new HashMap<String, String>();
		for(Edges edge:edges) {
			if(map.get(edge.getSource()) == null) {
				map.put(edge.getSource(), edge.getTarget());
			}else {
				String temp = map.get(edge.getSource());
				map.put(edge.getSource(), temp + "," +edge.getTarget());
			}
		}
//		System.out.println(map);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	@RequestMapping(path= "/getGameList", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<ArrayList<ArrayList<String>>> getGameList(@RequestBody RequestBodyPassed value) {
		ArrayList<ArrayList<String> > aList =  new ArrayList<ArrayList<String>>();
		HashMap<String,String> map = value.getGameTreeMap();
		String initialNode = value.getInitialNode();
		String gameStart = value.getGameStart();
		int x = 1, position = 1;
		if(gameStart.equalsIgnoreCase("Proponent")) {
			if(value.getGameType().equalsIgnoreCase("Preferred")) {
				x=0;
				position = 1;
			}else if(value.getGameType().equalsIgnoreCase("Grounded")) {
				x=1;
				position = 0;
			}
		}else {
			if(value.getGameType().equalsIgnoreCase("Preferred")) {
				x=1;
				position = 0;
			}else if(value.getGameType().equalsIgnoreCase("Grounded")) {
				x=0;
				position = 1;
			}
		}
		
		
		ArrayList<String> firstRow = new ArrayList<String>();
		firstRow.add(initialNode);
		aList.add(firstRow);
		int index=0;
		while(index<aList.size()) {
			ArrayList<String> tempList = aList.get(index);
			int count = aList.get(index).size();
			String element = aList.get(index).get(count-1);
			while(map.get(element) != null ) {
				if(count%2==x) {
					String attacker = map.get(element);
					if(attacker.length() == 1) {
						aList.get(index).add(attacker);
					}else {
						String[] attackers = attacker.split(",");
						for(int i=1;i<attackers.length;i++) {
							ArrayList<String> temp = copyElements(aList.get(index));
							temp.add(attackers[i]);
							aList.add(temp);
						}
						aList.get(index).add(attackers[0]);
					}
				}else {
					String attacker = map.get(element);
					if(attacker.length() == 1) {
						if(!duplicateElement(aList.get(index),attacker,position)) {
							aList.get(index).add(attacker);
						}else {
							break;
						}
					}else {
						String[] tempAttackers = attacker.split(",");
						ArrayList<String> attackers = new ArrayList<String>();
						for(String temp:tempAttackers) {
							if(!duplicateElement(aList.get(index),temp,position)) {
								attackers.add(temp);
							}
						}
						if(attackers.size() >0) {
							for(int i=1;i<attackers.size();i++) {
								ArrayList<String> temp = copyElements(aList.get(index));
								temp.add(attackers.get(i));
								aList.add(temp);
							}
							aList.get(index).add(attackers.get(0));
						}else {
							break;
						}
					}
				}
				count = aList.get(index).size();
				element = aList.get(index).get(count-1);
			}
			index++;
		}
		aList = sortListOfList(aList,"asc");
		return ResponseEntity.status(HttpStatus.OK).body(aList);
	}
	
	@RequestMapping(path= "/validate", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<ValidateResponse> getGameMap(@RequestBody ValidateRequest validateRequest) {
		ArrayList<Nodes> nodes = validateRequest.getNodes();
		ArrayList<Edges> edges = validateRequest.getEdges();
		String initialNode = validateRequest.getInitialNode();
		String lastNodeAdded = validateRequest.getLastAddedNode();
		String gameStart = validateRequest.getGameStart();
		ArrayList<ArrayList<String>> gameTreeList = validateRequest.getGameTreeList();
		int proponentWinCount = validateRequest.getProponentWinCount();
		int opponentWinCount = validateRequest.getOpponentWinCount();
		
		ArrayList<ArrayList<String>> playedGameTreeList = new ArrayList<ArrayList<String>>();
		
		HashMap<String,String> map = new HashMap<String, String>();
		for(Edges edge:edges) {
			if(map.get(edge.getSource()) == null) {
				map.put(edge.getSource(), edge.getTarget());
			}else {
				String temp = map.get(edge.getSource());
				map.put(edge.getSource(), temp + "," +edge.getTarget());
			}
		}
		
		playedGameTreeList = getPlayedGameTreeList(initialNode,map);
		playedGameTreeList = sortListOfList(playedGameTreeList,"desc");
		int result = compareTreeList(gameTreeList,playedGameTreeList,lastNodeAdded,gameStart);
		boolean lengthMatched = compareLength(gameTreeList,playedGameTreeList);
//		String winningStatergy = getWinningStatergy(playedGameTreeList);
		ValidateResponse validateResponse = new ValidateResponse();
		
		if(result == -1 && lengthMatched) {
			validateResponse.setResult(true);
			validateResponse.setWin("Game Over");
			String message = getMessage(playedGameTreeList,gameTreeList,opponentWinCount, proponentWinCount,gameStart);
			if(message != null) {
				validateResponse.setMessage(message.split(";")[0] + " and Game Finished with " + getWinningStatergy(playedGameTreeList,gameTreeList,gameStart));
				validateResponse.setProponentWinCount(Integer.valueOf(message.split(";")[1]));
				validateResponse.setOpponentWinCount(Integer.valueOf(message.split(";")[2]));
			}
			}else if(result == -1 && !lengthMatched) {
			validateResponse.setResult(true);
			String nextMove = getNextMove(playedGameTreeList,gameTreeList,nodes,gameStart);
			if(!nextMove.equalsIgnoreCase("")) {
				validateResponse.setWin("Won");
				Nodes node = new Nodes();
				Edges edge = new Edges();
				node.setId(nextMove.split(";")[0]);
				edge.setId("e"+nextMove.split(";")[1] + "-" + nextMove.split(";")[0]);
				edge.setTarget(nextMove.split(";")[0]);
				edge.setSource(nextMove.split(";")[1]);
				validateResponse.setEdge(edge);
				validateResponse.setNode(node);
				edges.add(edge);
				playedGameTreeList = updatePlayedGameTreeList(initialNode,edges,playedGameTreeList );
			}else {
				validateResponse.setWin("Game Won");
			}
			String message = getMessage(playedGameTreeList,gameTreeList,opponentWinCount, proponentWinCount,gameStart);
			if(message != null) {
				boolean lengthEqual = compareLength(gameTreeList,playedGameTreeList);
				if(lengthEqual) {
					validateResponse.setMessage(message.split(";")[0] + " and Game Finished with " + getWinningStatergy(playedGameTreeList,gameTreeList,gameStart));
					validateResponse.setProponentWinCount(Integer.valueOf(message.split(";")[1]));
					validateResponse.setOpponentWinCount(Integer.valueOf(message.split(";")[2]));
				}else {
					validateResponse.setMessage(message.split(";")[0] + getWinningStatergy(playedGameTreeList,gameTreeList,gameStart));
					validateResponse.setProponentWinCount(Integer.valueOf(message.split(";")[1]));
					validateResponse.setOpponentWinCount(Integer.valueOf(message.split(";")[2]));
				}
			}
		}else if(result != -1 ) {
			validateResponse.setResult(false);
			validateResponse.setExceptionMessage("Invalid Move");
		}

		return ResponseEntity.status(HttpStatus.OK).body(validateResponse);
	}
	
	@RequestMapping(path= "/hintMove", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<String> hintMove(@RequestBody ValidateRequest validateRequest) {
		ArrayList<Nodes> nodes = validateRequest.getNodes();
		ArrayList<Edges> edges = validateRequest.getEdges();
		String initialNode = validateRequest.getInitialNode();
		String gameStart = validateRequest.getGameStart();
		ArrayList<ArrayList<String>> gameTreeList = validateRequest.getGameTreeList();
		
		ArrayList<ArrayList<String>> playedGameTreeList = new ArrayList<ArrayList<String>>();
		
		HashMap<String,String> map = new HashMap<String, String>();
		for(Edges edge:edges) {
			if(map.get(edge.getSource()) == null) {
				map.put(edge.getSource(), edge.getTarget());
			}else {
				String temp = map.get(edge.getSource());
				map.put(edge.getSource(), temp + "," +edge.getTarget());
			}
		}
		
		playedGameTreeList = getPlayedGameTreeList(initialNode,map);
		playedGameTreeList = sortListOfList(playedGameTreeList,"desc");
		
		int validLength = 1, validLength2 =0;
		if(gameStart.equalsIgnoreCase("Proponent")) {
			validLength = 1;
			validLength2 =0;
		}else {
			validLength = 0;
			validLength2 =1;
		}
		
		ArrayList<ArrayList<String>> tempGameTreeList = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> tempList: gameTreeList) {
			if(tempList.size()%2 == validLength) {
				tempGameTreeList.add(tempList);
			}
		}
		for(ArrayList<String> tempList: gameTreeList) {
			if(tempList.size()%2 == validLength2) {
				tempGameTreeList.add(tempList);
			}
		}
		gameTreeList = new ArrayList<ArrayList<String>>(tempGameTreeList); 
		
		ArrayList<String> allMatched = new ArrayList<String>();
//		int index=0, maxSameLength = 0;
		String result = "";
		String tempNextNode = null;
		ArrayList<String> subList = new ArrayList<String>();
		ArrayList<String> tempTreeList = new ArrayList<String>();
		boolean resultObtained = false;
		for(ArrayList<String> gameList: gameTreeList ) {
			if(gameList.size() == 2 && gameStart.equalsIgnoreCase("Proponent")) {
				continue;
			}else if(gameList.size()%2 == validLength) {
				tempNextNode = gameList.get(gameList.size()-1);
				subList = new ArrayList<String>(gameList.subList(0, gameList.size()-1));
			}else {
				tempNextNode = gameList.get(gameList.size()-2);
				subList = new ArrayList<String>(gameList.subList(0, gameList.size()-2));
			}
			while(subList.size() >0) {
				boolean matched = true;
				for(ArrayList<String> treeList: playedGameTreeList ) {
					matched = true;
					int length = 0;
					if(subList.size() > treeList.size()) {
						length = treeList.size();
					}else {
						length = subList.size();
					}
					for(int i=0;i<length;i++) {
						if(!treeList.get(i).split("\\(")[0].equalsIgnoreCase(subList.get(i))) {
							matched = false;
							break;
						}
					}
					if(matched) {
						tempTreeList = treeList;
						break;
					}
				}
				if(matched) {
					if(!isSublistAlreadyPresent(subList,tempNextNode,playedGameTreeList)) {
						result = "You can add Node: " + tempNextNode + " to the current branch i.e. ";
						StringBuffer buffer = new StringBuffer();
						for(int i=0; i<subList.size();i++) {
							buffer.append(tempTreeList.get(i));
							if(i != subList.size()-1) {
								buffer.append("-->");
							}
						}
						result = result+buffer.toString();						
						resultObtained = true;
						break;
					}
				}
				if(subList.size() == 2 && gameStart.equalsIgnoreCase("Proponent")) {
					break;
				}else if(subList.size() == 1 && gameStart.equalsIgnoreCase("Opponent")) {
					break;
				}else if(subList.size()%2 == validLength) {
					tempNextNode = subList.get(subList.size()-1);
					subList = new ArrayList<String>(subList.subList(0, subList.size()-1));
				}else {
					tempNextNode = subList.get(subList.size()-2);
					subList = new ArrayList<String>(subList.subList(0, subList.size()-2));
				}
			}
			if(resultObtained) {
				break;
			}
		}
		if(!result.equals("")) {
			return ResponseEntity.status(HttpStatus.OK).body(result);
		}else {
			return ResponseEntity.status(HttpStatus.OK).body("No Hint Available !");
		}
		
	}
}
