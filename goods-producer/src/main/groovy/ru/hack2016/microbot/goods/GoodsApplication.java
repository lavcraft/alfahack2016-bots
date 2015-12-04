package ru.hack2016.microbot.goods;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author tolkv
 * @since 04/12/15
 */
@SpringBootApplication
public class GoodsApplication {
  public static void main(String[] args) {
    new SpringApplicationBuilder(GoodsApplication.class)
        .web(false)
        .run(args);
  }
}
