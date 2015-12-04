package ru.hack2016.microbot.goods

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.hack2016.microbot.goods.search.DamerauLevensteinMetric
import ru.hack2016.microbot.goods.search.Index
import ru.hack2016.microbot.goods.search.NGramIndexerM2
import ru.hack2016.microbot.goods.search.NGramSearcherM2
import ru.hack2016.microbot.goods.search.NGrammSentenceAnalyzer
import ru.hack2016.microbot.goods.search.SentenceAnalyzer
import ru.hack2016.microbot.goods.search.SimpleAlphabet
import ru.hack2016.microbot.goods.search.WordSearcher

/**
 * @author mgorelikov
 * @since 05/12/15
 */
@Configuration
class DictConfiguration {
  @Bean
  SentenceAnalyzer analyzer() {
    def indexer = new NGramIndexerM2(new SimpleAlphabet((char) 'А', (char) 'я'))
    def list = getClass().getClassLoader().getResourceAsStream("goods_dict").readLines()
    def dict = list.toArray(new String[list.size()])
    def index = indexer.createIndex(dict)
    def searcher = new NGramSearcherM2(index, new DamerauLevensteinMetric(), 2, true)
    new NGrammSentenceAnalyzer(searcher, dict)
  }
}
