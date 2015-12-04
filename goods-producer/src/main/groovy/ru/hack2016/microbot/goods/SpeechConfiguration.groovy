package ru.hack2016.microbot.goods

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import ru.hack2016.microbot.speechkit.SpeechRecognitor

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author tolkv
 * @since 04/12/15
 */
@Configuration
class SpeechConfiguration {
  @Bean
  @Lazy(false)
  SpeechRecognitor speechRecognitor() {
    new SpeechRecognitor('queries')
  }

  @Bean(name = 'bot.speech.pool')
  ExecutorService speechExecutorService() {
    Executors.newFixedThreadPool(1);
  }
}
