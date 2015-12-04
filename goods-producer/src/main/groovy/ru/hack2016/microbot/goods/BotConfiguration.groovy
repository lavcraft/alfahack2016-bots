package ru.hack2016.microbot.goods

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.impl.BotApi
import com.pengrad.telegrambot.impl.FileApi
import com.squareup.okhttp.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit.RestAdapter
import retrofit.client.OkClient
import ru.hack2016.microbot.goods.bot.GoodsBotConfig

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static java.util.concurrent.TimeUnit.SECONDS

/**
 * @author tolkv
 * @since 04/12/15
 */
@Configuration
class BotConfiguration {
  @Autowired
  @Bean(name = "bot.goods.httpClient")
  OkHttpClient client(GoodsBotConfig botConfig) {
    def client = new OkHttpClient()
    client.setConnectTimeout(botConfig.getConnectionTimeout(), SECONDS)
    client.setReadTimeout(botConfig.getConnectionReadTimeout(), SECONDS)
    client.setWriteTimeout(botConfig.getConnectionWriteTimeout(), SECONDS)
    return client
  }

  @Bean
  @Autowired
  public TelegramBot buildBot(GoodsBotConfig goodsBotConfig,
                              @Qualifier('bot.goods.httpClient') OkHttpClient client,
                              BotApi botApi,
                              FileApi fileApi,
                              RestAdapter restAdapter) {
    new TelegramBot(botApi, fileApi);
  }

  @Bean
  private static FileApi fileApiClient(GoodsBotConfig goodsBotConfig) {
    FileApi fileApi = new FileApi(goodsBotConfig.token);
    fileApi
  }

  @Bean
  private static BotApi botRestClient(RestAdapter restAdapter) {
    BotApi botApi = restAdapter.create(BotApi.class);
    botApi
  }

  @Bean
  private static RestAdapter restAdaptor(GoodsBotConfig goodsBotConfig, OkHttpClient client) {
    RestAdapter restAdapter = prepare(goodsBotConfig.token, new OkClient(client)).build();
    restAdapter
  }

  @Bean(name = 'bot.goods.pool')
  ExecutorService botExecutorService() {
    Executors.newFixedThreadPool(1);
  }

  @Bean(name = 'bot.sensor.pool')
  ExecutorService sensorExecutorService() {
    Executors.newFixedThreadPool(1);
  }

  public static RestAdapter.Builder prepare(String botToken, OkClient client) {
    new RestAdapter.Builder()
        .setEndpoint("https://api.telegram.org/bot" + botToken)
        .setClient(client);
  }
}