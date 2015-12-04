package ru.hack2016.microbot.goods

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import ru.hack2016.microbot.speechkit.SpeechRecognitor

/**
 * @author tolkv
 * @since 04/12/15
 */
@Configuration
class SpeechConfiguration {
  @Bean
  @Lazy(false)
  SpeechRecognitor speechRecognitor() {
    new SpeechRecognitor(10000)
  }
}
