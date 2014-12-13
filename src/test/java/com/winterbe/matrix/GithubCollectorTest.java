package com.winterbe.matrix;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class GithubCollectorTest {

    @Test
    public void testFetchLatestCommits() throws Exception {
        String apiToken = System.getProperty("apiToken");
        GithubCollector repo = new GithubCollector(apiToken);
        List<Drop> drops = repo.collect();
        assertThat(drops, not(empty()));
    }

}