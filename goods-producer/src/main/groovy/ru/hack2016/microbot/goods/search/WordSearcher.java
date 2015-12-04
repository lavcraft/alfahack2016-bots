package ru.hack2016.microbot.goods.search;

public abstract class WordSearcher implements Searcher {

	protected WordSearcher(Index index) {
		this.index = index;
	}

	public Index getIndex() {
		return index;
	}

	private final Index index;
}
