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

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
