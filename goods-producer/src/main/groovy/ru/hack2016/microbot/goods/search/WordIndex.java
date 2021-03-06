package ru.hack2016.microbot.goods.search;

public abstract class WordIndex implements Index {

	private static final long serialVersionUID = 1L;

	protected WordIndex(String[] dictionary) {
		this.dictionary = dictionary;
	}

	public String[] getDictionary() {
		return dictionary;
	}

	private final String[] dictionary;
}
