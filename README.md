# hochzeit

A Clojure library designed to ... well, that part is up to you.

## Usage

```
crontab -e
```

```
# Run every 5 minutes
*/5 * * * * java -cp ~/hochzeit-0.1.2-SNAPSHOT-standalone.jar hochzeit.core https://vircurex.com/api/get_info_for_currency.xml ~/vircurex/
```

```
mvn install:install-file -DgroupId=org.joda \
-DartifactId=joda-money \
-Dversion=0.8.1 \
-Dfile=~/git/joda-money/target/joda-money-0.8.1-SNAPSHOT.jar \
-Dpackaging=jar \
-DgeneratePom=true

mvn install:install-file -DgroupId=clojurewerkz \
-DartifactId=money \
-Dversion=1.2.0 \
-Dfile=~/dev/money/target/money-1.2.0-SNAPSHOT-standalone.jar \
-Dpackaging=jar \
-DgeneratePom=true
```

## Running the App

Set up and start the server like this:

    $ lein deps
    $ lein ring server-headless 3000

Now, point your web browser at `http://localhost:3000`, and see the web app in action!


## Connecting chromium-browser to a REPL

First, in one terminal, start the Ring server:

    $ lein ring server-headless 3000

Now, in a different terminal:
    $ # lein cljsbuild clean; lein cljsbuild auto	# optionaly

    $ lein trampoline cljsbuild repl-launch chromium-browser http://localhost:3000/repl-demo

The REPL should start, and in a moment, Chromium should start up and browse to the `repl-demo`
page.  Viewing the source for `repl-demo`, you'll see that after loading the main JavaScript
file, it calls `example.repl.connect()`.  This function connects back to the REPL, thereby
allowing you to execute arbitrary ClojureScript code in the context of the `repl-demo` page.

There's also a launcher configured for a "naked" page.  This is just a simple static
HTML page that will connect to the REPL.  This is convenient when you want to run
a ClojureScript REPL with access to your project, but don't need a specific page to
be loaded at the time.  The biggest advantage to the "naked" launcher is that you don't
need to have your app running in the background:

    $ lein trampoline cljsbuild repl-launch firefox-naked

## Connecting PhantomJS to a REPL

To try out a PhantomJS-based REPL, first start the Ring server in one terminal:

    $ lein ring server-headless 3000

Now, in a different terminal, run `repl-launch` with the "phantom" identifier and the URL of the REPL demo page:

    $ lein trampoline cljsbuild repl-launch phantom http://localhost:3000/repl-demo

The REPL should start, and in a moment, PhantomJS should start up and browse to the `repl-demo`
page, in the background.  This is a convenient way to interact with your application in cases
where you don't need to open a full browser UI.

As with the Firefox example, there's a launch configured for a "naked" page.  This is probably
the most convenient way to launch a REPL when you just want to try running a couple snippets
of ClojureScript code.  As with the "firefox-naked" launcher, you don't need your app to be
running in the background:

    $ lein trampoline cljsbuild repl-launch phantom-naked

[1]: https://github.com/emezeske/lein-cljsbuild
[2]: https://github.com/mmcgrana/ring
[3]: https://github.com/weavejester/compojure
[4]: https://github.com/technomancy/leiningen



## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
