package ru.hack2016.microbot.goods.client;

import retrofit.http.GET;

/**
 * @author tolkv
 * @since 04/12/15
 */
public interface ItemsClient {
  @GET("/card/items")
  String sendItems();
}
