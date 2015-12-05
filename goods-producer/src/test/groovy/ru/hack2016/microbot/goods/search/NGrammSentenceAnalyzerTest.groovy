package ru.hack2016.microbot.goods.search

import spock.lang.Specification
import spock.lang.Subject

/**
 * @author mgorelikov
 * @since 05/12/15
 */
class NGrammSentenceAnalyzerTest extends Specification {
  @Subject
  SentenceAnalyzer analyzer;
  String[] dict;

  def setup() {
    def indexer = new NGramIndexerM2(new UnionAlphabet(new SimpleAlphabet((char) 'А', (char) 'я'), new SimpleAlphabet((char)'A', (char)'z')))
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("goods_dict")
    List<String> list = inputStream.readLines()
    dict = list.toArray(new String[list.size()])
    Index index = indexer.createIndex(dict)
    WordSearcher searcher = new NGramSearcherM2(index, new DamerauLevensteinMetric(), 1, true)
    analyzer = new NGrammSentenceAnalyzer( searcher, dict)
  }

  def 'should parse sentence' () {
    when:
    List<String> words = analyzer.parse('Закажи мне пиво и сосиски').toList().toBlocking().single()
    then:
    assert words.contains('Пиво')
    assert words.contains('Сосиски')
    assert words.size() == 2
  }
}
