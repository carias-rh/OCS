if type -p java; then
    echo Found java executable in PATH...
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo Found java executable in JAVA_HOME.
    _java="$JAVA_HOME/bin/java"
else
    echo "No java found. Please install it and try again!"
    exit 1
fi

if [[ "$_java" ]]; then
    echo This is good. Starting the Offline Tool...
    $_java -jar ./../lib/oct-offline-5.0.0.jar
fi
