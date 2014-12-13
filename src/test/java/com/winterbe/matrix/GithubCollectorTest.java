package com.winterbe.matrix;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class GithubCollectorTest {

    @Test
    public void testFetchLatestCommits() throws Exception {
        GithubCollector repo = new GithubCollector();
        List<Drop> drops = repo.collect();
        assertThat(drops, not(empty()));
    }

}