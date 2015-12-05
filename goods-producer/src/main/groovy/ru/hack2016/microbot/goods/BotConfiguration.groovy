package ru.hack2016.microbot.goods
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.impl.BotApi
import com.pengrad.telegrambot.impl.FileApi
import com.squareup.okhttp.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import retrofit.RestAdapter
import retrofit.client.OkClient
import ru.hack2016.microbot.goods.bot.GoodsBotConfig
import ru.hack2016.microbot.raspberry.LCDController
import ru.hack2016.microbot.raspberry.RaspberryLCDController
import ru.hack2016.microbot.goods.bot.RawBotConfig

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static java.util.concurrent.TimeUnit.SECONDS
/**
 * @author tolkv
 * @since 04/12/15
 */
@Configuration
class BotConfiguration {

    @Bean
    @Profile('sensor')
    LCDController lcdController() {
        new RaspberryLCDController()
    }

    @Bean
    @Profile('stub')
    LCDController lcdControllerStub() {
        new LCDController() {
            @Override
            void writeText(int i, String s) {

            }
        }
    }

  @Autowired
  @Bean(name = "bot.goods.httpClient")
  OkHttpClient client(GoodsBotConfig botConfig) {
      def client = new OkHttpClient()
      client.setConnectTimeout(botConfig.getConnectionTimeout(), SECONDS)
      client.setReadTimeout(botConfig.getConnectionReadTimeout(), SECONDS)
      client.setWriteTimeout(botConfig.getConnectionWriteTimeout(), SECONDS)
      return client
  }

  @Bean(name = "bot.goods.telegram")
  @Autowired
  public TelegramBot buildBot(GoodsBotConfig goodsBotConfig,
                              @Qualifier('bot.goods.httpClient') OkHttpClient client,
                              @Qualifier('bot.goods.botApi') BotApi botApi,
                              @Qualifier('bot.goods.fileApi') FileApi fileApi,
                              @Qualifier('bot.goods.restAdapter') RestAdapter restAdapter) {
    new TelegramBot(botApi, fileApi);
  }

  @Bean(name = "bot.goods.fileApi")
  private static FileApi fileApiClient(GoodsBotConfig goodsBotConfig) {
    FileApi fileApi = new FileApi(goodsBotConfig.token);
    fileApi
  }

  @Bean(name = "bot.goods.botApi")
  private static BotApi botRestClient(@Qualifier('bot.goods.restAdapter')RestAdapter restAdapter) {
    BotApi botApi = restAdapter.create(BotApi.class);
    botApi
  }

  @Bean(name = "bot.goods.restAdapter")
  private static RestAdapter restAdaptor(GoodsBotConfig goodsBotConfig,
                                         @Qualifier("bot.goods.httpClient") OkHttpClient client) {
    RestAdapter restAdapter = prepare(goodsBotConfig.token, new OkClient(client)).build();
    restAdapter
  }

    @Autowired
    @Bean(name = "bot.raw.httpClient")
    OkHttpClient rawClient(RawBotConfig botConfig) {
        def client = new OkHttpClient()
        client.setConnectTimeout(botConfig.getConnectionTimeout(), SECONDS)
        client.setReadTimeout(botConfig.getConnectionReadTimeout(), SECONDS)
        client.setWriteTimeout(botConfig.getConnectionWriteTimeout(), SECONDS)
        return client
    }

    @Bean(name = "bot.raw.telegram")
    @Autowired
    public TelegramBot buildRawBot(RawBotConfig rawBotConfig,
                       @Qualifier('bot.raw.httpClient') OkHttpClient client,
                       @Qualifier('bot.raw.botApi') BotApi botApi,
                       @Qualifier('bot.raw.fileApi') FileApi fileApi,
                       @Qualifier('bot.raw.restAdapter') RestAdapter restAdapter) {
        new TelegramBot(botApi, fileApi);
    }

    @Bean(name = "bot.raw.fileApi")
    private static FileApi rawFileApiClient(RawBotConfig rawBotConfig) {
        FileApi fileApi = new FileApi(rawBotConfig.token);
        fileApi
    }

    @Bean(name = "bot.raw.botApi")
    private static BotApi rawBotRestClient(@Qualifier('bot.raw.restAdapter') RestAdapter restAdapter) {
        BotApi botApi = restAdapter.create(BotApi.class);
        botApi
    }

    @Bean(name = "bot.raw.restAdapter")
    private static RestAdapter rawRestAdaptor(RawBotConfig rawBotConfig,
                                           @Qualifier("bot.raw.httpClient") OkHttpClient client) {
        RestAdapter restAdapter = prepare(rawBotConfig.token, new OkClient(client)).build();
        restAdapter
    }

    @Bean(name = 'bot.raw.pool')
    ExecutorService rawBotExecutorService() {
        Executors.newFixedThreadPool(1, ThreadFactoryBuilder.newInstance()
                .setNameFormat("raw-%d")
                .build());
    }

  @Bean(name = 'bot.goods.pool')
  ExecutorService botExecutorService() {
    Executors.newFixedThreadPool(1, ThreadFactoryBuilder.newInstance()
        .setNameFormat("goods-%d")
        .build());
  }

  @Bean(name = 'bot.sensor.pool')
  ExecutorService sensorExecutorService() {
    Executors.newFixedThreadPool(1, ThreadFactoryBuilder.newInstance()
        .setNameFormat("sensor-%d")
        .build());
  }

  @Bean(name = 'bot.goods.telegram.pool')
  ExecutorService telegramExecutorService() {
    Executors.newFixedThreadPool(3, ThreadFactoryBuilder.newInstance()
        .setNameFormat("telegram-goods-%d")
        .build());
  }

    @Bean(name = 'bot.raw.telegram.pool')
    ExecutorService rawTelegramExecutorService() {
        Executors.newFixedThreadPool(3, ThreadFactoryBuilder.newInstance()
                .setNameFormat("telegram-raw-%d")
                .build());
    }

  public static RestAdapter.Builder prepare(String botToken, OkClient client) {
    new RestAdapter.Builder()
        .setEndpoint("https://api.telegram.org/bot" + botToken)
        .setClient(client);
  }
}