
package ru.hack2016.microbot.goods.search;

import java.util.Set;

/**
 * Поиск с предварительным индексированием.
 */
public interface Searcher {

	/**
	 * Метод выполняет поиск заданного слова string в словаре по предварительно сгенерированному индексу.
	 * 
	 * @param word
	 *            искомое слово
	 * @return множество индексов слов в словаре, соответствующих критерию поиска
	 */
	public Set<Integer> search(String word);

	/**
	 * Возвращает поисковый индекс, используемый при поиске.
	 * 
	 * @return поисковый индекс {@link Index}
	 */
	public Index getIndex();
}
