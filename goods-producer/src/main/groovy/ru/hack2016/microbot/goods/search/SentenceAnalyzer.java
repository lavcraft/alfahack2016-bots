package ru.hack2016.microbot.goods.search;

import rx.Observable;

/**
 * @author mgorelikov
 * @since 05/12/15
 */
public interface SentenceAnalyzer {
  /**
   * Returns observable or words extracted from phrase
   * @param sentence input sentence
   * @return observable of words
   */
  Observable<String> parse(String sentence);
}
