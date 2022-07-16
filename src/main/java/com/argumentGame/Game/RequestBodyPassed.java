package com.argumentGame.Game;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestBodyPassed {
	
	public ArrayList<Nodes> nodes;
	public ArrayList<Edges> edges;
	public String initialNode;
	public String gameType;
	public String gameStart;
	public HashMap<String,String> gameTreeMap;
	
	public String getGameStart() {
		return gameStart;
	}
	public void setGameStart(String gameStart) {
		this.gameStart = gameStart;
	}
	public String getGameType() {
		return gameType;
	}
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	
	public String getInitialNode() {
		return initialNode;
	}
	public void setInitialNode(String initialNode) {
		this.initialNode = initialNode;
	}
	public HashMap<String, String> getGameTreeMap() {
		return gameTreeMap;
	}
	public void setGameTreeMap(HashMap<String, String> gameTreeMap) {
		this.gameTreeMap = gameTreeMap;
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

}
