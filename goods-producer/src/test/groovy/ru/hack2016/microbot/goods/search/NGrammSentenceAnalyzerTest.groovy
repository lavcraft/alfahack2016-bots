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
    def indexer = new NGramIndexerM2(new SimpleAlphabet((char) 'А', (char) 'я'))
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("goods_dict")
    List<String> list = inputStream.readLines()
    dict = list.toArray(new String[list.size()])
    Index index = indexer.createIndex(dict)
    WordSearcher searcher = new NGramSearcherM2(index, new DamerauLevensteinMetric(), 2, true)
    analyzer = new NGrammSentenceAnalyzer( searcher, dict)
  }

  def 'should parse sentence' () {
    when:
    List<String> words = analyzer.parse('бот купи мне пива и сосисонов').toList().toBlocking().single()
    then:
    assert words.contains('пиво')
    assert words.contains('сосиски')
    assert words.size() == 2
  }
}
