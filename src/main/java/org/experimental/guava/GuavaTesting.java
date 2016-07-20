package org.experimental.guava;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.EventBus;
import org.junit.Test;

/**
 * Created by Matt on 1/30/2016.
 */
public class GuavaTesting {

    @Test
    public void stringTesting() {

        Map<String, String> map = new HashMap<>();
        map.put("localhost", "matt-laptop");
        map.put("10.20.1.60", "crm");
        map.put("10.20.1.11", "dns");
        map.put("10.20.5.60", null);

        /* Join a list of strings */
        Joiner joiner = Joiner.on(", ").useForNull("UNKNOWN");
        String result = joiner.join(map.values());
        System.out.println("Joiner=" + result);

        /* Join all keys and values in a map */
        MapJoiner mapJoiner = Joiner.on(", ").useForNull("UNKNOWN").withKeyValueSeparator(":");
        String mapResult = mapJoiner.join(map);
        System.out.println("MapJoiner=" + mapResult);

        /* Split a list of Strings into an Iterable */
        List<String> list = Splitter.on(",").trimResults().splitToList(result);
        System.out.println("Splitter=" + list);

        /* Pad out a String with chars before and after */
        String header = "Changelog";
        header = Strings.padStart(header, header.length() + 5, '*');
        header = Strings.padEnd(header, header.length() + 5, '*');
        System.out.println(header);
    }

    @Test
    public void cacheTesting() throws Exception {

        /* Build a cache that will expire an entry after 1 second */
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.SECONDS)
                .build();

        cache.put("localhost", "mattg-laptop");
        String cacheResult = cache.getIfPresent("localhost");
        System.out.println("Cache result=" + cacheResult);

        Thread.sleep(1200);
        // Need to run at least 1 read/write to evict the expired entry
        cache.put("10.20.1.60", "crm");

        // The expired entry is now gone
        cacheResult = cache.getIfPresent("localhost");
        System.out.println("Cache result after expiration=" + cacheResult);
    }

    @Test
    public void publishSubscribeTesting(){



        EventBus eventBus = new EventBus();

    }

    private class OldEventBus{

    }

    

}
