package com.winterbe.matrix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * @author Benjamin Winterberg
 */
@RestController
public class MatrixController {

    @Autowired
    private GithubScheduler scheduler;

    @RequestMapping("fetch")
    public Collection<Drop> fetch(int fetchSize) {
        return scheduler.fetch(fetchSize);
    }

}