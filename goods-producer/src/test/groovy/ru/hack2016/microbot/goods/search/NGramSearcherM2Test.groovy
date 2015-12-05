package ru.hack2016.microbot.goods.search

import spock.lang.Specification
import spock.lang.Subject

/**
 * @author mgorelikov
 * @since 05/12/15
 */
class NGramSearcherM2Test extends Specification {
  @Subject
  WordSearcher searcher;
  String[] dict;

  def setup() {
    def indexer = new NGramIndexerM2(new SimpleAlphabet((char) 'А', (char) 'я'))
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("goods_dict")
    List<String> list = inputStream.readLines()
    dict = list.toArray(new String[list.size()])
    Index index = indexer.createIndex(dict)
    searcher = new NGramSearcherM2(index, new DamerauLevensteinMetric(), 5, true)
  }

  def 'should test search'() {
    expect:
    def res = searcher.search(text)
    result == res.stream().map() { code -> dict[code] }.findFirst().get()
    where:
    text       | result
    'сосисоны' | 'сосиски'
    'угропчег' | 'укроп'
    'мяско'    | 'мясо'
  }
}
