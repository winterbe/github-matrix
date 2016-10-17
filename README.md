The GitHub Matrix
====================

<blockquote>The latest commits from GitHub visualized in a Matrix-style animation.</blockquote>

<img src="http://winterbe.com/image/matrix-has-you.gif">

The GitHub [Matrix](http://en.wikipedia.org/wiki/The_Matrix) shows a constant stream of recent commits from GitHub. Click on the drops to open the corresponding revision on GitHub. Use the pause/play button at the lower right corner to pause and resume the matrix animation (or press SPACE).

### http://winterbe.com/projects/github-matrix/

<img src="http://winterbe.com/image/matrix.png">

> You should [follow me on Twitter](https://twitter.com/winterbe_).

## Screensaver

The GitHub Matrix is also available as [Screensaver](https://github.com/winterbe/github-matrix-screensaver) for Mac OSX. Enjoy!

## Be your own operator

The GitHub Matrix is a [Spring Boot](http://projects.spring.io/spring-boot/) webapp written in Java 8. You need JDK 8 + Maven 3 preinstalled in order to run the app by yourself. [Fork](https://github.com/winterbe/github-matrix/fork) and clone the repository to your local machine, then `cd` into the project directory and run the following command:

```bash
$ echo "apiToken=YOUR_API_TOKEN" >> src/main/resources/application.properties 
$ mvn package
$ java -jar target/*.jar -XX:MaxMetaspaceSize=64m -Xmx256m -Djava.awt.headless=true
```

<blockquote>You have to create your own GitHub API Token. Go to your "GitHub Account Settings", choose "Personal access tokens" then click "Generate New Token". Make sure you don't accidentally push your API token to GitHub (as I did in the past). Alternatively just pass the token as system property: java ... -DapiToken=YOUR_API_TOKEN</blockquote>

## Compatibility

The GitHub Matrix frontend is written in JavaScript and HTML5 (Canvas). It's heavily tested and optimized for current desktop browser versions (Chrome, Firefox, Safari) and mobile iOS Safari + Android Chrome. If you find any issues related to Internet Explorer or other browsers, please let me know.

## Contribute

Feel free to [fork](https://github.com/winterbe/github-matrix/fork) this project and send me pull requests. You can also send me feedback via [Twitter](https://twitter.com/benontherun) or by [opening an issue](https://github.com/winterbe/github-matrix/issues).

## License

The source code is published under the MIT license. If you reuse parts of the code for your own projects please preserve information about me as original author visible in your application.
