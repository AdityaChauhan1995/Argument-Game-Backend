package com.argumentGame.Game;

import java.util.ArrayList;
import java.util.HashMap;

public class ValidateResponse {

	public boolean result;
	public String win;
	public String message;
	public String exceptionMessage;
	public Nodes node;
	public Edges edge;
	public Integer proponentWinCount;
	public Integer opponentWinCount;	
	public Integer getProponentWinCount() {
		return proponentWinCount;
	}
	public void setProponentWinCount(Integer proponentWinCount) {
		this.proponentWinCount = proponentWinCount;
	}
	public Integer getOpponentWinCount() {
		return opponentWinCount;
	}
	public void setOpponentWinCount(Integer opponentWinCount) {
		this.opponentWinCount = opponentWinCount;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getWin() {
		return win;
	}
	public void setWin(String win) {
		this.win = win;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getExceptionMessage() {
		return exceptionMessage;
	}
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	public Nodes getNode() {
		return node;
	}
	public void setNode(Nodes node) {
		this.node = node;
	}
	public Edges getEdge() {
		return edge;
	}
	public void setEdge(Edges edge) {
		this.edge = edge;
	}
}
