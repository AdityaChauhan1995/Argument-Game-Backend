package com.argumentGame.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

		int validLength = 0;
		if(gameStart.equalsIgnoreCase("User")) {
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
		
		ArrayList<ArrayList<ArrayList<String>>> completeWinningList = getWinningSubtrees(gameTreeList);
		
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
		
		int count =0;
		int validPropLength = 1, validOppLength = 0;
		if(gameStart.equalsIgnoreCase("User")) {
			validPropLength = 1;
			validOppLength = 0;
		}
		
		ArrayList<ArrayList<String>> proWinsList = new ArrayList<ArrayList<String>>();
		
		for(ArrayList<String> gameList:playedGameTreeList) {
			if(gameList.size()%2 == validPropLength && allMatched.get(count).equalsIgnoreCase("true")) {
				proWinsList.add(gameList);
			}
			count++;
		}
		
		ArrayList<Boolean> completeListMatchedList = new ArrayList<Boolean>(); 
		
		boolean result = true;
		ArrayList<Boolean> winningListMatched = new ArrayList<Boolean>();
		for(int j=0;j<completeWinningList.size();j++) {
			result = true;
			ArrayList<ArrayList<String>> winList = completeWinningList.get(j);
			ArrayList<Boolean> allMatchedList = new ArrayList<Boolean>();
			for(int i=0;i<winList.size();i++) {
				boolean matched=true;
				for(int k=0;k<proWinsList.size();k++) {
					matched=true;
					if(winList.get(i).size()==proWinsList.get(k).size()) {
						for(int l=0;l<winList.get(i).size();l++) {
							if(!proWinsList.get(k).get(l).split("\\(")[0].equalsIgnoreCase(winList.get(i).get(l))) {
								matched=false;
								break;
							}
						}
					}else {
						matched=false;
					}
					if(matched) {
						break;
					}
				}
				allMatchedList.add(matched);
			}
			for(boolean value:allMatchedList) {
				if(!value) {
					result = false;
					break;
				}
			}
			winningListMatched.add(result);
		}
		
		StringBuffer temp  = new StringBuffer();
		int number = 0;
		for(int i=0;i<winningListMatched.size();i++) {
			if(temp.isEmpty()) {
				temp.append("\nWinning Statergy for proponent is :-");
			}	
			if(winningListMatched.get(i)) {
				ArrayList<ArrayList<String>> winList = completeWinningList.get(i);
				temp.append("\n" + (number+1) + ")   ");
				number++;
				int index =0;
				for(ArrayList<String> gameList:winList) {
					for(int j=0;j<gameList.size();j++) {
						temp.append(gameList.get(j));
						if(j != gameList.size()-1) {
							temp.append("-->");
						}
					}
					if(index != winList.size()-1) {
						temp.append("     ;     ");
					}
					index++;
				}
			}
		}
		
		return temp.toString();
	}
	
	String getMessage(ArrayList<ArrayList<String>>playedGameTreeList ,ArrayList<ArrayList<String>> gameTreeList,int opponentWinCount
						,int proponentWinCount, String gameStart,ArrayList<ArrayList<ArrayList<String>>> completeWinningList) {
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
		if(gameStart.equalsIgnoreCase("User")) {
			validPropLength = 1;
			validOppLength = 0;
		}
		
		int index = 0;
		ArrayList<ArrayList<String>> proWins = new ArrayList<ArrayList<String>>(); 
		for(String r:allMatched) {
			if(r.equalsIgnoreCase("true") && playedGameTreeList.get(index).size()%2 == validPropLength) {
				newProponentWinCount++;
				proWins.add(playedGameTreeList.get(index));
			}else if(r.equalsIgnoreCase("true") && playedGameTreeList.get(index).size()%2 == validOppLength) {
				newOpponentWinCount++;
			}
			index++;
		}
		String message = null;
		if(newProponentWinCount>proponentWinCount) {
			boolean result = compareCompleteWinningStratergy(proWins,completeWinningList);
			if(result) {
				message = "Proponent Wins !!!";
			}else {
				message = "null";
			}
		}
		else if(newOpponentWinCount>opponentWinCount) {
//			message = "Opponent Wins !";
			message = "null";
		}
		if(message != null) {
			return message+";"+newProponentWinCount+";"+newOpponentWinCount;
		}else {
			return message;
		}
	}
	
	boolean compareCompleteWinningStratergy(ArrayList<ArrayList<String>> proWins,ArrayList<ArrayList<ArrayList<String>>> completeWinningList) {
		
		boolean result = true;
		ArrayList<Boolean> winningListMatched = new ArrayList<Boolean>();
		for(int j=0;j<completeWinningList.size();j++) {
			result = true;
			ArrayList<ArrayList<String>> winList = completeWinningList.get(j);
			ArrayList<Boolean> allMatched = new ArrayList<Boolean>();
			for(int i=0;i<winList.size();i++) {
				boolean matched=true;
				for(int k=0;k<proWins.size();k++) {
					matched=true;
					if(winList.get(i).size()==proWins.get(k).size()) {
						for(int l=0;l<winList.get(i).size();l++) {
							if(!proWins.get(k).get(l).split("\\(")[0].equalsIgnoreCase(winList.get(i).get(l))) {
								matched=false;
								break;
							}
						}
					}else {
						matched=false;
					}
					if(matched) {
						break;
					}
				}
				allMatched.add(matched);
			}
			for(boolean value:allMatched) {
				if(!value) {
					result = false;
					break;
				}
			}
			winningListMatched.add(result);
		}
		result = false;
		for(boolean value:winningListMatched) {
			if(value) {
				result = true;
				break;
			}
		}
		return result;
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
		if(tempNextNode != null) {
			tempSubList.add(tempNextNode);
		}
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
		if(gameStart.equalsIgnoreCase("User")) {
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
					if(subList.size() == 1 && gameStart.equalsIgnoreCase("User")) {
						break;
					}else if(subList.size() == 2 && gameStart.equalsIgnoreCase("Computer")) {
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
		Map<Integer,Integer> map = new HashMap<Integer, Integer>();
		int count =0;
		for(ArrayList<String> list: gameTreeList) {
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
	
	ArrayList<ArrayList<ArrayList<String>>> getWinningSubtrees(ArrayList<ArrayList<String>> gameTreeList){
			
		ArrayList<ArrayList<String>> winningSubTrees = helperWinningSubTrees(gameTreeList);
		
		ArrayList<ArrayList<ArrayList<String>>> completeWinningList = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
		int index=0;
		for(ArrayList<String> winningSub:winningSubTrees) {
			String tempNextNode = winningSub.get(winningSub.size()-2);
			ArrayList<String> winShortList = new ArrayList<String>(winningSub.subList(0, winningSub.size()-2));
			ArrayList<ArrayList<ArrayList<String>>> tempCompleteWinningList = new ArrayList<ArrayList<ArrayList<String>>>();
			temp = new ArrayList<ArrayList<String>>();
			temp.add(winningSub);
			completeWinningList.add(temp);
			tempCompleteWinningList.add(temp);
			while(winShortList.size()>0) {
				ArrayList<ArrayList<ArrayList<String>>> tempMatchedSubList = new ArrayList<ArrayList<ArrayList<String>>>();
				Map<String,Integer> map = new HashMap<String, Integer>();
				for(ArrayList<String> tempWinningSub:winningSubTrees) {
					boolean matched = true;
					if(winShortList.size()<tempWinningSub.size()) {
						for(int i=0;i<winShortList.size();i++) {
							if(!winShortList.get(i).equalsIgnoreCase(tempWinningSub.get(i))) {
								matched = false;
								break;
							}
						}
					}else {
						matched=false;
					}
					if(matched && !tempNextNode.equalsIgnoreCase(tempWinningSub.get(winShortList.size()))) {
						if(map.get(tempWinningSub.get(winShortList.size())) == null) {
							map.put(tempWinningSub.get(winShortList.size()), map.size());
						}
						int z = map.get(tempWinningSub.get(winShortList.size()));
						if(tempMatchedSubList.isEmpty()) {
							ArrayList<ArrayList<String>> tempList = new ArrayList<ArrayList<String>>();
							tempList.add(tempWinningSub);
							tempMatchedSubList.add(tempList);
						}else {
							if(tempMatchedSubList.size()-1<z) {
								ArrayList<ArrayList<String>> tempList = new ArrayList<ArrayList<String>>();
								tempList.add(tempWinningSub);
								tempMatchedSubList.add(tempList);
							}else {
								tempMatchedSubList.get(z).add(tempWinningSub);
							}
						}
					}
				}
				if(!tempMatchedSubList.isEmpty()) {
					for(ArrayList<ArrayList<String>> tempMatchedSub:tempMatchedSubList) {
						tempCompleteWinningList = addInCompleteWinningList(tempCompleteWinningList,tempMatchedSub);
					}
				}
				if(winShortList.size() == 1) {
					break;
				}
				tempNextNode = winShortList.get(winShortList.size()-2);
				winShortList = new ArrayList<String>(winShortList.subList(0, winShortList.size()-2));
			}
			completeWinningList.addAll(tempCompleteWinningList);
			index++;
		}
		completeWinningList = removeDuplicate(completeWinningList);
		return completeWinningList;

	}
	
	ArrayList<ArrayList<String>> helperWinningSubTrees(ArrayList<ArrayList<String>> gameTreeList){
		ArrayList<ArrayList<String>> proWinGames = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> oppWinGames = new ArrayList<ArrayList<String>>();
		Set<String> oppFirstMoves = new HashSet<String>();
		for(ArrayList<String> treeList:gameTreeList) {
//		for(ArrayList<String> treeList:tempGameTreeList) {
			if(treeList.size()%2 == 1) {
				proWinGames.add(treeList);
			}else {
				oppWinGames.add(treeList);
			}
			oppFirstMoves.add(treeList.get(1));
		}
		ArrayList<ArrayList<String>> excludedSubTrees = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> winningSubTrees = new ArrayList<ArrayList<String>>();
		int loopCount = 10;
		for(int z=0;z<loopCount;z++) {
			winningSubTrees = new ArrayList<ArrayList<String>>();
			for(ArrayList<String> proWin:proWinGames) {
				boolean toBeIncluded = true;
				if(proWin.size()==3) {
					winningSubTrees.add(proWin);
					continue;
				}else {
					int index = proWin.size()-1;
					String tempNextNode = proWin.get(proWin.size()-2);
					ArrayList<String> proSubList = new ArrayList<String>(proWin.subList(0, proWin.size()-2));
					while(proSubList.size()>1) {
						for(ArrayList<String> oppWin:oppWinGames) {
							boolean matched = true;
							if(oppWin.size()>=proSubList.size()) {
								for(int i=0;i<proSubList.size();i++) {
									if(!proSubList.get(i).equalsIgnoreCase(oppWin.get(i))) {
										matched = false;
										break;
									}
								}
							}else {
								matched = false;
							}
							if(matched && !oppWin.get(proSubList.size()).equalsIgnoreCase(tempNextNode)) {
								if(!isSublistAlreadyPresent(proSubList, oppWin.get(proSubList.size()), proWinGames)) {
//								if(branchLeadsToOpponentWins(proSubList, oppWin.get(proSubList.size()), proWinGames,excludedSubTrees)) {	
									toBeIncluded = false;
									break;
								}
							}
						}
						tempNextNode = proSubList.get(proSubList.size()-2);
						proSubList = new ArrayList<String>(proSubList.subList(0, proSubList.size()-2));
					}
					if(toBeIncluded) {
						winningSubTrees.add(proWin);
					}else {
						excludedSubTrees.add(proWin);
					}
				}
			}
			
			ArrayList<Integer> removeIndex = new ArrayList<Integer>();
			for(int i=proWinGames.size();i<proWinGames.size();i++) {
				ArrayList<String> proWin = proWinGames.get(i);
				for(ArrayList<String> excludedSub:excludedSubTrees) {
					if(proWin.equals(excludedSub)) {
						removeIndex.add(i);
						break;
					}
				}
			}
			
			for(int i = 0; i < removeIndex.size(); i++){
				proWinGames.remove(removeIndex.get(i));
			}
			
			if(removeIndex.isEmpty()) {
				break;
			}else if(z==9) {
				loopCount++;
			}
		}
		
		
		for(int j=0;j<winningSubTrees.size();j++) {
			oppFirstMoves.remove(winningSubTrees.get(j).get(1));
		}
		if(!oppFirstMoves.isEmpty()) {
			winningSubTrees = new ArrayList<ArrayList<String>>();
		}
		
		return winningSubTrees;
	}
	
	ArrayList<ArrayList<ArrayList<String>>> removeDuplicate(ArrayList<ArrayList<ArrayList<String>>> completeWinningList){
		
		for(int i=0;i<completeWinningList.size();i++) {
			ArrayList<ArrayList<String>> winList = completeWinningList.get(i);
			ArrayList<Integer> removeIndexes = new ArrayList<Integer>();
			for(int j=winList.size()-1;j>=0;j--) {
				boolean present = true;
				for(int k=j-1;k>=0;k--) {
					present = true;
					if(winList.get(j).size()<=winList.get(k).size()) {
						for(int l=0;l<winList.get(j).size();l++) {
							if(!winList.get(j).get(l).equalsIgnoreCase(winList.get(k).get(l))) {
								present = false;
								break;
							}
						}
					}else {
						present = false;
					}
					if(present) {
						break;
					}
				}
				if(present) {
					removeIndexes.add(j);
				}
			}
			if(!removeIndexes.isEmpty()) {
				for(int z = 0; z < removeIndexes.size(); z++){
					winList.remove(removeIndexes.get(z));
				}
			}
		}
		
		ArrayList<ArrayList<ArrayList<String>>> tempCompleteWinningList = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<Boolean> remove = new ArrayList<Boolean>();
		for(int i=0;i<completeWinningList.size();i++) {
			ArrayList<ArrayList<String>> winList = completeWinningList.get(i);
			ArrayList<Boolean> allMatched = new ArrayList<Boolean>();
			boolean removeList = false;
			for(int j=i+1;j<completeWinningList.size();j++) {
				ArrayList<ArrayList<String>> tempWinList = completeWinningList.get(j);
				allMatched = new ArrayList<Boolean>();
				for(ArrayList<String> win:winList) {
					boolean matched = false;
					for(ArrayList<String> temp:tempWinList) {
						if(win.equals(temp)) {
							matched = true;
							break;
						}
					}
					allMatched.add(matched);
				}
				for(boolean value:allMatched) {
					if(!value) {
						removeList = false;
						break;
					}else {
						removeList = true;
					}
				}
				if(removeList) {
					break;
				}
			}
			remove.add(removeList);
		}
		
		for(int i=0;i<remove.size();i++) {
			if(!remove.get(i)) {
				tempCompleteWinningList.add(completeWinningList.get(i));
			}
		}
		return tempCompleteWinningList;
		
	}
	
	ArrayList<ArrayList<ArrayList<String>>> addInCompleteWinningList(ArrayList<ArrayList<ArrayList<String>>> tempCompleteWinningList,ArrayList<ArrayList<String>> tempMatchedSubList){
		
		int matchedLength = tempMatchedSubList.size();
		int completeListLength = tempCompleteWinningList.size();
		ArrayList<ArrayList<ArrayList<String>>> tempList = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<ArrayList<String>>> tempList2 = new ArrayList<ArrayList<ArrayList<String>>>(tempCompleteWinningList);
		for(int i=0;i<matchedLength-1;i++) {
			tempList = new ArrayList<ArrayList<ArrayList<String>>>();
			for(ArrayList<ArrayList<String>> temp:tempList2) {
				ArrayList<ArrayList<String>> newTemp = new ArrayList<ArrayList<String>>(temp);
				tempList.add(newTemp);
			}
			tempCompleteWinningList.addAll(tempList);
		}
		int index = -1;
		for(int i=0;i<tempCompleteWinningList.size();i++) {
			if(i%completeListLength == 0) {
				index++;
			}

			tempCompleteWinningList.get(i).add(tempMatchedSubList.get(index));
		}
		
		return tempCompleteWinningList;
	}
	
	@RequestMapping(path= "/", method = RequestMethod.GET)
	public String getList() {
		return String.valueOf(101);
	}
	
	@RequestMapping(path= "/getInitialMap", method = RequestMethod.GET)
	public ResponseEntity<RequestBodySaveMap> getInitialMap() {	
		String sql = "SELECT NodesJson,EdgesJson FROM `argument_games`.`argument-game-table` ";
		List<RequestBodySaveMap> value = jdbcTemplate.query(sql,new ArgumentGameMapper());
		if(value != null && !value.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(value.get(0));
		}else {
			RequestBodySaveMap tempValue = new RequestBodySaveMap();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(tempValue);
		}
		
	}
	
	@RequestMapping(path= "/saveMap", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<String> saveMap(@RequestBody RequestBodySaveMap value) {
		String sql1 = "TRUNCATE `argument_games`.`argument-game-table` ";
		jdbcTemplate.execute(sql1);
		
		String nodeJson = value.getNodes();
		String edgesJson = value.getEdges();
		
		String sql = "INSERT INTO `argument_games`.`argument-game-table` (NodesJson, EdgesJson) VALUES (" + 
				 "'" + nodeJson + "', " + "'" + edgesJson + "')";
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
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	@RequestMapping(path= "/getGameList", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<ArrayList<ArrayList<String>>> getGameList(@RequestBody RequestBodyPassed value) {
		ArrayList<ArrayList<String> > aList =  new ArrayList<ArrayList<String>>();
		HashMap<String,String> map = value.getGameTreeMap();
		String initialNode = value.getInitialNode();
		String gameStart = value.getGameStart();
		int x = 1, position = 1;
			if(value.getGameType().equalsIgnoreCase("Preferred")) {
				x=0;
				position = 1;
			}else if(value.getGameType().equalsIgnoreCase("Grounded")) {
				x=1;
				position = 0;
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
		String gameType = validateRequest.getGameType();
		ArrayList<ArrayList<String>> gameTreeList = validateRequest.getGameTreeList();
		int proponentWinCount = validateRequest.getProponentWinCount();
		int opponentWinCount = validateRequest.getOpponentWinCount();
		ValidateResponse validateResponse = new ValidateResponse();
		
		
		//checking all connections made in Argument game
		if(nodes.size()>1) {
			boolean notPresent = false;
			for(Nodes tempNode: nodes) {
				notPresent = false;
				for(Edges tempEdges:edges) {
					if(tempEdges.getTarget().equalsIgnoreCase(tempNode.getId()) || tempEdges.getSource().equalsIgnoreCase(tempNode.getId())) {
						notPresent = true;
					}
				}
				if(!notPresent) {
					break;
				}
			}
			if(!notPresent) {
				validateResponse.setResult(false);
				validateResponse.setExceptionMessage("Invalid Move");
				return ResponseEntity.status(HttpStatus.OK).body(validateResponse);
			}
		}
		
		ArrayList<ArrayList<ArrayList<String>>> completeWinningList = getWinningSubtrees(gameTreeList);

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
		
		if(result == -1 && lengthMatched) {
			validateResponse.setResult(true);
			validateResponse.setWin("Game Over");
			String message = getMessage(playedGameTreeList,gameTreeList,opponentWinCount, proponentWinCount,gameStart,completeWinningList);
			if(message != null && !message.split(";")[0].equalsIgnoreCase("null")) {
				validateResponse.setMessage(message.split(";")[0] + " and Game Finished \n"+ "Node " +initialNode +" will be included in " 
											+ gameType + " game.\n" + getWinningStatergy(playedGameTreeList,gameTreeList,gameStart));
				validateResponse.setProponentWinCount(Integer.valueOf(message.split(";")[1]));
				validateResponse.setOpponentWinCount(Integer.valueOf(message.split(";")[2]));
			}else if(message != null && message.split(";")[0].equalsIgnoreCase("null")) {
				validateResponse.setMessage("Game Finished but no winning strategy found.");
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
			String message = getMessage(playedGameTreeList,gameTreeList,opponentWinCount, proponentWinCount,gameStart,completeWinningList);
			boolean lengthEqual = compareLength(gameTreeList,playedGameTreeList);
			if(message != null && !message.split(";")[0].equalsIgnoreCase("null")) {
				if(lengthEqual) {
					validateResponse.setMessage(message.split(";")[0] + " and Game Finished \n"+ "Node " +initialNode +" will be included in "
																	+ gameType + " game.\n" +  getWinningStatergy(playedGameTreeList,gameTreeList,gameStart));
					validateResponse.setProponentWinCount(Integer.valueOf(message.split(";")[1]));
					validateResponse.setOpponentWinCount(Integer.valueOf(message.split(";")[2]));
				}else {
					validateResponse.setMessage(message.split(";")[0] + "\n"+ "Node " +initialNode +" will be included in "
													+ gameType + " game.\n" +  getWinningStatergy(playedGameTreeList,gameTreeList,gameStart));
					validateResponse.setProponentWinCount(Integer.valueOf(message.split(";")[1]));
					validateResponse.setOpponentWinCount(Integer.valueOf(message.split(";")[2]));
				}
			}else if(message != null && message.split(";")[0].equalsIgnoreCase("null")) {
				if(lengthEqual) {
					validateResponse.setMessage("Game Finished but no winning strategy found.");
				}else {
					validateResponse.setMessage(null);
				}
				validateResponse.setProponentWinCount(Integer.valueOf(message.split(";")[1]));
				validateResponse.setOpponentWinCount(Integer.valueOf(message.split(";")[2]));
			}
			if(nextMove.equalsIgnoreCase("")) {
				if(validateResponse.getMessage() == null) {
					validateResponse.setMessage("Computer has zero next moves left. If possible, user can explore other paths if he/she wishes to.");
				}else if(!validateResponse.getMessage().contains("Finished")) {
					String value = validateResponse.getMessage();
					validateResponse.setMessage(value + "\n\nComputer has zero next moves left. If possible, user can explore other paths if he/she wishes to.");
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
		if(gameStart.equalsIgnoreCase("User")) {
			validLength = 1;
			validLength2 =0;
		}else {
			validLength = 0;
			validLength2 =1;
		}
		
		ArrayList<ArrayList<String>> tempGameTreeList = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> tempList: gameTreeList) {
			if(tempList.size()%2 == 1) {
				tempGameTreeList.add(tempList);
			}
		}
		for(ArrayList<String> tempList: gameTreeList) {
			if(tempList.size()%2 == 0) {
				tempGameTreeList.add(tempList);
			}
		}
		gameTreeList = new ArrayList<ArrayList<String>>(tempGameTreeList); 
		
		ArrayList<String> allMatched = new ArrayList<String>();

		String result = "";
		String tempNextNode = null;
		ArrayList<String> subList = new ArrayList<String>();
		ArrayList<String> tempTreeList = new ArrayList<String>();
		boolean resultObtained = false;
		for(ArrayList<String> gameList: gameTreeList ) {
			if(gameList.size() == 2 && gameStart.equalsIgnoreCase("User")) {
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

					if(treeList.size()>=subList.size()) {
						for(int i=0;i<subList.size();i++) {
							if(!treeList.get(i).split("\\(")[0].equalsIgnoreCase(subList.get(i))) {
								matched = false;
								break;
							}
						}
					}else {
						matched = false;
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
							buffer.append(subList.get(i));
							if(i != subList.size()-1) {
								buffer.append("-->");
							}
						}
						result = result+buffer.toString();						
						resultObtained = true;
						break;
					}
				}
				if(subList.size() == 2 && gameStart.equalsIgnoreCase("User")) {
					break;
				}else if(subList.size() == 1 && gameStart.equalsIgnoreCase("Computer")) {
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
