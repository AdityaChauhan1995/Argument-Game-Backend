package com.argumentGame.Game;

import java.util.ArrayList;
import java.util.HashMap;

public class ValidateRequest {

	public ArrayList<Nodes> nodes;
	public ArrayList<Edges> edges;
	public String initialNode;
	public String lastAddedNode;
	public ArrayList<ArrayList<String>> gameTreeList;
	public int proponentWinCount;
	public int opponentWinCount;
	public String gameStart;
	public String gameType;
	public ArrayList<Nodes> playedGameNodes;
	public ArrayList<Nodes> getPlayedGameNodes() {
		return playedGameNodes;
	}
	public void setPlayedGameNodes(ArrayList<Nodes> playedGameNodes) {
		this.playedGameNodes = playedGameNodes;
	}
	public String getGameType() {
		return gameType;
	}
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	public String getGameStart() {
		return gameStart;
	}
	public void setGameStart(String gameStart) {
		this.gameStart = gameStart;
	}
	public int getProponentWinCount() {
		return proponentWinCount;
	}
	public String getLastAddedNode() {
		return lastAddedNode;
	}
	public void setLastAddedNode(String lastAddedNode) {
		this.lastAddedNode = lastAddedNode;
	}
	public void setProponentWinCount(int proponentWinCount) {
		this.proponentWinCount = proponentWinCount;
	}
	public int getOpponentWinCount() {
		return opponentWinCount;
	}
	public void setOpponentWinCount(int opponentWinCount) {
		this.opponentWinCount = opponentWinCount;
	}
	public ArrayList<Nodes> getNodes() {
		return nodes;
	}
	public void setNodes(ArrayList<Nodes> nodes) {
		this.nodes = nodes;
	}
	public ArrayList<Edges> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<Edges> edges) {
		this.edges = edges;
	}
	public String getInitialNode() {
		return initialNode;
	}
	public void setInitialNode(String initialNode) {
		this.initialNode = initialNode;
	}
	public ArrayList<ArrayList<String>> getGameTreeList() {
		return gameTreeList;
	}
	public void setGameTreeList(ArrayList<ArrayList<String>> gameTreeList) {
		this.gameTreeList = gameTreeList;
	}
	
}
