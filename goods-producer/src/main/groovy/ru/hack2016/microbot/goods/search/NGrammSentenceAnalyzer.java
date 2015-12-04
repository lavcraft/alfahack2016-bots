package ru.hack2016.microbot.goods.search;

import lombok.RequiredArgsConstructor;
import rx.Observable;

import java.util.Arrays;

/**
 * @author mgorelikov
 * @since 05/12/15
 */
@RequiredArgsConstructor
public class NGrammSentenceAnalyzer implements SentenceAnalyzer {
  private final WordSearcher searcher;
  private final String[] dict;

  @Override
  public Observable<String> parse(String sentence) {
    return Observable.create(subs -> {
      Arrays.stream(sentence.split(" "))
          .map(searcher::search)
          .filter(el -> el != null && !el.isEmpty())
          .map(set -> set.iterator().next())
          .map(pos -> dict[pos])
          .forEach(el -> subs.onNext(el));
      subs.onCompleted();

    });
  }
}
