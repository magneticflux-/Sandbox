package com.sandbox.benchmarking;

public class HangmanGame {
	public String answer;
	public StringBuffer progress;
	public StringBuffer tried;

	public HangmanGame(String answer) {
		this.answer = answer;

		progress = new StringBuffer(answer.length());
		for (int i = 0; i < answer.length(); i++) {
			progress.append("-");
		}

		tried = new StringBuffer(26);
	}

	public String getWord() {
		return answer;
	}

	public String getGuessed() {
		return progress.toString();
	}

	public String getTried() {
		return tried.toString();
	}

	public int tryLetter(char letter) {
		if (tried.indexOf("" + letter) > -1) {
			return 0;
		} else {
			tried.append("" + letter);
			if (answer.indexOf("" + letter) > -1) {
				for (int i = 0; i < answer.length(); i++) {
					if (answer.charAt(i) == letter) {
						progress.setCharAt(i, letter);
					}
				}
				return 1;
			} else {
				return -1;
			}
		}
	}
}