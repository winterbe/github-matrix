package com.winterbe.matrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Benjamin Winterberg
 */
@Component
public class GithubScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(GithubScheduler.class);

    private static final int MAX_CACHE_SIZE = 1000;

    @Autowired
    private Environment env;

    private ScheduledExecutorService scheduler;

    private GithubCollector collector;

    private ConcurrentMap<String, Drop> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void start() {
        String apiToken = env.getProperty("apiToken");
        Assert.notNull(apiToken, "apiToken must be present");
        collector = new GithubCollector(apiToken);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::collectLatest, 0, 10, TimeUnit.SECONDS);
        LOG.info("scheduler started");
    }

    @PreDestroy
    public void stop() {
        try {
            scheduler.shutdown();
            scheduler.awaitTermination(30, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        finally {
            LOG.info("scheduler stopped");
        }
    }

    private void collectLatest() {
        try {
            LOG.trace("start fetching latest commits");

            long t0 = System.nanoTime();

            int oldSize = cache.size();
            List<Drop> drops = collector.collect();
            for (Drop drop : drops) {
                cache.putIfAbsent(drop.getKey(), drop);
            }
            int newSize = cache.size();
            purgeCache();

            long t1 = System.nanoTime();
            long took = TimeUnit.NANOSECONDS.toSeconds(t1 - t0);

            LOG.info("done fetching {} commits. new cache size: {} [took {}s]", newSize - oldSize, cache.size(), took);
        }
        catch (Exception e) {
            LOG.error("failed to fetch github data", e);
        }
    }

    private void purgeCache() {
        if (cache.size() <= MAX_CACHE_SIZE) {
            return;
        }

        cache.values()
                .stream()
                .sorted(Comparator.reverseOrder())
                .skip(MAX_CACHE_SIZE)
                .forEach(c -> cache.remove(c.getKey()));
    }

    public List<Drop> fetch(int fetchSize) {
        List<Drop> drops = new ArrayList<>(cache.values());
        Collections.shuffle(drops);

        if (drops.size() < fetchSize) {
            return drops;
        }

        return drops
                .stream()
                .skip(drops.size() - fetchSize)
                .collect(Collectors.toList());
    }
}