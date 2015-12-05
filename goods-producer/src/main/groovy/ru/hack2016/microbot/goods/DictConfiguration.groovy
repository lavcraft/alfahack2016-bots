package ru.hack2016.microbot.goods
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.hack2016.microbot.goods.search.*
/**
 * @author mgorelikov
 * @since 05/12/15
 */
@Configuration
class DictConfiguration {
  @Bean
  SentenceAnalyzer analyzer() {
    def indexer = new NGramIndexerM2(new UnionAlphabet(
        new SimpleAlphabet((char) 'А', (char) 'я'),
        new SimpleAlphabet((char) 'A', (char) 'z')))
    def list = getClass().getClassLoader().getResourceAsStream("goods_dict").readLines()
    def dict = list.toArray(new String[list.size()])
    def index = indexer.createIndex(dict)
    def searcher = new NGramSearcherM2(index, new DamerauLevensteinMetric(), 1, true)
    new NGrammSentenceAnalyzer(searcher, dict)
  }
}
