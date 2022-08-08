# perepisun

FIXME: my new application.

## Installation

Download from https://github.com/perepisun/perepisun

## Usage

FIXME: explanation

Run the project directly, via `:exec-fn`:

    $ clojure -X:run-x
    Hello, Clojure!

Run the project, overriding the name to be greeted:

    $ clojure -X:run-x :name '"Someone"'
    Hello, Someone!

Run the project directly, via `:main-opts` (`-m perepisun.perepisun`):

    $ clojure -M:run-m
    Hello, World!

Run the project, overriding the name to be greeted:

    $ clojure -M:run-m Via-Main
    Hello, Via-Main!

Run the project's tests (they'll fail until you edit them):

    $ clojure -X:test

Build an uberjar:

    $ clojure -X:uberjar

This will update the generated `pom.xml` file to keep the dependencies synchronized with
your `deps.edn` file. You can update the version (and SCM tag) information in the `pom.xml` using the
`:version` argument:

    $ clojure -X:uberjar :version '"1.2.3"'

If you don't want the `pom.xml` file in your project, you can remove it, but you will
also need to remove `:sync-pom true` from the `deps.edn` file (in the `:exec-args` for `depstar`).

Run that uberjar:

    $ java -jar perepisun.jar


## How to run locally

install clojure, direnv, docker & compose

run ngrok server ~ngrok http 8859~

setup webhook:

    curl --header "Content-Type: application/json" \
        --data-raw "{\"url\":\"${WEBHOOK_CALLBACK_URI}/api/foo\"}" \
        -X POST "https://api.telegram.org/bot${BOT_API_KEY}/setWebhook"

It should respond with `{"ok":true,"result":true,"description":"Webhook was set"}`
Check it with:

    curl "https://api.telegram.org/bot${BOT_API_KEY}/getWebhookInfo"


# todos
- PAY FOR SERVER!
- implement missing handlers (start/stop)
- add more user friendly API
  - choose alphabet, set number of letters to write, timer with auto update of letters
- handle words (multiple letters)
- keep replies
- better help: tell that it requires permissions for reading messages in a group and how to set them;
- devops
  - prometheus metrics in the app (jetty, incoming with different statuses, response times)
  - prometheus metrics for vps
  - prometheus metrics for redis
  - prometheus metrics nginx
  - setup prometheus server
  - metrics for vps, nginx, app, http request to telegram API
- subscribe to messages only https://core.telegram.org/bots/api#update
- research if it's possible to `keep` **message** _formatting_
- integrate integrant
- refactor code for tests, so that it doesn't use real API and redis  
- more tests
- setup reloading dev env. DoD: when you changed the code you don't need to manually reload namespaces & webserver
- use core.async & async handlers. optionally collect several messages into one and send them in batch




